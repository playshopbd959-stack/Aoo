package com.example.data.repository

import com.example.data.database.AccountDao
import com.example.data.database.AgentDao
import com.example.data.database.LogDao
import com.example.data.database.TaskDao
import com.example.data.database.VideoDao
import com.example.data.model.AgentCluster
import com.example.data.model.AgentEntity
import com.example.data.model.AgentStatus
import com.example.data.model.GoogleAccountEntity
import com.example.data.model.LogEventEntity
import com.example.data.model.LogSeverity
import com.example.data.model.TaskEntity
import com.example.data.model.VideoAnalysisEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.random.Random

class AgentRepository(
    private val agentDao: AgentDao,
    private val taskDao: TaskDao,
    private val logDao: LogDao,
    private val videoDao: VideoDao,
    private val accountDao: AccountDao
) {
    val allAgents: Flow<List<AgentEntity>> = agentDao.getAllAgents()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val recentLogs: Flow<List<LogEventEntity>> = logDao.getRecentLogs(100)
    val allVideoAnalyses: Flow<List<VideoAnalysisEntity>> = videoDao.getAllAnalyses()
    val allAccounts: Flow<List<GoogleAccountEntity>> = accountDao.getAllAccounts()

    val completedTasksCount: Flow<Int> = taskDao.countCompletedTasks()
    val failedTasksCount: Flow<Int> = taskDao.countFailedTasks()

    val workingCount: Flow<Int> = agentDao.countByStatus(AgentStatus.WORKING.name)
    val pausedCount: Flow<Int> = agentDao.countByStatus(AgentStatus.PAUSED.name)
    val failedCount: Flow<Int> = agentDao.countByStatus(AgentStatus.FAILED.name)
    val idleCount: Flow<Int> = agentDao.countByStatus(AgentStatus.IDLE.name)
    val completedCount: Flow<Int> = agentDao.countByStatus(AgentStatus.COMPLETED.name)

    suspend fun allAgentsSnapshot(): List<AgentEntity> = withContext(Dispatchers.IO) {
        agentDao.getAllAgentsList()
    }

    suspend fun initializeAgentsIfNeeded() = withContext(Dispatchers.IO) {
        val existingCount = agentDao.countTotal()
        if (existingCount < 1000) {
            val agents = mutableListOf<AgentEntity>()
            val namePrefixes = listOf(
                "Aegis", "Nexus", "Cipher", "Vanguard", "Krypton", "Apex", "Echo", "Orion",
                "Titan", "Ghost", "Chronos", "Helios", "Vector", "Pulse", "Shadow", "Atlas",
                "Zephyr", "Sol", "Vortex", "Sentry", "Nova", "Obsidian", "Hydra", "Zenith"
            )

            for (i in 1..1000) {
                val id = String.format(Locale.US, "AGT-%04d", i)
                val cluster = when (i) {
                    in 1..200 -> AgentCluster.CONTENT_ANALYZER
                    in 201..350 -> AgentCluster.SENTIMENT_OBSERVER
                    in 351..500 -> AgentCluster.RETENTION_PREDICTOR
                    in 501..650 -> AgentCluster.DEMOGRAPHIC_SIMULATOR
                    in 651..800 -> AgentCluster.SEO_SCOUT
                    in 801..900 -> AgentCluster.BRAND_SAFETY
                    else -> AgentCluster.DISPATCH_SYNTHESIS
                }

                val prefix = namePrefixes[(i - 1) % namePrefixes.size]
                val subNumber = ((i - 1) / namePrefixes.size) + 1
                val name = "$prefix-$subNumber"

                agents.add(
                    AgentEntity(
                        id = id,
                        name = name,
                        cluster = cluster.name,
                        status = AgentStatus.IDLE.name,
                        completedTasks = 0,
                        failedTasks = 0,
                        successRate = 100.0f,
                        loadPercent = 0,
                        currentTask = "Standby telemetry node",
                        lastActive = System.currentTimeMillis()
                    )
                )
            }

            // Insert in chunks of 250 for speed and transaction bounds
            agents.chunked(250).forEach { chunk ->
                agentDao.insertAll(chunk)
            }

            logDao.insertLog(
                LogEventEntity(
                    agentId = "SYSTEM",
                    severity = LogSeverity.INFO.name,
                    message = "XSOS Real Multi-Agent V2 initialized. 1,000 discrete agents registered in database."
                )
            )
        }

        // Initialize default primary Google Account if empty
        if (accountDao.countAccounts() == 0) {
            accountDao.insertAccount(
                GoogleAccountEntity(
                    email = "sayumnahian@gmail.com",
                    displayName = "Sayum Nahian (Operator)",
                    authorizedScopes = "https://www.googleapis.com/auth/youtube.readonly, https://www.googleapis.com/auth/gmail.send",
                    isConnected = true,
                    accountType = "Verified Google OAuth Account"
                )
            )
        }
    }

    suspend fun getAgentIdsForTask(cluster: String, count: Int): List<String> = withContext(Dispatchers.IO) {
        val all = if (cluster == "ALL") {
            agentDao.getAllAgentIds()
        } else {
            agentDao.getAgentsByCluster(cluster).let { agentDao.getAllAgentsList().filter { it.cluster == cluster }.map { it.id } }
        }
        all.take(count)
    }

    suspend fun updateAgentExecution(id: String, status: String, task: String, load: Int, output: String?) = withContext(Dispatchers.IO) {
        agentDao.updateAgentExecution(id, status, task, load, output)
    }

    suspend fun recordAgentSuccess(id: String, output: String) = withContext(Dispatchers.IO) {
        agentDao.recordTaskSuccess(id, output)
        logDao.insertLog(
            LogEventEntity(
                agentId = id,
                severity = LogSeverity.SUCCESS.name,
                message = "Agent $id completed calculation: $output"
            )
        )
    }

    suspend fun recordAgentFailure(id: String, errorMsg: String) = withContext(Dispatchers.IO) {
        agentDao.recordTaskFailure(id, errorMsg)
        logDao.insertLog(
            LogEventEntity(
                agentId = id,
                severity = LogSeverity.ERROR.name,
                message = "Agent $id failed execution: $errorMsg"
            )
        )
    }

    fun getAgentsByCluster(cluster: String): Flow<List<AgentEntity>> = agentDao.getAgentsByCluster(cluster)
    fun getAgentsByStatus(status: String): Flow<List<AgentEntity>> = agentDao.getAgentsByStatus(status)
    fun searchAgents(query: String): Flow<List<AgentEntity>> = agentDao.searchAgents(query)
    fun getAgentById(id: String): Flow<AgentEntity?> = agentDao.getAgentById(id)
    suspend fun getAgentByIdDirect(id: String): AgentEntity? = agentDao.getAgentByIdDirect(id)

    suspend fun setAgentStatus(id: String, status: AgentStatus, task: String = "Manual Override") = withContext(Dispatchers.IO) {
        agentDao.updateAgentStatus(id, status.name, task)
        logDao.insertLog(
            LogEventEntity(
                agentId = id,
                severity = if (status == AgentStatus.FAILED) LogSeverity.ERROR.name else LogSeverity.INFO.name,
                message = "Agent $id state changed to ${status.label} ($task)"
            )
        )
    }

    suspend fun startAllAgents() = withContext(Dispatchers.IO) {
        agentDao.updateAllStatus(AgentStatus.IDLE.name, "Active monitoring")
        logDao.insertLog(
            LogEventEntity(
                agentId = "ORCHESTRATOR",
                severity = LogSeverity.SUCCESS.name,
                message = "All 1,000 agents set to IDLE / ready state."
            )
        )
    }

    suspend fun pauseAllAgents() = withContext(Dispatchers.IO) {
        agentDao.updateAllStatus(AgentStatus.PAUSED.name, "System Paused")
        logDao.insertLog(
            LogEventEntity(
                agentId = "ORCHESTRATOR",
                severity = LogSeverity.WARN.name,
                message = "All agents globally PAUSED by operator."
            )
        )
    }

    suspend fun restartAllAgents() = withContext(Dispatchers.IO) {
        agentDao.updateAllStatus(AgentStatus.IDLE.name, "Subsystem Rebooted")
        logDao.insertLog(
            LogEventEntity(
                agentId = "ORCHESTRATOR",
                severity = LogSeverity.INFO.name,
                message = "Full 1,000 agent system reset & reboot sequence executed."
            )
        )
    }

    suspend fun selfHealAll(): Int = withContext(Dispatchers.IO) {
        val recovered = agentDao.recoverAllErrors()
        if (recovered > 0) {
            logDao.insertLog(
                LogEventEntity(
                    agentId = "WATCHDOG",
                    severity = LogSeverity.RECOVERY.name,
                    message = "Auto-recovery watchdog restored $recovered agents from failure state."
                )
            )
        }
        recovered
    }

    suspend fun injectChaosError(id: String) = withContext(Dispatchers.IO) {
        agentDao.recordTaskFailure(id, "Simulated memory buffer overflow / timeout")
        logDao.insertLog(
            LogEventEntity(
                agentId = id,
                severity = LogSeverity.ERROR.name,
                message = "Chaos injection: Agent $id threw timeout/buffer exception."
            )
        )
    }

    suspend fun logEvent(agentId: String, severity: LogSeverity, message: String) = withContext(Dispatchers.IO) {
        logDao.insertLog(
            LogEventEntity(
                agentId = agentId,
                severity = severity.name,
                message = message
            )
        )
    }

    suspend fun clearLogs() = withContext(Dispatchers.IO) {
        logDao.clearLogs()
    }

    suspend fun insertTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }

    suspend fun updateTaskProgress(
        id: String,
        progress: Float,
        status: String,
        completedAt: Long? = null,
        execTime: Long = 0,
        result: String? = null
    ) = withContext(Dispatchers.IO) {
        taskDao.updateTaskProgress(id, progress, status, completedAt, execTime, result)
    }

    suspend fun saveVideoAnalysis(analysis: VideoAnalysisEntity) = withContext(Dispatchers.IO) {
        videoDao.insertAnalysis(analysis)
    }

    fun getVideoAnalysis(id: String): Flow<VideoAnalysisEntity?> = videoDao.getAnalysisById(id)

    // Multi-Account Operations
    suspend fun addGoogleAccount(account: GoogleAccountEntity) = withContext(Dispatchers.IO) {
        accountDao.insertAccount(account)
        logDao.insertLog(
            LogEventEntity(
                agentId = "OAUTH_GATEWAY",
                severity = LogSeverity.SUCCESS.name,
                message = "Authorized Google Account added: ${account.email}"
            )
        )
    }

    suspend fun removeGoogleAccount(email: String) = withContext(Dispatchers.IO) {
        accountDao.deleteAccount(email)
        logDao.insertLog(
            LogEventEntity(
                agentId = "OAUTH_GATEWAY",
                severity = LogSeverity.WARN.name,
                message = "Google Account disconnected: $email"
            )
        )
    }
}
