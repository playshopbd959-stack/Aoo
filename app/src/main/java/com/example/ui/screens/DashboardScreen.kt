package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.AgentMatrixView
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.LiveLogItem
import com.example.ui.components.MetricBox
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberYellow
import com.example.ui.theme.MonospaceFont
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onOpenTaskScheduler: () -> Unit,
    onOpenGmailDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val agents by viewModel.allAgents.collectAsState()
    val logs by viewModel.recentLogs.collectAsState()
    val throttleMode by viewModel.throttleMode.collectAsState()
    val tokenBucket by viewModel.tokenBucketRemaining.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Telemetry Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "XSOS",
                            style = MaterialTheme.typography.headlineLarge,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(SurfaceDark, RoundedCornerShape(4.dp))
                                .border(1.dp, CardBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "1000 AI AGENTS",
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = CyberGreen
                            )
                        }
                    }
                    Text(
                        text = "AUTONOMOUS MULTI-AGENT NEURAL PLATFORM",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                // Rate-limit token pill
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${throttleMode.maxOpsPerSec} OPS/S",
                        fontFamily = MonospaceFont,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PureWhite
                    )
                    Text(
                        text = "TOKENS: $tokenBucket",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        color = CyberCyan
                    )
                }
            }
        }

        // Key Dashboard Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        title = "Total Agents",
                        value = "${metrics.totalAgents}",
                        subValue = "7 Specialized Clusters",
                        modifier = Modifier.weight(1f),
                        testTag = "metric_total_agents"
                    )
                    MetricBox(
                        title = "Active Agents",
                        value = "${metrics.activeAgents}",
                        subValue = "${metrics.processingAgents} Processing",
                        indicatorColor = CyberGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_active_agents"
                    )
                    MetricBox(
                        title = "Inactive/Paused",
                        value = "${metrics.pausedAgents + metrics.idleAgents}",
                        subValue = "${metrics.idleAgents} Idle / ${metrics.pausedAgents} Paused",
                        indicatorColor = CyberYellow,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_inactive_agents"
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        title = "Completed Tasks",
                        value = "${metrics.completedTasks}",
                        subValue = "Consensus Verified",
                        indicatorColor = CyberGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_completed_tasks"
                    )
                    MetricBox(
                        title = "Failed / Recovered",
                        value = "${metrics.failedTasks}",
                        subValue = "${metrics.totalSelfHealed} Self-Healed",
                        indicatorColor = if (metrics.failedTasks > 0) CyberRed else CyberGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_failed_tasks"
                    )
                    MetricBox(
                        title = "System Throughput",
                        value = String.format(java.util.Locale.US, "%.1f", metrics.throughputOpsPerSec),
                        subValue = "Ops / Sec",
                        indicatorColor = CyberCyan,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_throughput"
                    )
                }
            }
        }

        // Global Agent Controls
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SurfaceDark
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "GLOBAL AGENT ORCHESTRATION",
                        fontFamily = MonospaceFont,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CyberButton(
                            text = "Start All",
                            onClick = { viewModel.startAllAgents() },
                            modifier = Modifier.weight(1f),
                            isPrimary = true,
                            testTag = "action_start_all"
                        )
                        CyberButton(
                            text = "Pause All",
                            onClick = { viewModel.pauseAllAgents() },
                            modifier = Modifier.weight(1f),
                            isPrimary = false,
                            testTag = "action_pause_all"
                        )
                        CyberButton(
                            text = "Self-Heal",
                            onClick = { viewModel.selfHealAll() },
                            modifier = Modifier.weight(1f),
                            isPrimary = false,
                            testTag = "action_self_heal"
                        )
                    }
                }
            }
        }

        // 1,000 Agent Matrix Preview
        item {
            AgentMatrixView(
                agents = agents,
                onSelectAgent = { agent -> viewModel.selectAgent(agent) }
            )
        }

        // Quick Action Bar
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CyberButton(
                    text = "Dispatch Parallel Task",
                    onClick = onOpenTaskScheduler,
                    modifier = Modifier.weight(1f),
                    isPrimary = true,
                    testTag = "dashboard_dispatch_task"
                )
                CyberButton(
                    text = "Gmail Executive Briefing",
                    onClick = onOpenGmailDialog,
                    modifier = Modifier.weight(1f),
                    isPrimary = false,
                    testTag = "dashboard_gmail_briefing"
                )
            }
        }

        // Live Activity Monitor
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SectionHeader(
                    title = "Live Activity Monitor",
                    tag = "REAL-TIME STREAM",
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { viewModel.clearLogs() },
                    modifier = Modifier.testTag("clear_logs_button")
                ) {
                    Text(
                        text = "CLEAR",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Logs Container Card
        item {
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_activity_card"),
                backgroundColor = CyberDark
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (logs.isEmpty()) {
                        Text(
                            text = "Awaiting agent activity telemetry...",
                            fontFamily = MonospaceFont,
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        logs.take(12).forEach { log ->
                            LiveLogItem(log = log)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // bottom navigation space
        }
    }
}
