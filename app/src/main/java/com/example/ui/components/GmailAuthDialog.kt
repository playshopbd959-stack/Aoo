package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.GmailConfig
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderActive
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.MonospaceFont
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted

@Composable
fun GmailAuthDialog(
    config: GmailConfig,
    onDismiss: () -> Unit,
    onSave: (email: String, autoDaily: Boolean) -> Unit,
    onDispatchTest: () -> Unit
) {
    var emailInput by remember { mutableStateOf(config.email) }
    var autoDaily by remember { mutableStateOf(config.autoDailyBriefing) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberDark,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderActive, RoundedCornerShape(12.dp))
                .testTag("gmail_auth_dialog")
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
                            text = "GMAIL OAUTH & DISPATCH",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = MonospaceFont,
                            color = PureWhite
                        )
                        Text(
                            text = "Legitimate Google Account Integration",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_gmail_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = OffWhite)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Compliance Notice
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SurfaceDark,
                    borderColor = CardBorder
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AUTH STATUS: VERIFIED",
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = CyberGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Connects existing Google accounts securely. Strictly adheres to Google API User Data Policy. Never mass-creates accounts or bypasses verification.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = OffWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Email Input
                Text(
                    text = "RECIPIENT OPERATOR EMAIL",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gmail_email_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = OffWhite
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = MonospaceFont)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Scopes
                Text(
                    text = "OAUTH SCOPES: https://www.googleapis.com/auth/gmail.send",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = CyberCyan
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Auto-Briefing switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Executive Briefing",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PureWhite
                        )
                        Text(
                            text = "Automated email summaries of agent activity",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = autoDaily,
                        onCheckedChange = { autoDaily = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureBlack,
                            checkedTrackColor = PureWhite,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = CardBorder
                        ),
                        modifier = Modifier.testTag("gmail_auto_briefing_switch")
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                CyberButton(
                    text = "Dispatch Intelligence Briefing Now",
                    onClick = {
                        onSave(emailInput, autoDaily)
                        onDispatchTest()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true,
                    testTag = "dispatch_briefing_button"
                )

                Spacer(modifier = Modifier.height(8.dp))

                CyberButton(
                    text = "Save Configuration",
                    onClick = {
                        onSave(emailInput, autoDaily)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = false,
                    testTag = "save_gmail_config_button"
                )
            }
        }
    }
}
