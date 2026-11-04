package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentCluster
import com.example.data.model.AgentEntity
import com.example.data.model.AgentStatus
import com.example.ui.MainViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusPill
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
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted

@Composable
fun AgentsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val filteredAgents by viewModel.filteredAgents.collectAsState()
    val totalCount = filteredAgents.size
    val currentCluster by viewModel.filterCluster.collectAsState()
    val currentStatus by viewModel.filterStatus.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 16.dp)
            .testTag("agents_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        text = "AGENT DIRECTORY",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PureWhite
                    )
                    Text(
                        text = "1,000 INDEPENDENT MULTI-AGENT UNITS",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
                Text(
                    text = "$totalCount SHOWN",
                    fontFamily = MonospaceFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = CyberGreen
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "Search by ID (e.g. AGT-0042) or designation...",
                        fontFamily = MonospaceFont,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("agent_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PureWhite,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = OffWhite
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = MonospaceFont)
            )
        }

        // Cluster Filter Chips
        item {
            Text(
                text = "SPECIALIZED CLUSTERS",
                fontFamily = MonospaceFont,
                fontSize = 10.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ClusterChip(
                    label = "ALL (1,000)",
                    isSelected = currentCluster == "ALL",
                    onClick = { viewModel.setFilterCluster("ALL") }
                )
                AgentCluster.values().forEach { cluster ->
                    ClusterChip(
                        label = cluster.displayName,
                        isSelected = currentCluster == cluster.name,
                        onClick = { viewModel.setFilterCluster(cluster.name) }
                    )
                }
            }
        }

        // Status Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusFilterChip(
                    label = "ALL STATES",
                    isSelected = currentStatus == "ALL",
                    onClick = { viewModel.setFilterStatus("ALL") }
                )
                AgentStatus.values().forEach { status ->
                    StatusFilterChip(
                        label = status.label.uppercase(),
                        isSelected = currentStatus == status.name,
                        onClick = { viewModel.setFilterStatus(status.name) }
                    )
                }
            }
        }

        // Agent List Cards
        items(
            items = filteredAgents.take(80), // efficient pagination in lazy column
            key = { it.id }
        ) { agent ->
            AgentCardItem(
                agent = agent,
                onClick = { viewModel.selectAgent(agent) },
                onStart = { viewModel.setAgentStatus(agent.id, AgentStatus.ACTIVE) },
                onPause = { viewModel.setAgentStatus(agent.id, AgentStatus.PAUSED) },
                onRestart = { viewModel.setAgentStatus(agent.id, AgentStatus.ACTIVE) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ClusterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(if (isSelected) PureWhite else CyberDark, RoundedCornerShape(4.dp))
            .border(1.dp, if (isSelected) PureWhite else CardBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontFamily = MonospaceFont,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PureBlack else OffWhite
        )
    }
}

@Composable
private fun StatusFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(if (isSelected) SurfaceVariantDark else PureBlack, RoundedCornerShape(4.dp))
            .border(1.dp, if (isSelected) CardBorderActive else CardBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontFamily = MonospaceFont,
            fontSize = 9.sp,
            color = if (isSelected) PureWhite else TextMuted
        )
    }
}

@Composable
private fun AgentCardItem(
    agent: AgentEntity,
    onClick: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onRestart: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("agent_card_${agent.id}"),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = agent.id,
                        fontFamily = MonospaceFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${agent.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
                StatusPill(status = agent.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = agent.cluster.replace("_", " "),
                fontFamily = MonospaceFont,
                fontSize = 10.sp,
                color = CyberCyan
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Success: ${agent.successRate}% | Completed: ${agent.completedTasks} | Load: ${agent.loadPercent}%",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CyberButton(
                    text = "Inspect",
                    onClick = onClick,
                    modifier = Modifier.weight(1f),
                    isPrimary = false,
                    testTag = "inspect_button_${agent.id}"
                )
                if (agent.status == AgentStatus.ACTIVE.name) {
                    CyberButton(
                        text = "Pause",
                        onClick = onPause,
                        modifier = Modifier.weight(1f),
                        isPrimary = false,
                        testTag = "pause_button_${agent.id}"
                    )
                } else {
                    CyberButton(
                        text = "Start",
                        onClick = onStart,
                        modifier = Modifier.weight(1f),
                        isPrimary = true,
                        testTag = "start_button_${agent.id}"
                    )
                }
            }
        }
    }
}
