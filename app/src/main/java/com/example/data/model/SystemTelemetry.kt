package com.example.data.model

data class SystemTelemetry(
    val usedMemoryMb: Long = 0,
    val totalMemoryMb: Long = 0,
    val maxMemoryMb: Long = 0,
    val availableProcessors: Int = 1,
    val activeWorkersCount: Int = 0,
    val queuedWorkItemsCount: Int = 0,
    val totalAgentsInitialized: Int = 0,
    val workingAgentsCount: Int = 0,
    val idleAgentsCount: Int = 0,
    val failedAgentsCount: Int = 0,
    val completedAgentsCount: Int = 0,
    val pausedAgentsCount: Int = 0,
    val all1000Verified: Boolean = false,
    val systemUptimeSeconds: Long = 0,
    val throughputOpsPerSec: Float = 0f
)
