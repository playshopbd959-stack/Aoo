package com.example.data.repository

import com.example.data.model.AgentCluster
import com.example.data.model.AgentStatus
import com.example.data.model.LogSeverity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.data.model.TaskType
import com.example.data.model.ThrottleMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class MultiAgentEngine(
    private val repository: AgentRepository,
    private val scope: CoroutineScope
) {
    private var telemetryJob: Job? = null
    private var watchdogJob: Job? = null
    private val activeTaskJobs = mutableMapOf<String, Job>()

    private val _currentThrottleMode = MutableStateFlow(ThrottleMode.SAFE)
    val currentThrottleMode: StateFlow<ThrottleMode> = _currentThrottleMode.asStateFlow()

    private val _tokenBucketRemaining = MutableStateFlow(100)
    val tokenBucketRemaining: StateFlow<Int> = _tokenBucketRemaining.asStateFlow()

    private val _systemUptime = MutableStateFlow(0L)
    val systemUptime: StateFlow<Long> = _systemUptime.asStateFlow()

    private val _totalSelfHealedCount = MutableStateFlow(0)
    val totalSelfHealedCount: StateFlow<Int> = _totalSelfHealedCount.asStateFlow()

    init {
        startTelemetryLoop()
        startWatchdogLoop()
        startUptimeCounter()
    }

    fun setThrottleMode(mode: ThrottleMode) {
        _currentThrottleMode.value = mode
        _tokenBucketRemaining.value = mode.maxOpsPerSec
    }

    private fun startUptimeCounter() {
        scope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(1000)
                _systemUptime.value += 1
                // Refill token bucket every second
                _tokenBucketRemaining.value = _currentThrottleMode.value.maxOpsPerSec
            }
        }
    }

    /**
     * Continuous live agent activity generator simulating the independent actions
     * of the 1,000 agents in the background.
     */
    private fun startTelemetryLoop() {
        telemetryJob?.cancel()
        telemetryJob = scope.launch(Dispatchers.Default) {
            val sampleActions = listOf(
                "evaluated audio spectrum pacing" to LogSeverity.INFO,
                "scored sentiment valence on comment cluster" to LogSeverity.INFO,
                "verified demographic retention hold at 01:30" to LogSeverity.INFO,
                "parsed semantic keyword density" to LogSeverity.INFO,
                "checked content rating and safety heuristics" to LogSeverity.INFO,
                "optimized internal prompt tokens for analysis" to LogSeverity.INFO,
                "completed parallel micro-batch calculation" to LogSeverity.SUCCESS,
                "minor latency spike detected in worker pool" to LogSeverity.WARN
            )

            while (isActive) {
                delay(Random.nextLong(2500, 5000))
                val randomAgentNum = Random.nextInt(1, 1001)
                val agentId = String.format("AGT-%04d", randomAgentNum)
                val (action, severity) = sampleActions.random()

                // Check rate limit tokens
                if (_tokenBucketRemaining.value > 0) {
                    _tokenBucketRemaining.value -= 1
                    repository.logEvent(agentId, severity, "Agent $agentId $action")
                }
            }
        }
    }

    /**
     * Automatic recovery watchdog:
     * Scans for agents in ERROR state and automatically self-heals them.
     */
    private fun startWatchdogLoop() {
        watchdogJob?.cancel()
        watchdogJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(8000) // check every 8 seconds
                val healed = repository.selfHealAll()
                if (healed > 0) {
                    _totalSelfHealedCount.value += healed
                }
            }
        }
    }

    /**
     * AI-Powered Task Scheduler & Parallel Task Execution:
     * Allocates agents and runs parallel work stages with real-time progress updates.
     */
    fun scheduleAndExecuteTask(
        title: String,
        type: TaskType,
        priority: TaskPriority,
        targetCluster: String,
        agentCount: Int
    ): String {
        val taskId = UUID.randomUUID().toString()
        val initialTask = TaskEntity(
            id = taskId,
            title = title,
            type = type.name,
            priority = priority.name,
            status = TaskStatus.QUEUED.name,
            targetCluster = targetCluster,
            allocatedAgentCount = agentCount.coerceIn(1, 1000),
            progress = 0f,
            createdAt = System.currentTimeMillis()
        )

        scope.launch(Dispatchers.IO) {
            repository.insertTask(initialTask)
            repository.logEvent(
                "SCHEDULER",
                LogSeverity.INFO,
                "Task scheduled: $title ($agentCount agents in $targetCluster)"
            )
            executeParallelTask(initialTask)
        }

        return taskId
    }

    private suspend fun executeParallelTask(task: TaskEntity) {
        val startTime = System.currentTimeMillis()
        repository.updateTaskProgress(task.id, 0.05f, TaskStatus.RUNNING.name)

        val job = scope.launch(Dispatchers.Default) {
            try {
                val totalSteps = 10
                for (step in 1..totalSteps) {
                    val progress = step.toFloat() / totalSteps.toFloat()
                    val delayMs = when (_currentThrottleMode.value) {
                        ThrottleMode.BURST -> 200L
                        ThrottleMode.SAFE -> 500L
                        ThrottleMode.ECO -> 1000L
                    }
                    delay(delayMs)

                    val statusMsg = when (step) {
                        2 -> "Allocating ${task.allocatedAgentCount} agent worker threads in cluster ${task.targetCluster}..."
                        4 -> "Distributing parallel micro-shards across agent pool..."
                        6 -> "Synthesizing intermediate telemetry vectors..."
                        8 -> "Aggregating consensus and validating against safety bounds..."
                        else -> "Processing parallel batch ($step/$totalSteps)..."
                    }

                    repository.updateTaskProgress(
                        id = task.id,
                        progress = progress,
                        status = TaskStatus.RUNNING.name,
                        result = statusMsg
                    )

                    // Consume rate limit tokens
                    val tokensToDeduct = (task.allocatedAgentCount / 100).coerceAtLeast(1)
                    _tokenBucketRemaining.value = (_tokenBucketRemaining.value - tokensToDeduct).coerceAtLeast(0)
                }

                val execTime = System.currentTimeMillis() - startTime
                val finalResult = "Successfully executed across ${task.allocatedAgentCount} agents in ${execTime}ms. 100% consensus reached."

                repository.updateTaskProgress(
                    id = task.id,
                    progress = 1.0f,
                    status = TaskStatus.COMPLETED.name,
                    completedAt = System.currentTimeMillis(),
                    execTime = execTime,
                    result = finalResult
                )

                repository.logEvent(
                    "ORCHESTRATOR",
                    LogSeverity.SUCCESS,
                    "Task [${task.title}] completed by ${task.allocatedAgentCount} agents in ${execTime}ms"
                )
            } catch (e: Exception) {
                val execTime = System.currentTimeMillis() - startTime
                repository.updateTaskProgress(
                    id = task.id,
                    progress = 0f,
                    status = TaskStatus.FAILED.name,
                    completedAt = System.currentTimeMillis(),
                    execTime = execTime,
                    result = "Execution failed: ${e.localizedMessage ?: "Unknown error"}"
                )
                repository.logEvent(
                    "ORCHESTRATOR",
                    LogSeverity.ERROR,
                    "Task [${task.title}] failed: ${e.localizedMessage}"
                )
            }
        }
        activeTaskJobs[task.id] = job
    }

    fun cancelTask(taskId: String) {
        activeTaskJobs[taskId]?.cancel()
        activeTaskJobs.remove(taskId)
        scope.launch(Dispatchers.IO) {
            repository.updateTaskProgress(
                id = taskId,
                progress = 0f,
                status = TaskStatus.CANCELLED.name,
                completedAt = System.currentTimeMillis(),
                result = "Cancelled by operator"
            )
            repository.logEvent("OPERATOR", LogSeverity.WARN, "Task $taskId cancelled manually.")
        }
    }
}
