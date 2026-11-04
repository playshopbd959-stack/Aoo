package com.example.data.repository

import com.example.data.database.AgentDao
import com.example.data.database.LogDao
import com.example.data.database.TaskDao
import com.example.data.database.VideoDao
import com.example.data.model.AgentCluster
import com.example.data.model.AgentEntity
import com.example.data.model.AgentStatus
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
    private val videoDao: VideoDao
) {
    val allAgents: Flow<List<AgentEntity>> = agentDao.getAllAgents()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val recentLogs: Flow<List<LogEventEntity>> = logDao.getRecentLogs(100)
    val allVideoAnalyses: Flow<List<VideoAnalysisEntity>> = videoDao.getAllAnalyses()

    val completedTasksCount: Flow<Int> = taskDao.countCompletedTasks()
    val failedTasksCount: Flow<Int> = taskDao.countFailedTasks()

    val activeCount: Flow<Int> = agentDao.countByStatus(AgentStatus.ACTIVE.name)
    val pausedCount: Flow<Int> = agentDao.countByStatus(AgentStatus.PAUSED.name)
    val errorCount: Flow<Int> = agentDao.countByStatus(AgentStatus.ERROR.name)
    val idleCount: Flow<Int> = agentDao.countByStatus(AgentStatus.IDLE.name)
    val processingCount: Flow<Int> = agentDao.countByStatus(AgentStatus.PROCESSING.name)

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

                // Initial state
                val initialStatus = if (i % 25 == 0) AgentStatus.IDLE.name else AgentStatus.ACTIVE.name
                val completed = Random.nextInt(20, 180)
                val failed = if (Random.nextInt(10) > 7) Random.nextInt(1, 4) else 0
                val successRate = if (completed + failed > 0) {
                    ((completed.toFloat() / (completed + failed)) * 100f).coerceIn(85f, 100f)
                } else 100f

                agents.add(
                    AgentEntity(
                        id = id,
                        name = name,
                        cluster = cluster.name,
                        status = initialStatus,
                        completedTasks = completed,
                        failedTasks = failed,
                        successRate = String.format(Locale.US, "%.1f", successRate).toFloat(),
                        loadPercent = Random.nextInt(5, 45),
                        currentTask = "Standby telemetry node",
                        lastActive = System.currentTimeMillis() - Random.nextLong(1000, 60000)
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
                    message = "XSOS Core initialized. 1,000 independent multi-agents online across 7 clusters."
                )
            )
        }
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
                severity = if (status == AgentStatus.ERROR) LogSeverity.ERROR.name else LogSeverity.INFO.name,
                message = "Agent $id state changed to ${status.label} ($task)"
            )
        )
    }

    suspend fun updateAgent(agent: AgentEntity) = withContext(Dispatchers.IO) {
        agentDao.updateAgent(agent)
    }

    suspend fun startAllAgents() = withContext(Dispatchers.IO) {
        agentDao.updateAllStatus(AgentStatus.ACTIVE.name, "Active monitoring")
        logDao.insertLog(
            LogEventEntity(
                agentId = "ORCHESTRATOR",
                severity = LogSeverity.SUCCESS.name,
                message = "All 1,000 agents transitioned to ACTIVE state."
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
        agentDao.updateAllStatus(AgentStatus.ACTIVE.name, "Subsystem Rebooted")
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
                    message = "Auto-recovery watchdog restored $recovered agents from ERROR state."
                )
            )
        }
        recovered
    }

    suspend fun injectChaosError(id: String) = withContext(Dispatchers.IO) {
        agentDao.updateAgentStatus(id, AgentStatus.ERROR.name, "Simulated memory buffer overflow")
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

    suspend fun getRandomAvailableAgents(limit: Int): List<AgentEntity> = withContext(Dispatchers.IO) {
        agentDao.getRandomAvailableAgents(limit)
    }

    suspend fun saveVideoAnalysis(analysis: VideoAnalysisEntity) = withContext(Dispatchers.IO) {
        videoDao.insertAnalysis(analysis)
    }

    fun getVideoAnalysis(id: String): Flow<VideoAnalysisEntity?> = videoDao.getAnalysisById(id)
}
