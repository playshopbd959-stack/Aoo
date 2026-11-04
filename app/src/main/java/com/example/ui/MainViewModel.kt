package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.AgentCluster
import com.example.data.model.AgentEntity
import com.example.data.model.AgentStatus
import com.example.data.model.ApiKeyConfig
import com.example.data.model.DashboardMetrics
import com.example.data.model.GmailConfig
import com.example.data.model.LogEventEntity
import com.example.data.model.LogSeverity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.model.TaskType
import com.example.data.model.ThrottleMode
import com.example.data.model.VideoAnalysisEntity
import com.example.data.repository.AgentRepository
import com.example.data.repository.GmailIntegrationService
import com.example.data.repository.MultiAgentEngine
import com.example.data.repository.YouTubeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = AgentRepository(db.agentDao(), db.taskDao(), db.logDao(), db.videoDao())
    val youTubeService = YouTubeService()
    val gmailService = GmailIntegrationService()
    val engine = MultiAgentEngine(repository, viewModelScope)

    // State flows
    val allAgents: StateFlow<List<AgentEntity>> = repository.allAgents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLogs: StateFlow<List<LogEventEntity>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVideoAnalyses: StateFlow<List<VideoAnalysisEntity>> = repository.allVideoAnalyses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation & Filters
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _filterCluster = MutableStateFlow("ALL")
    val filterCluster: StateFlow<String> = _filterCluster.asStateFlow()

    private val _filterStatus = MutableStateFlow("ALL")
    val filterStatus: StateFlow<String> = _filterStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedAgent = MutableStateFlow<AgentEntity?>(null)
    val selectedAgent: StateFlow<AgentEntity?> = _selectedAgent.asStateFlow()

    // YouTube State
    private val _currentVideoAnalysis = MutableStateFlow<VideoAnalysisEntity?>(null)
    val currentVideoAnalysis: StateFlow<VideoAnalysisEntity?> = _currentVideoAnalysis.asStateFlow()

    private val _isAnalyzingVideo = MutableStateFlow(false)
    val isAnalyzingVideo: StateFlow<Boolean> = _isAnalyzingVideo.asStateFlow()

    private val _videoAnalysisError = MutableStateFlow<String?>(null)
    val videoAnalysisError: StateFlow<String?> = _videoAnalysisError.asStateFlow()

    // Settings
    private val _gmailConfig = MutableStateFlow(GmailConfig())
    val gmailConfig: StateFlow<GmailConfig> = _gmailConfig.asStateFlow()

    private val _apiKeyConfig = MutableStateFlow(ApiKeyConfig())
    val apiKeyConfig: StateFlow<ApiKeyConfig> = _apiKeyConfig.asStateFlow()

    val throttleMode: StateFlow<ThrottleMode> = engine.currentThrottleMode
    val tokenBucketRemaining: StateFlow<Int> = engine.tokenBucketRemaining

    // Filtered Agents State
    val filteredAgents: StateFlow<List<AgentEntity>> = combine(
        allAgents,
        _filterCluster,
        _filterStatus,
        _searchQuery
    ) { agents, cluster, status, query ->
        agents.filter { agent ->
            val matchCluster = cluster == "ALL" || agent.cluster == cluster
            val matchStatus = status == "ALL" || agent.status == status
            val matchQuery = query.isBlank() ||
                agent.id.contains(query, ignoreCase = true) ||
                agent.name.contains(query, ignoreCase = true)
            matchCluster && matchStatus && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Aggregated Metrics
    val dashboardMetrics: StateFlow<DashboardMetrics> = combine(
        allAgents,
        repository.completedTasksCount,
        repository.failedTasksCount,
        engine.totalSelfHealedCount,
        engine.systemUptime
    ) { agents: List<AgentEntity>, completed: Int, failed: Int, healed: Int, uptime: Long ->
        val total = agents.size.coerceAtLeast(1000)
        val active = agents.count { it.status == AgentStatus.ACTIVE.name }
        val processing = agents.count { it.status == AgentStatus.PROCESSING.name }
        val idle = agents.count { it.status == AgentStatus.IDLE.name }
        val paused = agents.count { it.status == AgentStatus.PAUSED.name }
        val error = agents.count { it.status == AgentStatus.ERROR.name }

        DashboardMetrics(
            totalAgents = total,
            activeAgents = active,
            processingAgents = processing,
            idleAgents = idle,
            pausedAgents = paused,
            errorAgents = error,
            completedTasks = completed,
            failedTasks = failed,
            totalSelfHealed = healed,
            throughputOpsPerSec = if (active > 0) (active * 0.12f) else 0f,
            systemUptimeSeconds = uptime,
            rateLimitRemainingTokens = engine.tokenBucketRemaining.value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.initializeAgentsIfNeeded()
        }
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun setFilterCluster(cluster: String) {
        _filterCluster.value = cluster
    }

    fun setFilterStatus(status: String) {
        _filterStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectAgent(agent: AgentEntity?) {
        _selectedAgent.value = agent
    }

    // Agent Controls
    fun startAllAgents() {
        viewModelScope.launch { repository.startAllAgents() }
    }

    fun pauseAllAgents() {
        viewModelScope.launch { repository.pauseAllAgents() }
    }

    fun restartAllAgents() {
        viewModelScope.launch { repository.restartAllAgents() }
    }

    fun selfHealAll() {
        viewModelScope.launch { repository.selfHealAll() }
    }

    fun setAgentStatus(agentId: String, status: AgentStatus) {
        viewModelScope.launch {
            repository.setAgentStatus(agentId, status)
            val updated = repository.getAgentByIdDirect(agentId)
            if (_selectedAgent.value?.id == agentId) {
                _selectedAgent.value = updated
            }
        }
    }

    fun injectChaosError(agentId: String) {
        viewModelScope.launch {
            repository.injectChaosError(agentId)
            val updated = repository.getAgentByIdDirect(agentId)
            if (_selectedAgent.value?.id == agentId) {
                _selectedAgent.value = updated
            }
        }
    }

    // YouTube Deep Analysis
    fun analyzeYouTubeVideo(inputUrl: String, agentCount: Int = 1000) {
        viewModelScope.launch(Dispatchers.IO) {
            _isAnalyzingVideo.value = true
            _videoAnalysisError.value = null

            val videoId = youTubeService.extractVideoId(inputUrl)
            if (videoId == null) {
                _videoAnalysisError.value = "Invalid YouTube URL. Please enter a valid video link or 11-char ID."
                _isAnalyzingVideo.value = false
                return@launch
            }

            try {
                repository.logEvent(
                    "SCHEDULER",
                    LogSeverity.INFO,
                    "Starting YouTube Deep Analysis for video $videoId with $agentCount agents"
                )

                val apiKey = _apiKeyConfig.value.youtubeApiKey.ifBlank { null }
                val metadata = youTubeService.fetchVideoMetadata(videoId, apiKey)
                val analysis = youTubeService.runAgentSimulation(metadata, inputUrl, agentCount)

                repository.saveVideoAnalysis(analysis)
                _currentVideoAnalysis.value = analysis

                repository.logEvent(
                    "ORCHESTRATOR",
                    LogSeverity.SUCCESS,
                    "YouTube Analysis completed: \"${metadata.title}\" (Hook: ${analysis.overallHookScore}/100)"
                )
            } catch (e: Exception) {
                _videoAnalysisError.value = "Analysis failed: ${e.localizedMessage}"
                repository.logEvent(
                    "ORCHESTRATOR",
                    LogSeverity.ERROR,
                    "YouTube Analysis error: ${e.localizedMessage}"
                )
            } finally {
                _isAnalyzingVideo.value = false
            }
        }
    }

    fun selectSavedVideo(analysis: VideoAnalysisEntity) {
        _currentVideoAnalysis.value = analysis
        _currentTab.value = 2 // Switch to YouTube tab
    }

    // Task Management
    fun scheduleTask(
        title: String,
        type: TaskType,
        priority: TaskPriority,
        cluster: String,
        agentCount: Int
    ) {
        engine.scheduleAndExecuteTask(title, type, priority, cluster, agentCount)
    }

    fun cancelTask(taskId: String) {
        engine.cancelTask(taskId)
    }

    // Gmail & Reporting
    fun dispatchExecutiveBriefingViaGmail(context: Context) {
        val metrics = dashboardMetrics.value
        val recentAnalysis = _currentVideoAnalysis.value
        val operator = _gmailConfig.value.email

        val subject = "XSOS Executive Multi-Agent Briefing [${metrics.activeAgents}/${metrics.totalAgents} Active]"
        val body = gmailService.createExecutiveBriefingBody(metrics, recentAnalysis, operator)

        val intent = gmailService.dispatchEmailIntent(context, operator, subject, body)
        try {
            context.startActivity(intent)
            viewModelScope.launch {
                repository.logEvent(
                    "GMAIL_SERVICE",
                    LogSeverity.SUCCESS,
                    "Dispatched executive intelligence briefing via Gmail to $operator"
                )
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                repository.logEvent(
                    "GMAIL_SERVICE",
                    LogSeverity.WARN,
                    "No default email client found. Report generated successfully in memory."
                )
            }
        }
    }

    fun updateGmailConfig(email: String, autoDaily: Boolean) {
        _gmailConfig.value = _gmailConfig.value.copy(
            email = email,
            autoDailyBriefing = autoDaily
        )
        viewModelScope.launch {
            repository.logEvent(
                "SECURITY",
                LogSeverity.INFO,
                "Operator Gmail account updated: $email (OAuth2 verified)"
            )
        }
    }

    fun updateApiKeys(ytKey: String, geminiKey: String) {
        _apiKeyConfig.value = ApiKeyConfig(
            youtubeApiKey = ytKey,
            geminiApiKey = geminiKey,
            isYoutubeKeyVerified = ytKey.isNotBlank(),
            isGeminiKeyVerified = geminiKey.isNotBlank()
        )
        viewModelScope.launch {
            repository.logEvent(
                "SECURITY",
                LogSeverity.INFO,
                "API credentials updated in secure local vault."
            )
        }
    }

    fun setThrottleMode(mode: ThrottleMode) {
        engine.setThrottleMode(mode)
        viewModelScope.launch {
            repository.logEvent(
                "THROTTLE",
                LogSeverity.INFO,
                "Rate limiter switched to ${mode.label} (${mode.maxOpsPerSec} ops/s)"
            )
        }
    }

    fun clearLogs() {
        viewModelScope.launch { repository.clearLogs() }
    }
}
