package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentStatus
import com.example.ui.MainViewModel
import com.example.ui.components.AgentDetailDialog
import com.example.ui.components.ApiKeyDialog
import com.example.ui.components.GmailAuthDialog
import com.example.ui.components.TaskCreateDialog
import com.example.ui.screens.AgentsScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.YouTubeScreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderActive
import com.example.ui.theme.CyberDark
import com.example.ui.theme.MonospaceFont
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.XSOSTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XSOSTheme {
                XSOSApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun XSOSApp(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedAgent by viewModel.selectedAgent.collectAsState()
    val gmailConfig by viewModel.gmailConfig.collectAsState()
    val apiKeyConfig by viewModel.apiKeyConfig.collectAsState()

    var showTaskDialog by remember { mutableStateOf(false) }
    var showGmailDialog by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }

    // Predictive Back Handling
    BackHandler(enabled = selectedAgent != null || showTaskDialog || showGmailDialog || showApiKeyDialog || currentTab != 0) {
        when {
            selectedAgent != null -> viewModel.selectAgent(null)
            showTaskDialog -> showTaskDialog = false
            showGmailDialog -> showGmailDialog = false
            showApiKeyDialog -> showApiKeyDialog = false
            currentTab != 0 -> viewModel.setTab(0)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .statusBarsPadding(),
        bottomBar = {
            XSOSBottomNavigation(
                selectedTab = currentTab,
                onSelectTab = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onOpenTaskScheduler = { showTaskDialog = true },
                    onOpenGmailDialog = { showGmailDialog = true }
                )
                1 -> AgentsScreen(viewModel = viewModel)
                2 -> YouTubeScreen(viewModel = viewModel)
                3 -> TasksScreen(
                    viewModel = viewModel,
                    onOpenTaskScheduler = { showTaskDialog = true }
                )
                4 -> AnalyticsScreen(viewModel = viewModel)
                5 -> SettingsScreen(
                    viewModel = viewModel,
                    onOpenGmailDialog = { showGmailDialog = true },
                    onOpenApiKeyDialog = { showApiKeyDialog = true }
                )
            }
        }
    }

    // Agent Detail Dialog
    if (selectedAgent != null) {
        AgentDetailDialog(
            agent = selectedAgent!!,
            onDismiss = { viewModel.selectAgent(null) },
            onStart = { id -> viewModel.setAgentStatus(id, AgentStatus.ACTIVE) },
            onPause = { id -> viewModel.setAgentStatus(id, AgentStatus.PAUSED) },
            onRestart = { id -> viewModel.setAgentStatus(id, AgentStatus.ACTIVE) },
            onInjectChaos = { id -> viewModel.injectChaosError(id) }
        )
    }

    // Task Creation Dialog
    if (showTaskDialog) {
        TaskCreateDialog(
            onDismiss = { showTaskDialog = false },
            onSchedule = { title, type, priority, cluster, count ->
                viewModel.scheduleTask(title, type, priority, cluster, count)
            }
        )
    }

    // Gmail Auth Dialog
    if (showGmailDialog) {
        GmailAuthDialog(
            config = gmailConfig,
            onDismiss = { showGmailDialog = false },
            onSave = { email, autoDaily ->
                viewModel.updateGmailConfig(email, autoDaily)
            },
            onDispatchTest = {
                // context handled inside MainViewModel
            }
        )
    }

    // API Key Dialog
    if (showApiKeyDialog) {
        ApiKeyDialog(
            config = apiKeyConfig,
            onDismiss = { showApiKeyDialog = false },
            onSave = { ytKey, geminiKey ->
                viewModel.updateApiKeys(ytKey, geminiKey)
            }
        )
    }
}

@Composable
fun XSOSBottomNavigation(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit
) {
    val navItems = listOf(
        Triple("Dash", Icons.Default.Dashboard, 0),
        Triple("Agents", Icons.Default.Group, 1),
        Triple("YouTube", Icons.Default.PlayArrow, 2),
        Triple("Tasks", Icons.Default.Schedule, 3),
        Triple("Metrics", Icons.Default.Analytics, 4),
        Triple("System", Icons.Default.Settings, 5)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PureBlack)
            .border(1.dp, CardBorder)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { (label, icon, index) ->
                val isSelected = selectedTab == index
                NavItemButton(
                    label = label,
                    icon = icon,
                    isSelected = isSelected,
                    onClick = { onSelectTab(index) },
                    testTag = "nav_tab_$index"
                )
            }
        }
    }
}

@Composable
private fun NavItemButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag(testTag)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) PureWhite else TextMuted,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            fontFamily = MonospaceFont,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PureWhite else TextMuted
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 2.dp)
                    .background(PureWhite, RoundedCornerShape(1.dp))
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
