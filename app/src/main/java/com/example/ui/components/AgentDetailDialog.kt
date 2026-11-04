package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AgentEntity
import com.example.data.model.AgentStatus
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderActive
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberYellow
import com.example.ui.theme.MonospaceFont
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AgentDetailDialog(
    agent: AgentEntity,
    onDismiss: () -> Unit,
    onStart: (String) -> Unit,
    onPause: (String) -> Unit,
    onRestart: (String) -> Unit,
    onInjectChaos: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberDark,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderActive, RoundedCornerShape(12.dp))
                .testTag("agent_detail_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = agent.id,
                            fontFamily = MonospaceFont,
                            fontSize = 20.sp,
                            color = PureWhite
                        )
                        Text(
                            text = agent.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusPill(status = agent.status)
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_agent_dialog")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = OffWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cluster & Designation Info
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SurfaceDark
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "CLUSTER ASSIGNMENT",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = agent.cluster.replace("_", " "),
                            fontFamily = MonospaceFont,
                            fontSize = 13.sp,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "CURRENT TASK",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = agent.currentTask,
                            fontFamily = MonospaceFont,
                            fontSize = 12.sp,
                            color = CyberCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Grid
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        title = "Completed",
                        value = "${agent.completedTasks}",
                        subValue = "tasks",
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Failed",
                        value = "${agent.failedTasks}",
                        subValue = "exceptions",
                        indicatorColor = if (agent.failedTasks > 0) CyberRed else CyberGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Success Rate",
                        value = "${agent.successRate}%",
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Compute Load
                Text(
                    text = "NEURAL COMPUTE LOAD: ${agent.loadPercent}%",
                    fontFamily = MonospaceFont,
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { agent.loadPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (agent.loadPercent > 80) CyberRed else PureWhite,
                    trackColor = CardBorder
                )

                Spacer(modifier = Modifier.height(12.dp))
                val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                Text(
                    text = "LAST HEARTBEAT: ${timeFormat.format(Date(agent.lastActive))}",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Controls
                Text(
                    text = "AGENT OVERRIDE CONTROLS",
                    fontFamily = MonospaceFont,
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CyberButton(
                        text = "Start",
                        onClick = { onStart(agent.id) },
                        modifier = Modifier.weight(1f),
                        isPrimary = agent.status != AgentStatus.ACTIVE.name,
                        testTag = "agent_action_start"
                    )
                    CyberButton(
                        text = "Pause",
                        onClick = { onPause(agent.id) },
                        modifier = Modifier.weight(1f),
                        isPrimary = false,
                        testTag = "agent_action_pause"
                    )
                    CyberButton(
                        text = "Restart",
                        onClick = { onRestart(agent.id) },
                        modifier = Modifier.weight(1f),
                        isPrimary = false,
                        testTag = "agent_action_restart"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CyberButton(
                    text = "Simulate Chaos / Inject Error",
                    onClick = { onInjectChaos(agent.id) },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = false,
                    testTag = "agent_action_inject_chaos"
                )
            }
        }
    }
}
