package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RetentionPoint
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.MonospaceFont
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextDisabled
import com.example.ui.theme.TextMuted

@Composable
fun RetentionCurveChart(
    retentionPoints: List<RetentionPoint>,
    modifier: Modifier = Modifier
) {
    if (retentionPoints.isEmpty()) return

    var selectedPoint by remember { mutableStateOf<RetentionPoint?>(retentionPoints.firstOrNull()) }

    CyberCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("retention_chart_card"),
        backgroundColor = CyberDark
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "AUDIENCE RETENTION PREDICTION",
                    style = MaterialTheme.typography.labelLarge,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "150 PREDICTOR AGENTS",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = CyberGreen
                )
            }

            if (selectedPoint != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${selectedPoint!!.second}s: ${selectedPoint!!.retentionPercent}% retention – ${selectedPoint!!.note ?: "Audience baseline"}",
                    fontFamily = MonospaceFont,
                    fontSize = 11.sp,
                    color = CyberCyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .pointerInput(retentionPoints) {
                            detectTapGestures { tapOffset ->
                                val pointSpacing = size.width / (retentionPoints.size - 1).coerceAtLeast(1)
                                val closestIndex = (tapOffset.x / pointSpacing)
                                    .toInt()
                                    .coerceIn(0, retentionPoints.size - 1)
                                selectedPoint = retentionPoints[closestIndex]
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val paddingBottom = 20f

                    // Grid lines at 100%, 75%, 50%, 25%
                    val gridLevels = listOf(1.0f, 0.75f, 0.5f, 0.25f)
                    for (level in gridLevels) {
                        val y = (1.0f - level) * (h - paddingBottom)
                        drawLine(
                            color = CardBorder,
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    // Compute points
                    val points = retentionPoints.mapIndexed { index, item ->
                        val x = if (retentionPoints.size > 1) {
                            (index.toFloat() / (retentionPoints.size - 1).toFloat()) * w
                        } else w / 2f
                        val y = (1.0f - (item.retentionPercent / 100f)) * (h - paddingBottom)
                        Offset(x, y)
                    }

                    // Curve Path
                    val curvePath = Path()
                    val fillPath = Path()

                    points.forEachIndexed { index, pt ->
                        if (index == 0) {
                            curvePath.moveTo(pt.x, pt.y)
                            fillPath.moveTo(pt.x, pt.y)
                        } else {
                            val prev = points[index - 1]
                            val midX = (prev.x + pt.x) / 2f
                            curvePath.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
                            fillPath.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
                        }
                    }

                    fillPath.lineTo(w, h - paddingBottom)
                    fillPath.lineTo(0f, h - paddingBottom)
                    fillPath.close()

                    // Draw gradient area
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = h - paddingBottom
                        )
                    )

                    // Draw glowing curve
                    drawPath(
                        path = curvePath,
                        color = PureWhite,
                        style = Stroke(width = 3.5f)
                    )

                    // Draw node circles
                    points.forEachIndexed { idx, pt ->
                        val item = retentionPoints[idx]
                        val isSelected = item == selectedPoint
                        drawCircle(
                            color = if (isSelected) CyberCyan else PureWhite,
                            radius = if (isSelected) 6f else 3.5f,
                            center = pt
                        )
                    }
                }
            }

            // Timeline labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                retentionPoints.forEach { pt ->
                    Text(
                        text = "${pt.second}s",
                        fontFamily = MonospaceFont,
                        fontSize = 9.sp,
                        color = TextDisabled
                    )
                }
            }
        }
    }
}
