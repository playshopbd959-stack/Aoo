package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AgentCluster
import com.example.data.model.TaskPriority
import com.example.data.model.TaskType
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderActive
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDark
import com.example.ui.theme.MonospaceFont
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted

@Composable
fun TaskCreateDialog(
    onDismiss: () -> Unit,
    onSchedule: (title: String, type: TaskType, priority: TaskPriority, cluster: String, agentCount: Int) -> Unit
) {
    var title by remember { mutableStateOf("Autonomous Deep Content Scan") }
    var selectedType by remember { mutableStateOf(TaskType.YOUTUBE_DEEP_ANALYSIS) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.HIGH) }
    var selectedCluster by remember { mutableStateOf("ALL") }
    var agentCount by remember { mutableFloatStateOf(250f) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberDark,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderActive, RoundedCornerShape(12.dp))
                .testTag("task_create_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "DISPATCH MULTI-AGENT TASK",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = MonospaceFont,
                        color = PureWhite
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_task_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = OffWhite)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title input
                Text(
                    text = "TASK IDENTIFIER / DIRECTIVE",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = OffWhite
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = MonospaceFont)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Task Type
                Text(
                    text = "SELECT PIPELINE TYPE",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                TaskType.values().take(4).forEach { type ->
                    val isSelected = selectedType == type
                    CyberCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { selectedType = type },
                        backgroundColor = if (isSelected) SurfaceDark else CyberDark,
                        borderColor = if (isSelected) PureWhite else CardBorder
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(
                                text = type.title,
                                fontFamily = MonospaceFont,
                                fontSize = 11.sp,
                                color = if (isSelected) PureWhite else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Priority
                Text(
                    text = "EXECUTION PRIORITY",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TaskPriority.values().forEach { priority ->
                        val isSelected = selectedPriority == priority
                        CyberCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPriority = priority },
                            backgroundColor = if (isSelected) SurfaceDark else CyberDark,
                            borderColor = if (isSelected) PureWhite else CardBorder
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = priority.name,
                                    fontFamily = MonospaceFont,
                                    fontSize = 10.sp,
                                    color = if (isSelected) PureWhite else TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Agent Pool Allocation Slider
                Text(
                    text = "AGENT POOL ALLOCATION: ${agentCount.toInt()} / 1,000 AGENTS",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = CyberCyan
                )
                Slider(
                    value = agentCount,
                    onValueChange = { agentCount = it },
                    valueRange = 10f..1000f,
                    steps = 19,
                    modifier = Modifier.testTag("agent_count_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = PureWhite,
                        activeTrackColor = PureWhite,
                        inactiveTrackColor = CardBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Schedule button
                CyberButton(
                    text = "Launch Parallel Task (${agentCount.toInt()} Agents)",
                    onClick = {
                        if (title.isNotBlank()) {
                            onSchedule(title, selectedType, selectedPriority, selectedCluster, agentCount.toInt())
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true,
                    testTag = "submit_schedule_task"
                )
            }
        }
    }
}
