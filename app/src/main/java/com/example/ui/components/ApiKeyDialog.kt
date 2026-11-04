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
import com.example.data.model.ApiKeyConfig
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderActive
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.MonospaceFont
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted

@Composable
fun ApiKeyDialog(
    config: ApiKeyConfig,
    onDismiss: () -> Unit,
    onSave: (youtubeKey: String, geminiKey: String) -> Unit
) {
    var ytKey by remember { mutableStateOf(config.youtubeApiKey) }
    var geminiKey by remember { mutableStateOf(config.geminiApiKey) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberDark,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderActive, RoundedCornerShape(12.dp))
                .testTag("api_key_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "API CREDENTIALS VAULT",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = MonospaceFont,
                            color = PureWhite
                        )
                        Text(
                            text = "Official API Integration Endpoints",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_api_key_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = OffWhite)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SurfaceDark,
                    borderColor = CardBorder
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "OFFICIAL YOUTUBE DATA API V3",
                            fontFamily = MonospaceFont,
                            fontSize = 10.sp,
                            color = CyberGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter your Google Cloud YouTube API Key to query live production metadata. If left blank, XSOS uses high-fidelity sandbox heuristics for private testing.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = OffWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "YOUTUBE DATA API KEY (GOOGLE CLOUD)",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = ytKey,
                    onValueChange = { ytKey = it },
                    placeholder = { Text("AIzaSy...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("youtube_api_key_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = OffWhite
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = MonospaceFont)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "GEMINI AI API KEY (OPTIONAL)",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = geminiKey,
                    onValueChange = { geminiKey = it },
                    placeholder = { Text("AIzaSy...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gemini_api_key_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = OffWhite
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = MonospaceFont)
                )

                Spacer(modifier = Modifier.height(20.dp))

                CyberButton(
                    text = "Save Keys & Update Endpoints",
                    onClick = {
                        onSave(ytKey, geminiKey)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true,
                    testTag = "save_api_keys_button"
                )
            }
        }
    }
}
