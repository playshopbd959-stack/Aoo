package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentCluster
import com.example.ui.MainViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.MetricBox
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.MonospaceFont
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val agents by viewModel.allAgents.collectAsState()

    val uptimeHours = metrics.systemUptimeSeconds / 3600
    val uptimeMinutes = (metrics.systemUptimeSeconds % 3600) / 60
    val uptimeSecs = metrics.systemUptimeSeconds % 60
    val uptimeFormatted = String.format("%02d:%02d:%02d", uptimeHours, uptimeMinutes, uptimeSecs)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 16.dp)
            .testTag("analytics_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "SYSTEM TELEMETRY",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PureWhite
                )
                Text(
                    text = "AGENT PERFORMANCE & SELF-HEALING WATCHDOG",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        // Top Metrics
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricBox(
                    title = "System Uptime",
                    value = uptimeFormatted,
                    subValue = "Continuous Loop",
                    indicatorColor = CyberGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    title = "Self-Healed",
                    value = "${metrics.totalSelfHealed}",
                    subValue = "Watchdog Restorations",
                    indicatorColor = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricBox(
                    title = "Success Rate",
                    value = "99.4%",
                    subValue = "Multi-Agent Consensus",
                    indicatorColor = CyberGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    title = "Error Tolerance",
                    value = "${metrics.errorAgents} in Error",
                    subValue = "Zero-Crash Watchdog",
                    indicatorColor = if (metrics.errorAgents > 0) CyberRed else CyberGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Agent Performance Over Time Visualization (Tasks Completed & Average Latency)
        item {
            com.example.ui.components.AgentPerformanceChart()
        }

        // Fault Tolerance & Chaos Testing
        item {
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chaos_test_card"),
                backgroundColor = SurfaceDark,
                borderColor = CyberCyan
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CHAOS TESTING & AUTOMATIC RECOVERY",
                        fontFamily = MonospaceFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Inject artificial failures and memory timeouts into random agents to test the background Watchdog self-healing loop in real time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OffWhite
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CyberButton(
                        text = "Inject Chaos into Random Agent",
                        onClick = {
                            val randomId = String.format("AGT-%04d", kotlin.random.Random.nextInt(1, 1001))
                            viewModel.injectChaosError(randomId)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isPrimary = false,
                        testTag = "inject_chaos_button"
                    )
                }
            }
        }

        // Cluster Workload Breakdown
        item {
            SectionHeader(title = "Cluster Workload & Allocation", tag = "7 DOMAINS")
        }

        val clusterAllocations = listOf(
            AgentCluster.CONTENT_ANALYZER to 200,
            AgentCluster.SENTIMENT_OBSERVER to 150,
            AgentCluster.RETENTION_PREDICTOR to 150,
            AgentCluster.DEMOGRAPHIC_SIMULATOR to 150,
            AgentCluster.SEO_SCOUT to 150,
            AgentCluster.BRAND_SAFETY to 100,
            AgentCluster.DISPATCH_SYNTHESIS to 100
        )

        clusterAllocations.forEach { (cluster, count) ->
            item {
                ClusterPerformanceBar(
                    clusterName = cluster.displayName,
                    description = cluster.description,
                    agentCount = count,
                    total = 1000
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ClusterPerformanceBar(
    clusterName: String,
    description: String,
    agentCount: Int,
    total: Int
) {
    val pct = agentCount.toFloat() / total.toFloat()

    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = CyberDark
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = clusterName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PureWhite
                )
                Text(
                    text = "$agentCount Agents (${(pct * 100).toInt()}%)",
                    fontFamily = MonospaceFont,
                    fontSize = 11.sp,
                    color = CyberCyan
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { pct },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PureWhite,
                trackColor = CardBorder
            )
        }
    }
}
