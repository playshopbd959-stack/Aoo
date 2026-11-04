package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AgentStatus(val label: String) {
    ACTIVE("Active"),
    PROCESSING("Processing"),
    IDLE("Idle"),
    PAUSED("Paused"),
    ERROR("Error")
}

enum class AgentCluster(val displayName: String, val description: String) {
    CONTENT_ANALYZER("Content Analyzer", "Pacing, narrative hook, transcript & scene structure"),
    SENTIMENT_OBSERVER("Sentiment Observer", "Audience emotional resonance & comment mood modeling"),
    RETENTION_PREDICTOR("Retention Predictor", "Drop-off timeline points & watch-duration curves"),
    DEMOGRAPHIC_SIMULATOR("Demographic Simulator", "Gen Z, tech professionals, casual and global personas"),
    SEO_SCOUT("SEO & Algo Scout", "Keyword density, metadata reach & thumbnail CTR index"),
    BRAND_SAFETY("Brand Safety Auditor", "Compliance, sentiment drift & claims verification"),
    DISPATCH_SYNTHESIS("Dispatch & Synthesis", "Briefing generation, Gmail alerts & telemetry roll-up")
}

@Entity(tableName = "agents")
data class AgentEntity(
    @PrimaryKey
    val id: String, // e.g., "AGT-0001" to "AGT-1000"
    val name: String,
    val cluster: String, // AgentCluster name
    val status: String,  // AgentStatus name
    val completedTasks: Int = 0,
    val failedTasks: Int = 0,
    val successRate: Float = 100.0f,
    val loadPercent: Int = 0,
    val currentTask: String = "Standby",
    val lastActive: Long = System.currentTimeMillis()
)
