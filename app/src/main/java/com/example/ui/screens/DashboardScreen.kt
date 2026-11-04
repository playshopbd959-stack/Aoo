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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
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
    val telem by viewModel.systemTelemetry.collectAsState()
    val agents by viewModel.allAgents.collectAsState()
    val logs by viewModel.recentLogs.collectAsState()
    val throttleMode by viewModel.throttleMode.collectAsState()

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
                            text = "XSOS 1000",
                            style = MaterialTheme.typography.headlineLarge,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(SurfaceDark, RoundedCornerShape(4.dp))
                                .border(1.dp, if (telem.all1000Verified) CyberGreen else CyberYellow, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (telem.all1000Verified) "1,000 VERIFIED IN DB" else "INITIALIZING...",
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = if (telem.all1000Verified) CyberGreen else CyberYellow
                            )
                        }
                    }
                    Text(
                        text = "REAL MULTI-AGENT EXECUTION ENGINE V2",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                // Throttle / Rate Mode Indicator
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${throttleMode.maxOpsPerSec} OPS/S",
                        fontFamily = MonospaceFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PureWhite
                    )
                    Text(
                        text = throttleMode.label.uppercase(),
                        fontFamily = MonospaceFont,
                        fontSize = 9.sp,
                        color = CyberCyan
                    )
                }
            }
        }

        // Real Hardware, CPU & Memory Monitoring Card
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SurfaceDark,
                borderColor = CyberCyan
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "SYSTEM TELEMETRY & HARDWARE",
                            fontFamily = MonospaceFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = PureWhite
                        )
                        Text(
                            text = "${telem.availableProcessors} CPU CORES DETECTED",
                            fontFamily = MonospaceFont,
                            fontSize = 10.sp,
                            color = CyberGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Memory progress
                    val memPercent = if (telem.maxMemoryMb > 0) (telem.usedMemoryMb.toFloat() / telem.maxMemoryMb.toFloat()).coerceIn(0f, 1f) else 0.1f
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "JVM HEAP: ${telem.usedMemoryMb} MB / ${telem.totalMemoryMb} MB",
                            fontFamily = MonospaceFont,
                            fontSize = 11.sp,
                            color = OffWhite
                        )
                        Text(
                            text = "MAX: ${telem.maxMemoryMb} MB",
                            fontFamily = MonospaceFont,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { memPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (memPercent > 0.8f) CyberRed else CyberCyan,
                        trackColor = CardBorder
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Active Workers: ${telem.activeWorkersCount}",
                            fontFamily = MonospaceFont,
                            fontSize = 10.sp,
                            color = CyberCyan
                        )
                        Text(
                            text = "Queued Units: ${telem.queuedWorkItemsCount}",
                            fontFamily = MonospaceFont,
                            fontSize = 10.sp,
                            color = OffWhite
                        )
                        Text(
                            text = "DB Nodes: ${telem.totalAgentsInitialized} / 1000",
                            fontFamily = MonospaceFont,
                            fontSize = 10.sp,
                            color = CyberGreen
                        )
                    }
                }
            }
        }

        // Key Dashboard Metrics Grid (Real State)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        title = "Working",
                        value = "${telem.workingAgentsCount}",
                        subValue = "Active Workers",
                        indicatorColor = CyberCyan,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_working_agents"
                    )
                    MetricBox(
                        title = "Idle / Ready",
                        value = "${telem.idleAgentsCount}",
                        subValue = "Standby in DB",
                        indicatorColor = CyberGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_idle_agents"
                    )
                    MetricBox(
                        title = "Completed",
                        value = "${telem.completedAgentsCount}",
                        subValue = "Shards Finished",
                        indicatorColor = CyberGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_completed_agents"
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        title = "Tasks Completed",
                        value = "${metrics.completedTasks}",
                        subValue = "Consensus Verified",
                        indicatorColor = CyberGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_completed_tasks"
                    )
                    MetricBox(
                        title = "Failed / Paused",
                        value = "${telem.failedAgentsCount + telem.pausedAgentsCount}",
                        subValue = "${telem.failedAgentsCount} Failed, ${telem.pausedAgentsCount} Paused",
                        indicatorColor = if (telem.failedAgentsCount > 0) CyberRed else CyberGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_failed_tasks"
                    )
                    MetricBox(
                        title = "Throughput",
                        value = String.format(java.util.Locale.US, "%.1f", telem.throughputOpsPerSec),
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
                        text = "GLOBAL ORCHESTRATION & DISPATCH",
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
                    text = "Google Accounts",
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
                    tag = "REAL-TIME EXECUTION LOGS",
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
                        logs.take(15).forEach { log ->
                            LiveLogItem(log = log)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
