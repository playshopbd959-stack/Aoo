package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentStatus
import com.example.data.model.LogEventEntity
import com.example.data.model.LogSeverity
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderActive
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
import com.example.ui.theme.TextDisabled
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CardBorder,
    backgroundColor: Color = SurfaceDark,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickableModifier = if (onClick != null) {
        modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    } else {
        modifier.clip(RoundedCornerShape(8.dp))
    }

    Surface(
        modifier = clickableModifier
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(8.dp)),
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        content()
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    subValue: String? = null,
    indicatorColor: Color? = null,
    modifier: Modifier = Modifier,
    testTag: String = "metric_box"
) {
    CyberCard(
        modifier = modifier.testTag(testTag),
        backgroundColor = CyberDark,
        borderColor = CardBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (indicatorColor != null) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(indicatorColor)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = MonospaceFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PureWhite
            )
            if (subValue != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subValue,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun StatusPill(
    status: String,
    modifier: Modifier = Modifier
) {
    val (dotColor, textLabel) = when (status.uppercase()) {
        AgentStatus.ACTIVE.name, "ACTIVE", "RUNNING", "ONLINE" -> CyberGreen to "ACTIVE"
        AgentStatus.PROCESSING.name, "PROCESSING" -> CyberCyan to "PROCESSING"
        AgentStatus.IDLE.name, "IDLE", "STANDBY" -> TextMuted to "IDLE"
        AgentStatus.PAUSED.name, "PAUSED" -> CyberYellow to "PAUSED"
        AgentStatus.ERROR.name, "ERROR", "FAILED" -> CyberRed to "ERROR"
        else -> PureWhite to status.uppercase()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(PureBlack, RoundedCornerShape(4.dp))
            .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = textLabel,
            fontFamily = MonospaceFont,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = OffWhite
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    tag: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            fontFamily = MonospaceFont
        )
        if (tag != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = tag,
                fontFamily = MonospaceFont,
                fontSize = 11.sp,
                color = TextMuted,
                modifier = Modifier
                    .background(SurfaceDark, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(CardBorder)
        )
    }
}

@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    testTag: String = "cyber_button"
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PureWhite,
                contentColor = PureBlack
            ),
            modifier = modifier
                .testTag(testTag)
                .height(44.dp)
        ) {
            Text(
                text = text.uppercase(),
                fontFamily = MonospaceFont,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, CardBorderActive),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = OffWhite
            ),
            modifier = modifier
                .testTag(testTag)
                .height(44.dp)
        ) {
            Text(
                text = text.uppercase(),
                fontFamily = MonospaceFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun LiveLogItem(
    log: LogEventEntity,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    val timeString = timeFormat.format(Date(log.timestamp))

    val severityColor = when (log.severity) {
        LogSeverity.SUCCESS.name -> CyberGreen
        LogSeverity.ERROR.name -> CyberRed
        LogSeverity.WARN.name -> CyberYellow
        LogSeverity.RECOVERY.name -> CyberCyan
        else -> TextMuted
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "[$timeString]",
            fontFamily = MonospaceFont,
            fontSize = 11.sp,
            color = TextDisabled,
            modifier = Modifier.width(68.dp)
        )
        Text(
            text = log.agentId,
            fontFamily = MonospaceFont,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = PureWhite,
            modifier = Modifier.width(90.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = log.message,
            fontFamily = MonospaceFont,
            fontSize = 11.sp,
            color = severityColor,
            modifier = Modifier.weight(1f)
        )
    }
}
