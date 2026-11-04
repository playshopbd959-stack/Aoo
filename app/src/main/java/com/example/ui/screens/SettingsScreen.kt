package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThrottleMode
import com.example.ui.MainViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CardBorder
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
fun SettingsScreen(
    viewModel: MainViewModel,
    onOpenGmailDialog: () -> Unit,
    onOpenApiKeyDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentThrottle by viewModel.throttleMode.collectAsState()
    val gmailConfig by viewModel.gmailConfig.collectAsState()
    val apiKeyConfig by viewModel.apiKeyConfig.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "SETTINGS & SYSTEM",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PureWhite
                )
                Text(
                    text = "SECURITY, RATE LIMITING & SETUP GUIDE",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        // Rate-Limit Protection
        item {
            SectionHeader(title = "Rate-Limit Protection Engine", tag = "TOKEN BUCKET")
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ThrottleMode.values().forEach { mode ->
                    val isSelected = currentThrottle == mode
                    CyberCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setThrottleMode(mode) }
                            .testTag("throttle_mode_${mode.name}"),
                        backgroundColor = if (isSelected) SurfaceDark else CyberDark,
                        borderColor = if (isSelected) PureWhite else CardBorder
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = mode.label.uppercase(),
                                    fontFamily = MonospaceFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) PureWhite else OffWhite
                                )
                                Text(
                                    text = "${mode.maxOpsPerSec} OPS/S",
                                    fontFamily = MonospaceFont,
                                    fontSize = 11.sp,
                                    color = if (isSelected) CyberGreen else TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mode.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Integrations & Credentials
        item {
            SectionHeader(title = "External Integrations", tag = "API VAULT")
        }

        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CyberDark
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "YouTube Data API v3",
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite
                            )
                            Text(
                                text = if (apiKeyConfig.isYoutubeKeyVerified) "Live API Key Active" else "Sandbox Emulation Mode Active",
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = if (apiKeyConfig.isYoutubeKeyVerified) CyberGreen else CyberCyan
                            )
                        }
                        CyberButton(
                            text = "Configure",
                            onClick = onOpenApiKeyDialog,
                            isPrimary = false,
                            testTag = "config_api_keys_btn"
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Gmail OAuth Dispatch",
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite
                            )
                            Text(
                                text = "${gmailConfig.email} (Verified)",
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = CyberGreen
                            )
                        }
                        CyberButton(
                            text = "Manage",
                            onClick = onOpenGmailDialog,
                            isPrimary = false,
                            testTag = "manage_gmail_btn"
                        )
                    }
                }
            }
        }

        // Setup Instructions
        item {
            SectionHeader(title = "Platform Setup & Architecture Guide", tag = "DOCUMENTATION")
        }

        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CyberDark
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "1. AI AGENT SYSTEM (1,000 NODES)",
                        fontFamily = MonospaceFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Each agent has a persistent unique ID (AGT-0001 to AGT-1000) stored in local Room DB across 7 specialized cognitive clusters: Content Analyzers, Sentiment Observers, Retention Predictors, Demographic Simulators, SEO Scouts, Brand Safety, and Dispatch.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OffWhite
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "2. OFFICIAL YOUTUBE INTEGRATION SETUP",
                        fontFamily = MonospaceFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "To connect live Google Cloud production data:\n• Enable 'YouTube Data API v3' in Google Cloud Console.\n• Generate an API key and paste it under External Integrations.\n• XSOS will query live video metadata while running 1,000-agent simulations in strict private testing sandbox.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OffWhite
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "3. GMAIL OAUTH INTEGRATION SETUP",
                        fontFamily = MonospaceFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "• Uses standard Android Intent RFC mailto & OAuth account authorization.\n• Dispatches formatted multi-agent intelligence briefings directly to the operator's Google account.\n• Strictly compliant: zero mass creation, zero spam bypass.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OffWhite
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "4. WATCHDOG & SELF-HEALING ARCHITECTURE",
                        fontFamily = MonospaceFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "• Background coroutine watchdog continuously monitors health of all 1,000 nodes.\n• Automatic error recovery restarts failed nodes without app disruption.\n• Token-bucket rate limiter ensures API thresholds are never exceeded.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OffWhite
                    )
                }
            }
        }

        // Reboot / Reset System
        item {
            CyberButton(
                text = "Reboot Entire 1,000 Agent Core",
                onClick = { viewModel.restartAllAgents() },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = false,
                testTag = "reboot_system_btn"
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
