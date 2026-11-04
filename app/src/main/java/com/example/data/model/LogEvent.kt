package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LogSeverity {
    INFO, SUCCESS, WARN, ERROR, RECOVERY
}

@Entity(tableName = "system_logs")
data class LogEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val agentId: String,
    val severity: String, // LogSeverity name
    val message: String
)
