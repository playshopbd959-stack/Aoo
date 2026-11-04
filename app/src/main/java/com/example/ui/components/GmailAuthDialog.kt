package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.GoogleAccountEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderActive
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

@Composable
fun GmailAuthDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onDispatchTest: () -> Unit
) {
    val accounts by viewModel.allAccounts.collectAsState()
    var newEmail by remember { mutableStateOf("") }
    var newDisplayName by remember { mutableStateOf("") }
    var showAddForm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberDark,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorderActive, RoundedCornerShape(12.dp))
                .testTag("google_account_dialog")
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
                            text = "GOOGLE ACCOUNT GATEWAY",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = MonospaceFont,
                            color = PureWhite
                        )
                        Text(
                            text = "OFFICIAL OAUTH2 MULTI-ACCOUNT VAULT",
                            fontFamily = MonospaceFont,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_google_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = OffWhite)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Compliance Safeguard Banner
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SurfaceDark,
                    borderColor = CardBorder
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = CyberGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OAUTH USER DATA POLICY COMPLIANT",
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Authenticates legitimate existing Google accounts. Zero fake identity creation. Never used to manipulate YouTube engagement or views.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = OffWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Connected Accounts List Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "CONNECTED ACCOUNTS (${accounts.size})",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = if (showAddForm) "CANCEL" else "+ ADD ACCOUNT",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        modifier = Modifier
                            .clickable { showAddForm = !showAddForm }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Account items
                accounts.forEach { acc ->
                    CyberCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        backgroundColor = SurfaceDark
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = acc.email,
                                    fontFamily = MonospaceFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = PureWhite
                                )
                                Text(
                                    text = "${acc.displayName} • ${if (acc.isConnected) "Active" else "Revoked"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (acc.isConnected) CyberGreen else CyberRed
                                )
                                Text(
                                    text = "Scopes: youtube.readonly, gmail.send",
                                    fontFamily = MonospaceFont,
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            }
                            IconButton(
                                onClick = { viewModel.removeGoogleAccount(acc.email) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove Account",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Add Account Form
                if (showAddForm) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        placeholder = { Text("operator@gmail.com", color = TextMuted, fontSize = 11.sp) },
                        label = { Text("Google / Gmail Email", color = TextMuted, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PureWhite,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = OffWhite
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = MonospaceFont)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newDisplayName,
                        onValueChange = { newDisplayName = it },
                        placeholder = { Text("Operator Name / Team", color = TextMuted, fontSize = 11.sp) },
                        label = { Text("Display Label", color = TextMuted, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PureWhite,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = OffWhite
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = MonospaceFont)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CyberButton(
                        text = "Authorize & Link Google Account",
                        onClick = {
                            if (newEmail.isNotBlank()) {
                                viewModel.addGoogleAccount(newEmail, newDisplayName)
                                newEmail = ""
                                newDisplayName = ""
                                showAddForm = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isPrimary = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Briefing Dispatch Action
                CyberButton(
                    text = "Dispatch Agent Intelligence Briefing",
                    onClick = onDispatchTest,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true,
                    testTag = "dispatch_briefing_button"
                )
            }
        }
    }
}
