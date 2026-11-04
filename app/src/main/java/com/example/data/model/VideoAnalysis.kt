package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class RetentionPoint(
    val second: Int,
    val retentionPercent: Int,
    val note: String? = null
)

data class PersonaSimulation(
    val persona: String,
    val sentiment: String, // "POSITIVE", "NEUTRAL", "SKEPTICAL", "ENTHUSIASTIC"
    val score: Int, // 0..100
    val expectedCompletion: Int, // 0..100%
    val behavioralFeedback: String
)

data class CommentCluster(
    val theme: String,
    val percentage: Int,
    val sentimentTone: String,
    val sampleSummary: String
)

@Entity(tableName = "video_analyses")
data class VideoAnalysisEntity(
    @PrimaryKey
    val id: String, // video ID e.g. "dQw4w9WgXcQ"
    val originalUrl: String,
    val title: String,
    val channelTitle: String,
    val thumbnailUrl: String,
    val viewCount: Long,
    val likeCount: Long,
    val commentCount: Long,
    val durationText: String,
    val overallHookScore: Int, // 0..100
    val viralPotentialScore: Int, // 0..100
    val sentimentRatio: Float, // 0.0 to 1.0
    val agentsInvolvedCount: Int,
    val retentionJson: String, // serialized list of retention points
    val personasJson: String,  // serialized list of persona simulations
    val commentClustersJson: String, // serialized clusters
    val summaryReport: String,
    val analyzedAt: Long = System.currentTimeMillis()
)
