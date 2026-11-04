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
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.data.model.PersonaSimulation
import com.example.data.model.RecommendationItem
import com.example.data.model.RetentionPoint
import com.example.data.model.TitleVariation
import com.example.data.model.VideoAnalysisEntity
import com.example.ui.MainViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.MetricBox
import com.example.ui.components.RetentionCurveChart
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CardBorder
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
                    text = "REAL YOUTUBE INTELLIGENCE",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PureWhite
                )
                Text(
                    text = "OFFICIAL API RETRIEVAL & REAL AGENT VIDEO AUDIT",
                    fontFamily = MonospaceFont,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        // Compliance & Ground-Truth Transparency Notice
        item {
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("youtube_compliance_card"),
                backgroundColor = CyberDark,
                borderColor = CyberGreen
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "DATA INTEGRITY: ZERO FABRICATED METRICS",
                        fontFamily = MonospaceFont,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Video metadata is retrieved live from official Google/YouTube endpoints (YouTube Data API v3 and oEmbed). Estimated values and analytical models are clearly marked as AI predictions.",
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
                        text = "QUICK TEST VIDEOS",
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
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CyberButton(
                        text = if (isAnalyzing) "Retrieving API & Running Agents..." else "Audit Video with 1,000 Agents",
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
                                text = "Querying live API & distributing shards to 1,000 agents...",
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

            // Video Info Header Card with Verification Badge
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CyberDark,
                    borderColor = if (analysis.isVerifiedApiData) CyberGreen else CardBorder
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (analysis.isVerifiedApiData) "● VERIFIED VIA OFFICIAL YOUTUBE API" else "● ESTIMATED / UNVERIFIED",
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (analysis.isVerifiedApiData) CyberGreen else CyberYellow
                            )
                            Text(
                                text = "ID: ${analysis.id}",
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

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
                            val viewStr = if (analysis.viewCount > 0) "${formatter.format(analysis.viewCount)} views" else "Views: API Key Needed"
                            val likeStr = if (analysis.likeCount > 0) "${formatter.format(analysis.likeCount)} likes" else "Likes: API Key Needed"
                            val cmtStr = if (analysis.commentCount > 0) "${formatter.format(analysis.commentCount)} cmts" else "Cmts: API Key Needed"

                            Text(
                                text = viewStr,
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = OffWhite
                            )
                            Text(
                                text = likeStr,
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = OffWhite
                            )
                            Text(
                                text = cmtStr,
                                fontFamily = MonospaceFont,
                                fontSize = 10.sp,
                                color = OffWhite
                            )
                        }
                    }
                }
            }

            // Real Agent Analytical Finding Cards
            item {
                SectionHeader(title = "Real Agent Analytical Findings", tag = "AI EVALUATION")
            }

            // Title Analysis
            item {
                AnalyticalCard(
                    title = "Title Evaluation",
                    score = "${analysis.titleAnalysisScore}/100",
                    description = analysis.titleFeedback,
                    accentColor = CyberCyan
                )
            }

            // Description Analysis
            item {
                AnalyticalCard(
                    title = "Description & SEO Scan",
                    score = "${analysis.descriptionAnalysisScore}/100",
                    description = analysis.descriptionFeedback,
                    accentColor = CyberGreen
                )
            }

            // Intro Weak Points
            if (analysis.introWeakPointWarning != null) {
                item {
                    AnalyticalCard(
                        title = "Introduction & Hook Pacing",
                        score = "${analysis.introPacingScore}/100",
                        description = analysis.introWeakPointWarning!!,
                        accentColor = CyberYellow
                    )
                }
            }

            // Retention Curve Chart (Clearly marked as AI Predictive Model)
            item {
                val retentionPoints = parseRetentionPoints(analysis.retentionJson)
                RetentionCurveChart(retentionPoints = retentionPoints)
            }

            // Title Variations (A/B Test Concepts)
            val variations = parseTitleVariations(analysis.abVariationsJson)
            if (variations.isNotEmpty()) {
                item {
                    SectionHeader(title = "Thumbnail & Title Variations", tag = "A/B TEST CONCEPTS")
                }
                variations.forEach { variation ->
                    item {
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
                                        text = variation.concept,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = variation.predictedCtrLift,
                                        fontFamily = MonospaceFont,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = variation.rationale,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // Actionable Recommendations
            val recommendations = parseRecommendations(analysis.recommendationsJson)
            if (recommendations.isNotEmpty()) {
                item {
                    SectionHeader(title = "Actionable Recommendations", tag = "OPTIMIZATION")
                }
                recommendations.forEach { rec ->
                    item {
                        CyberCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = CyberDark
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (rec.urgency == "HIGH") CyberRed else CyberCyan,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = rec.category,
                                        fontFamily = MonospaceFont,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PureBlack
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = rec.actionText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OffWhite,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Executive Briefing Email Action
            item {
                Spacer(modifier = Modifier.height(6.dp))
                CyberButton(
                    text = "Dispatch Complete Video Dossier via Gmail",
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
private fun AnalyticalCard(
    title: String,
    score: String,
    description: String,
    accentColor: androidx.compose.ui.graphics.Color
) {
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
                    text = title.uppercase(),
                    fontFamily = MonospaceFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = PureWhite
                )
                Text(
                    text = score,
                    fontFamily = MonospaceFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = OffWhite
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

private fun parseTitleVariations(json: String): List<TitleVariation> {
    if (json.isBlank()) return emptyList()
    return try {
        json.split(";").mapNotNull { part ->
            val tokens = part.split("|")
            if (tokens.size >= 3) {
                TitleVariation(concept = tokens[0], predictedCtrLift = tokens[1], rationale = tokens[2])
            } else null
        }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun parseRecommendations(json: String): List<RecommendationItem> {
    if (json.isBlank()) return emptyList()
    return try {
        json.split(";").mapNotNull { part ->
            val tokens = part.split("|")
            if (tokens.size >= 3) {
                RecommendationItem(category = tokens[0], urgency = tokens[1], actionText = tokens[2])
            } else null
        }
    } catch (e: Exception) {
        emptyList()
    }
}
