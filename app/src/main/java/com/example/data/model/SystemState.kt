package com.example.data.model

enum class ThrottleMode(val label: String, val maxOpsPerSec: Int, val description: String) {
    SAFE("Safe Mode", 100, "100 ops/s - Strict Token-Bucket rate limiting"),
    BURST("Burst Mode", 500, "500 ops/s - High-concurrency parallel cluster bursts"),
    ECO("Eco Mode", 25, "25 ops/s - Minimal compute and background battery saving")
}

data class DashboardMetrics(
    val totalAgents: Int = 1000,
    val activeAgents: Int = 0,
    val processingAgents: Int = 0,
    val idleAgents: Int = 0,
    val pausedAgents: Int = 0,
    val errorAgents: Int = 0,
    val completedTasks: Int = 0,
    val failedTasks: Int = 0,
    val totalSelfHealed: Int = 0,
    val throughputOpsPerSec: Float = 0f,
    val systemUptimeSeconds: Long = 0,
    val rateLimitRemainingTokens: Int = 100
)

data class GmailConfig(
    val isConnected: Boolean = true,
    val email: String = "operator@xsos-core.internal",
    val authorizedScope: String = "gmail.send, profile, email",
    val autoDailyBriefing: Boolean = true,
    val lastBriefingTimestamp: Long = System.currentTimeMillis() - 86400000L
)

data class ApiKeyConfig(
    val youtubeApiKey: String = "",
    val geminiApiKey: String = "",
    val isYoutubeKeyVerified: Boolean = false,
    val isGeminiKeyVerified: Boolean = false
)
