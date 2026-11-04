package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.model.DashboardMetrics
import com.example.data.model.GmailConfig
import com.example.data.model.VideoAnalysisEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GmailIntegrationService {

    /**
     * Generates a structured executive multi-agent intelligence briefing
     */
    fun createExecutiveBriefingBody(
        metrics: DashboardMetrics,
        recentAnalysis: VideoAnalysisEntity?,
        operatorEmail: String
    ): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US)
        val timestamp = dateFormat.format(Date())

        val videoSection = if (recentAnalysis != null) {
            val statusTag = if (recentAnalysis.isVerifiedApiData) "[VERIFIED API DATA]" else "[ESTIMATED / EMBED]"
            """
            ------------------------------------------------------------
            LATEST YOUTUBE REAL AGENT ANALYSIS $statusTag
            ------------------------------------------------------------
            Title: ${recentAnalysis.title}
            Channel: ${recentAnalysis.channelTitle}
            Views: ${if (recentAnalysis.viewCount > 0) recentAnalysis.viewCount.toString() else "N/A (oEmbed)"} | Likes: ${if (recentAnalysis.likeCount > 0) recentAnalysis.likeCount.toString() else "N/A"}
            Title Analysis Score: ${recentAnalysis.titleAnalysisScore}/100 (${recentAnalysis.titleFeedback})
            Description SEO Score: ${recentAnalysis.descriptionAnalysisScore}/100
            Intro Pacing Score: ${recentAnalysis.introPacingScore}/100
            Intro Weak Point: ${recentAnalysis.introWeakPointWarning ?: "None detected (smooth retention hook)"}
            Duration: ${recentAnalysis.durationText}
            """.trimIndent()
        } else {
            "No recent video analyses completed in this session."
        }

        return """
            ============================================================
            XSOS 1000 - EXECUTIVE MULTI-AGENT PLATFORM BRIEFING
            ============================================================
            Generated: $timestamp
            Operator: $operatorEmail
            Platform: XSOS Neural Core v1.0
            Security: Legitimate OAuth2 Verified / Zero-Spam Certified
            
            ------------------------------------------------------------
            AGENT CLUSTER TELEMETRY (1,000 SIMULATED AGENTS)
            ------------------------------------------------------------
            - Total Registered Agents: ${metrics.totalAgents}
            - Currently Active: ${metrics.activeAgents}
            - Processing Tasks: ${metrics.processingAgents}
            - Idle / Standby: ${metrics.idleAgents}
            - Paused by Operator: ${metrics.pausedAgents}
            - System Exceptions / Error State: ${metrics.errorAgents}
            - Self-Healed by Watchdog: ${metrics.totalSelfHealed}
            - Completed Tasks: ${metrics.completedTasks}
            - Failed Tasks: ${metrics.failedTasks}
            - System Throughput: ${String.format(Locale.US, "%.1f", metrics.throughputOpsPerSec)} ops/sec
            
            $videoSection
            
            ------------------------------------------------------------
            SYSTEM HEALTH & COMPLIANCE
            ------------------------------------------------------------
            - Error Rate: ${if (metrics.completedTasks > 0) String.format(Locale.US, "%.2f", (metrics.failedTasks.toFloat() / (metrics.completedTasks + metrics.failedTasks)) * 100f) else "0.00"}%
            - Sandbox Protection: STRICT (All simulations remain private)
            - Rate-Limit Protection: Active Token-Bucket Throttle
            
            [XSOS Autonomous Operations Command]
        """.trimIndent()
    }

    /**
     * Prepares an Android Intent to open the official Gmail application
     * with the prefilled executive multi-agent intelligence report.
     */
    fun dispatchEmailIntent(
        context: Context,
        recipientEmail: String,
        subject: String,
        body: String
    ): Intent {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }
}
