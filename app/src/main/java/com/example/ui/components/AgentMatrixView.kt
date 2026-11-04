package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentEntity
import com.example.data.model.AgentStatus
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberYellow
import com.example.ui.theme.MonospaceFont
import com.example.ui.theme.TextDisabled
import com.example.ui.theme.TextMuted

@Composable
fun AgentMatrixView(
    agents: List<AgentEntity>,
    onSelectAgent: (AgentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalNodes = agents.size.coerceAtLeast(1000)
    // 25 columns x 40 rows = 1,000 nodes
    val columns = 25
    val rows = 40

    CyberCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("agent_matrix_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "NEURAL MATRIX (1,000 AGENT NODES)",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
                Text(
                    text = "25 x 40 DENSE GRID",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Canvas rendering 1000 nodes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .pointerInput(agents) {
                            detectTapGestures { tapOffset ->
                                val cellWidth = size.width / columns.toFloat()
                                val cellHeight = size.height / rows.toFloat()
                                val col = (tapOffset.x / cellWidth).toInt().coerceIn(0, columns - 1)
                                val row = (tapOffset.y / cellHeight).toInt().coerceIn(0, rows - 1)
                                val index = row * columns + col
                                if (index in agents.indices) {
                                    onSelectAgent(agents[index])
                                }
                            }
                        }
                ) {
                    val cellWidth = size.width / columns.toFloat()
                    val cellHeight = size.height / rows.toFloat()
                    val dotRadius = (minOf(cellWidth, cellHeight) * 0.38f).coerceAtLeast(1.5f)

                    for (r in 0 until rows) {
                        for (c in 0 until columns) {
                            val idx = r * columns + c
                            val agent = agents.getOrNull(idx)
                            val nodeColor = if (agent != null) {
                                when (agent.status) {
                                    AgentStatus.ACTIVE.name -> CyberGreen
                                    AgentStatus.PROCESSING.name -> CyberCyan
                                    AgentStatus.PAUSED.name -> CyberYellow
                                    AgentStatus.ERROR.name -> CyberRed
                                    else -> TextDisabled
                                }
                            } else {
                                TextDisabled
                            }

                            val centerX = c * cellWidth + cellWidth / 2f
                            val centerY = r * cellHeight + cellHeight / 2f

                            drawCircle(
                                color = nodeColor,
                                radius = dotRadius,
                                center = Offset(centerX, centerY)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = CyberGreen, label = "Active")
                LegendItem(color = CyberCyan, label = "Processing")
                LegendItem(color = CyberYellow, label = "Paused")
                LegendItem(color = CyberRed, label = "Error")
                LegendItem(color = TextDisabled, label = "Idle")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontFamily = MonospaceFont,
            fontSize = 9.sp,
            color = TextMuted
        )
    }
}
