package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskType(val title: String) {
    YOUTUBE_DEEP_ANALYSIS("YouTube Deep Analysis"),
    SENTIMENT_SWEEP("Sentiment Resonance Sweep"),
    RETENTION_SIMULATION("Retention Timeline Simulation"),
    DEMOGRAPHIC_REACTION("Multi-Persona Viewer Reaction"),
    SEO_METADATA_AUDIT("SEO & CTR Algorithmic Audit"),
    GMAIL_BRIEFING("Executive Briefing Dispatch"),
    FULL_SYSTEM_SYNC("Full 1000-Agent Parallel Sync"),
    SELF_HEALING_AUDIT("Integrity & Self-Healing Audit")
}

enum class TaskPriority {
    CRITICAL, HIGH, NORMAL, LOW
}

enum class TaskStatus(val label: String) {
    QUEUED("Queued"),
    RUNNING("Running"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled")
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String, // UUID
    val title: String,
    val type: String, // TaskType name
    val priority: String, // TaskPriority name
    val status: String, // TaskStatus name
    val targetCluster: String, // ALL or specific cluster
    val allocatedAgentCount: Int,
    val progress: Float = 0f, // 0.0 to 1.0
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val executionTimeMs: Long = 0,
    val resultSummary: String? = null
)
