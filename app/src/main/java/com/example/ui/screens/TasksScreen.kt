package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.example.data.model.TaskEntity
import com.example.data.model.TaskStatus
import com.example.ui.MainViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusPill
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TasksScreen(
    viewModel: MainViewModel,
    onOpenTaskScheduler: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsState()
    val runningTasks = tasks.filter { it.status == TaskStatus.RUNNING.name || it.status == TaskStatus.QUEUED.name }
    val pastTasks = tasks.filter { it.status != TaskStatus.RUNNING.name && it.status != TaskStatus.QUEUED.name }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 16.dp)
            .testTag("tasks_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "TASK PIPELINE",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PureWhite
                    )
                    Text(
                        text = "PARALLEL TASK SCHEDULER & DISPATCH",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
                CyberButton(
                    text = "+ New Task",
                    onClick = onOpenTaskScheduler,
                    isPrimary = true,
                    testTag = "schedule_new_task_btn"
                )
            }
        }

        // Active Tasks Section
        if (runningTasks.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Active Parallel Pipelines",
                    tag = "${runningTasks.size} RUNNING"
                )
            }

            items(runningTasks, key = { it.id }) { task ->
                ActiveTaskCard(
                    task = task,
                    onCancel = { viewModel.cancelTask(task.id) }
                )
            }
        }

        // Past Tasks Section
        item {
            SectionHeader(
                title = "Execution History",
                tag = "${pastTasks.size} AUDITED"
            )
        }

        if (pastTasks.isEmpty()) {
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CyberDark
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No prior tasks in history log.",
                            fontFamily = MonospaceFont,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        CyberButton(
                            text = "Dispatch First AI Task",
                            onClick = onOpenTaskScheduler,
                            isPrimary = true
                        )
                    }
                }
            }
        } else {
            items(pastTasks, key = { it.id }) { task ->
                PastTaskCard(task = task)
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ActiveTaskCard(
    task: TaskEntity,
    onCancel: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_task_${task.id}"),
        backgroundColor = SurfaceDark,
        borderColor = CyberCyan
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = PureWhite
                    )
                    Text(
                        text = "${task.allocatedAgentCount} AGENTS • PRIORITY: ${task.priority}",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        color = CyberCyan
                    )
                }
                StatusPill(status = task.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { task.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PureWhite,
                trackColor = CardBorder
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = task.resultSummary ?: "Executing parallel vector stages...",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${(task.progress * 100).toInt()}%",
                    fontFamily = MonospaceFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            CyberButton(
                text = "Cancel Task",
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                isPrimary = false,
                testTag = "cancel_task_${task.id}"
            )
        }
    }
}

@Composable
private fun PastTaskCard(task: TaskEntity) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    val timeStr = timeFormat.format(Date(task.createdAt))

    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}"),
        backgroundColor = CyberDark
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PureWhite
                    )
                    Text(
                        text = "[$timeStr] ${task.allocatedAgentCount} Agents • ${task.executionTimeMs}ms",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
                StatusPill(status = task.status)
            }

            if (!task.resultSummary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = task.resultSummary,
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = if (task.status == TaskStatus.FAILED.name) CyberRed else CyberGreen
                )
            }
        }
    }
}
