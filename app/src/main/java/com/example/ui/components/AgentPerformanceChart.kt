package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.MonospaceFont
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextDisabled
import com.example.ui.theme.TextMuted

data class PerformanceTelemetryPoint(
    val timeLabel: String,
    val tasksCompleted: Int,
    val averageLatencyMs: Int,
    val activeNodes: Int = 1000
)

@Composable
fun AgentPerformanceChart(
    modifier: Modifier = Modifier
) {
    // Default 7-point telemetry timeline (T-6h to Now)
    val telemetryData = remember {
        listOf(
            PerformanceTelemetryPoint("T-6h", 120, 185),
            PerformanceTelemetryPoint("T-5h", 280, 154),
            PerformanceTelemetryPoint("T-4h", 490, 118),
            PerformanceTelemetryPoint("T-3h", 780, 92),
            PerformanceTelemetryPoint("T-2h", 1050, 68),
            PerformanceTelemetryPoint("T-1h", 1320, 48),
            PerformanceTelemetryPoint("Now", 1580, 38)
        )
    }

    var selectedIndex by remember { mutableIntStateOf(telemetryData.lastIndex) }
    val selectedPoint = telemetryData[selectedIndex]

    var viewMode by remember { mutableStateOf(0) } // 0 = Dual, 1 = Tasks, 2 = Latency

    CyberCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("agent_performance_chart_card"),
        backgroundColor = CyberDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "AGENT PERFORMANCE OVER TIME",
                        style = MaterialTheme.typography.labelLarge,
                        color = PureWhite
                    )
                    Text(
                        text = "TASKS COMPLETED & LATENCY TRAJECTORY",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                // Metric Toggle Chips
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ChartTogglePill("ALL", viewMode == 0) { viewMode = 0 }
                    ChartTogglePill("OPS", viewMode == 1) { viewMode = 1 }
                    ChartTogglePill("LATENCY", viewMode == 2) { viewMode = 2 }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Selected Inspector HUD
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(CyberCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tasks: ${selectedPoint.tasksCompleted} ops",
                        fontFamily = MonospaceFont,
                        fontSize = 11.sp,
                        color = CyberCyan
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(CyberGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Avg Latency: ${selectedPoint.averageLatencyMs}ms",
                        fontFamily = MonospaceFont,
                        fontSize = 11.sp,
                        color = CyberGreen
                    )
                }

                Text(
                    text = "@ ${selectedPoint.timeLabel}",
                    fontFamily = MonospaceFont,
                    fontSize = 11.sp,
                    color = PureWhite
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Canvas Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .pointerInput(telemetryData) {
                            detectTapGestures { tapOffset ->
                                val spacing = size.width / (telemetryData.size - 1).coerceAtLeast(1)
                                val idx = ((tapOffset.x + (spacing / 2f)) / spacing)
                                    .toInt()
                                    .coerceIn(0, telemetryData.size - 1)
                                selectedIndex = idx
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 24f

                    // Grid lines
                    for (i in 0..3) {
                        val y = (i / 3f) * (h - bottomPadding)
                        drawLine(
                            color = CardBorder,
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    val maxTasks = telemetryData.maxOf { it.tasksCompleted }.coerceAtLeast(1)
                    val maxLatency = 200 // 200ms benchmark

                    val taskPoints = telemetryData.mapIndexed { idx, pt ->
                        val x = if (telemetryData.size > 1) {
                            (idx.toFloat() / (telemetryData.size - 1).toFloat()) * w
                        } else w / 2f
                        val y = (1f - (pt.tasksCompleted.toFloat() / maxTasks.toFloat())) * (h - bottomPadding)
                        Offset(x, y)
                    }

                    val latencyPoints = telemetryData.mapIndexed { idx, pt ->
                        val x = if (telemetryData.size > 1) {
                            (idx.toFloat() / (telemetryData.size - 1).toFloat()) * w
                        } else w / 2f
                        val y = (1f - (pt.averageLatencyMs.toFloat() / maxLatency.toFloat())) * (h - bottomPadding)
                        Offset(x, y)
                    }

                    // 1. Draw Tasks Completed Area & Line (if mode 0 or 1)
                    if (viewMode == 0 || viewMode == 1) {
                        val taskPath = Path()
                        val taskFill = Path()

                        taskPoints.forEachIndexed { index, pt ->
                            if (index == 0) {
                                taskPath.moveTo(pt.x, pt.y)
                                taskFill.moveTo(pt.x, pt.y)
                            } else {
                                val prev = taskPoints[index - 1]
                                val midX = (prev.x + pt.x) / 2f
                                taskPath.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
                                taskFill.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
                            }
                        }

                        taskFill.lineTo(w, h - bottomPadding)
                        taskFill.lineTo(0f, h - bottomPadding)
                        taskFill.close()

                        drawPath(
                            path = taskFill,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    CyberCyan.copy(alpha = 0.28f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = h - bottomPadding
                            )
                        )

                        drawPath(
                            path = taskPath,
                            color = CyberCyan,
                            style = Stroke(width = 3.5f)
                        )

                        taskPoints.forEachIndexed { i, pt ->
                            val isSelected = i == selectedIndex
                            drawCircle(
                                color = if (isSelected) PureWhite else CyberCyan,
                                radius = if (isSelected) 5.5f else 3f,
                                center = pt
                            )
                        }
                    }

                    // 2. Draw Latency Line (if mode 0 or 2)
                    if (viewMode == 0 || viewMode == 2) {
                        val latencyPath = Path()
                        latencyPoints.forEachIndexed { index, pt ->
                            if (index == 0) {
                                latencyPath.moveTo(pt.x, pt.y)
                            } else {
                                val prev = latencyPoints[index - 1]
                                val midX = (prev.x + pt.x) / 2f
                                latencyPath.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
                            }
                        }

                        drawPath(
                            path = latencyPath,
                            color = CyberGreen,
                            style = Stroke(width = 3f)
                        )

                        latencyPoints.forEachIndexed { i, pt ->
                            val isSelected = i == selectedIndex
                            drawCircle(
                                color = if (isSelected) PureWhite else CyberGreen,
                                radius = if (isSelected) 5.5f else 3f,
                                center = pt
                            )
                        }
                    }

                    // Draw vertical scrubber guide line for selected index
                    val selectedX = taskPoints[selectedIndex].x
                    drawLine(
                        color = OffWhite.copy(alpha = 0.4f),
                        start = Offset(selectedX, 0f),
                        end = Offset(selectedX, h - bottomPadding),
                        strokeWidth = 1f
                    )
                }
            }

            // X-Axis Timeline Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                telemetryData.forEachIndexed { idx, pt ->
                    Text(
                        text = pt.timeLabel,
                        fontFamily = MonospaceFont,
                        fontSize = 9.sp,
                        color = if (idx == selectedIndex) PureWhite else TextDisabled
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartTogglePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                if (isSelected) PureWhite else CyberDark,
                RoundedCornerShape(4.dp)
            )
            .border(
                1.dp,
                if (isSelected) PureWhite else CardBorder,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontFamily = MonospaceFont,
            fontSize = 9.sp,
            color = if (isSelected) PureBlack else OffWhite
        )
    }
}
