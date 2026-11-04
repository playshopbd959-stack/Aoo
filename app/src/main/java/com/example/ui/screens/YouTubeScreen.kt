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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CommentCluster
import com.example.data.model.PersonaSimulation
import com.example.data.model.RetentionPoint
import com.example.data.model.VideoAnalysisEntity
import com.example.ui.MainViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.MetricBox
import com.example.ui.components.RetentionCurveChart
import com.example.ui.components.SectionHeader
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
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import java.text.NumberFormat
import java.util.Locale

@Composable
fun YouTubeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var urlInput by remember { mutableStateOf("https://www.youtube.com/watch?v=dQw4w9WgXcQ") }
    val isAnalyzing by viewModel.isAnalyzingVideo.collectAsState()
    val analysisError by viewModel.videoAnalysisError.collectAsState()
    val currentAnalysis by viewModel.currentVideoAnalysis.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 16.dp)
            .testTag("youtube_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "YOUTUBE INTELLIGENCE",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PureWhite
                )
                Text(
                    text = "1,000-AGENT SIMULATED VIEWER BEHAVIOR SANDBOX",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        // Compliance Banner (Mandatory safety requirement)
        item {
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("youtube_compliance_card"),
                backgroundColor = CyberDark,
                borderColor = CardBorder
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "SAFETY PROTOCOL: ZERO FAKE ENGAGEMENT",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberGreen
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "XSOS is an analytical sandbox. Agents simulate cognitive viewer responses, retention drop-off, and sentiment in an isolated private test environment. Public metrics are never manipulated.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = OffWhite
                    )
                }
            }
        }

        // URL Input Card
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SurfaceDark
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "YOUTUBE VIDEO URL OR 11-CHAR ID",
                        fontFamily = MonospaceFont,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("youtube_url_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PureWhite,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = OffWhite
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = MonospaceFont)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Buttons
                    Text(
                        text = "PRESET TESTING LINKS",
                        fontFamily = MonospaceFont,
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PresetChip("Never Gonna Give You Up") {
                            urlInput = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
                        }
                        PresetChip("Me at the zoo") {
                            urlInput = "https://www.youtube.com/watch?v=jNQXAC9IVRw"
                        }
                        PresetChip("Gangnam Style") {
                            urlInput = "https://www.youtube.com/watch?v=9bZkp7q19f0"
                        }
                        PresetChip("Neural Systems (Essay)") {
                            urlInput = "https://www.youtube.com/watch?v=kY90vR6Zt1E"
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CyberButton(
                        text = if (isAnalyzing) "Simulating with 1,000 Agents..." else "Deploy 1,000 Agents On Video",
                        onClick = {
                            viewModel.analyzeYouTubeVideo(urlInput, 1000)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isPrimary = !isAnalyzing,
                        testTag = "analyze_video_button"
                    )

                    if (isAnalyzing) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = PureWhite,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Running parallel neural perception pipeline...",
                                fontFamily = MonospaceFont,
                                fontSize = 11.sp,
                                color = CyberCyan
                            )
                        }
                    }

                    if (analysisError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = analysisError!!,
                            fontFamily = MonospaceFont,
                            fontSize = 11.sp,
                            color = CyberRed
                        )
                    }
                }
            }
        }

        // Analysis Results
        if (currentAnalysis != null) {
            val analysis = currentAnalysis!!
            val formatter = NumberFormat.getNumberInstance(Locale.US)

            // Video Info Header Card
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CyberDark
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = analysis.thumbnailUrl,
                                contentDescription = "Video Thumbnail",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(110.dp)
                                    .aspectRatio(16f / 9f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = analysis.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PureWhite,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = analysis.channelTitle,
                                    fontFamily = MonospaceFont,
                                    fontSize = 11.sp,
                                    color = CyberCyan
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Duration: ${analysis.durationText}",
                                    fontFamily = MonospaceFont,
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Views: ${formatter.format(analysis.viewCount)}",
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = OffWhite
                            )
                            Text(
                                text = "Likes: ${formatter.format(analysis.likeCount)}",
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = OffWhite
                            )
                            Text(
                                text = "Comments: ${formatter.format(analysis.commentCount)}",
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = OffWhite
                            )
                        }
                    }
                }
            }

            // Key Scores
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricBox(
                        title = "Hook Score",
                        value = "${analysis.overallHookScore}/100",
                        subValue = "First 30s Retention",
                        indicatorColor = CyberGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Viral Index",
                        value = "${analysis.viralPotentialScore}/100",
                        subValue = "Algorithm Velocity",
                        indicatorColor = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Sentiment Ratio",
                        value = "${(analysis.sentimentRatio * 100).toInt()}%",
                        subValue = "Positive Valence",
                        indicatorColor = CyberGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Retention Curve Chart (Interactive Canvas)
            item {
                val retentionPoints = parseRetentionPoints(analysis.retentionJson)
                RetentionCurveChart(retentionPoints = retentionPoints)
            }

            // Multi-Persona Simulation Breakdown
            item {
                SectionHeader(title = "Simulated Persona Reactions", tag = "150 PERSONA AGENTS")
            }

            val personas = parsePersonas(analysis.personasJson)
            personas.forEach { persona ->
                item {
                    PersonaCard(persona = persona)
                }
            }

            // Comment Clusters
            item {
                SectionHeader(title = "Comment Topic Clusters", tag = "NLP SYNTHESIS")
            }

            val clusters = parseCommentClusters(analysis.commentClustersJson)
            clusters.forEach { cluster ->
                item {
                    CommentClusterCard(cluster = cluster)
                }
            }

            // Executive Briefing Email Action
            item {
                Spacer(modifier = Modifier.height(6.dp))
                CyberButton(
                    text = "Dispatch Intelligence Briefing via Gmail",
                    onClick = { viewModel.dispatchExecutiveBriefingViaGmail(context) },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true,
                    testTag = "email_video_report_button"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PresetChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(SurfaceDark, RoundedCornerShape(4.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontFamily = MonospaceFont,
            fontSize = 9.sp,
            color = OffWhite
        )
    }
}

@Composable
private fun PersonaCard(persona: PersonaSimulation) {
    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceDark
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = persona.persona.uppercase(),
                    fontFamily = MonospaceFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PureWhite
                )
                Text(
                    text = "Satisfaction: ${persona.score}/100",
                    fontFamily = MonospaceFont,
                    fontSize = 11.sp,
                    color = CyberGreen
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Watch Completion Estimate: ${persona.expectedCompletion}%",
                fontFamily = MonospaceFont,
                fontSize = 10.sp,
                color = CyberCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = persona.behavioralFeedback,
                style = MaterialTheme.typography.bodySmall,
                color = OffWhite
            )
        }
    }
}

@Composable
private fun CommentClusterCard(cluster: CommentCluster) {
    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = CyberDark
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = cluster.theme,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PureWhite
                )
                Text(
                    text = "${cluster.percentage}% Volume",
                    fontFamily = MonospaceFont,
                    fontSize = 11.sp,
                    color = CyberCyan
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tone: ${cluster.sentimentTone} – ${cluster.sampleSummary}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

private fun parseRetentionPoints(json: String): List<RetentionPoint> {
    if (json.isBlank()) return emptyList()
    return try {
        json.split(";").mapNotNull { part ->
            val tokens = part.split(":")
            if (tokens.size >= 2) {
                val sec = tokens[0].toIntOrNull() ?: 0
                val pct = tokens[1].toIntOrNull() ?: 50
                val note = if (tokens.size > 2) tokens[2] else null
                RetentionPoint(second = sec, retentionPercent = pct, note = note)
            } else null
        }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun parsePersonas(json: String): List<PersonaSimulation> {
    if (json.isBlank()) return emptyList()
    return try {
        json.split(";").mapNotNull { part ->
            val tokens = part.split("|")
            if (tokens.size >= 5) {
                PersonaSimulation(
                    persona = tokens[0],
                    sentiment = tokens[1],
                    score = tokens[2].toIntOrNull() ?: 70,
                    expectedCompletion = tokens[3].toIntOrNull() ?: 60,
                    behavioralFeedback = tokens[4]
                )
            } else null
        }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun parseCommentClusters(json: String): List<CommentCluster> {
    if (json.isBlank()) return emptyList()
    return try {
        json.split(";").mapNotNull { part ->
            val tokens = part.split("|")
            if (tokens.size >= 4) {
                CommentCluster(
                    theme = tokens[0],
                    percentage = tokens[1].toIntOrNull() ?: 25,
                    sentimentTone = tokens[2],
                    sampleSummary = tokens[3]
                )
            } else null
        }
    } catch (e: Exception) {
        emptyList()
    }
}
