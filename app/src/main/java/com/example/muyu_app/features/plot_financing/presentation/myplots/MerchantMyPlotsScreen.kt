package com.example.muyu_app.features.plot_financing.presentation.myplots

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.di.MerchantEvidenceOrigin
import com.example.muyu_app.core.environment.Environment
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuHeader
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val Muted = Color.Black.copy(alpha = 0.5f)

@Composable
fun MerchantMyPlotsScreen(
    onPlotEvidenceClick: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    var plots by remember { mutableStateOf<List<Plot>>(emptyList()) }
    var reviewFirst by remember { mutableStateOf(true) }
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()

    LaunchedEffect(lifecycleState.value) {
        if (!lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) return@LaunchedEffect
        val email = container.fakeStore.currentUser?.email
            ?: Environment.DemoCredentials.MERCHANT_EMAIL
        container.getPlotsUseCase().onSuccess { all ->
            plots = all
                .filter { it.fundedByEmail.equals(email, ignoreCase = true) && it.status == "funded" }
                .mapNotNull { plot ->
                    container.fakeStore.ensureMilestones(plot.id)?.let { container.fakeStore.toDomain(it) }
                }
        }
    }

    val displayPlots = remember(plots, reviewFirst) {
        if (reviewFirst) {
            plots.sortedWith(compareBy({ it.sortPriorityNeedsReview() }, { it.name }))
        } else {
            plots.sortedBy { it.name }
        }
    }
    val cuscoCount = remember(plots) { cuscoFundedPlots(plots).size }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MuyuHeader)
                    .statusBarsPadding()
                    .padding(horizontal = 30.dp)
                    .padding(top = 16.dp, bottom = 20.dp),
            ) {
                Text(
                    text = stringResource(R.string.nav_my_plots),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.merchant_active_funded_plots, plots.size),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color(0xFFFEFEFE),
                    ),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 30.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                if (plots.isNotEmpty() && cuscoCount > 0) {
                    RainRiskBanner(
                        plotCount = cuscoCount,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                ReviewFirstToggle(
                    enabled = reviewFirst,
                    onToggle = { reviewFirst = !reviewFirst },
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (displayPlots.isEmpty()) {
                    Text(
                        text = stringResource(R.string.merchant_no_funded),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 16.sp,
                            color = Muted,
                        ),
                    )
                } else {
                    displayPlots.forEach { plot ->
                        FundedPlotCard(
                            plot = plot,
                            onClick = {
                                container.selectedPlot = plot
                                container.selectedMilestoneKey = plot.evidenceMilestoneKey()
                                container.merchantEvidenceOrigin = MerchantEvidenceOrigin.MyPlots
                                onPlotEvidenceClick()
                            },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        MerchantBottomBar(
            selectedTab = MerchantTab.MyPlots,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun RainRiskBanner(plotCount: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(93.dp)
            .clip(CardShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF162A50), Color(0xFF524FA0)),
                ),
            )
            .border(1.dp, Color(0xFF524FA0), CardShape)
            .padding(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MuyuAsset(
                fileName = "weather/check.svg",
                contentDescription = null,
                width = 32.dp,
                height = 32.dp,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.merchant_rain_risk_title),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFFEFEFE),
                    ),
                )
                Text(
                    text = stringResource(R.string.merchant_rain_risk_weather),
                    style = TextStyle(
                        fontFamily = UrbanistLight,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                        color = Color(0xFFFEFEFE),
                    ),
                )
            }
        }
    }
}

@Composable
private fun ReviewFirstToggle(enabled: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.merchant_show_review_first),
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 16.sp,
                color = Color.Black,
            ),
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (enabled) MuyuAction else Color.LightGray)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.merchant_filter_yes),
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                ),
            )
        }
    }
}

@Composable
private fun FundedPlotCard(plot: Plot, onClick: () -> Unit) {
    val approved = plot.approvedMilestoneCount()
    val total = MilestoneCatalog.all.size
    val progress = plot.progress.coerceIn(0, 100)
    val needsReview = plot.needsMerchantReview()
    val milestoneKey = plot.currentMilestoneLabelKey()
    val milestoneTitle = milestoneKey?.let { key ->
        stringResource(MilestoneCatalog.definition(key).titleRes)
    }.orEmpty()
    val nextDue = plot.nextPendingMilestone()?.dueLabel?.ifBlank { null }
        ?: plot.nextPendingMilestone()?.extensionDate?.ifBlank { null }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = plot.name,
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
            )
            StatusChip(
                label = if (needsReview) {
                    stringResource(R.string.merchant_in_progress)
                } else {
                    stringResource(R.string.merchant_status_funded_chip)
                },
                needsReview = needsReview,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${plot.crop} · ${plot.ownerName.ifBlank { "—" }}",
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 16.sp,
                color = Muted,
            ),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.merchant_milestones_fraction, approved, total),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 16.sp,
                    color = Muted,
                ),
            )
            Text(
                text = "$progress%",
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 16.sp,
                    color = Muted,
                ),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        ProgressBar(progress = progress)
        Spacer(modifier = Modifier.height(10.dp))
        if (needsReview && milestoneTitle.isNotBlank()) {
            Text(
                text = stringResource(R.string.merchant_current_milestone),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 16.sp,
                    color = Muted,
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = milestoneTitle,
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 16.sp,
                        color = Color.Black,
                    ),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFED2BD))
                        .clickable(onClick = onClick)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = stringResource(R.string.merchant_review_evidence),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFA65E12),
                        ),
                    )
                }
            }
        } else if (milestoneTitle.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row {
                    Text(
                        text = stringResource(R.string.merchant_next_milestone),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 16.sp,
                            color = Muted,
                        ),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = milestoneTitle,
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 16.sp,
                            color = Color.Black,
                        ),
                    )
                }
                if (!nextDue.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFE6E6E6).copy(alpha = 0.9f))
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = nextDue,
                            style = TextStyle(
                                fontFamily = UrbanistBold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Muted,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(label: String, needsReview: Boolean) {
    val bg = if (needsReview) Color(0xFFFED2BD) else Color(0xFFC8DBFF)
    val fg = if (needsReview) Color(0xFFA65E12) else Color(0xFF3A5DD2)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = fg,
            ),
        )
    }
}

@Composable
private fun ProgressBar(progress: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE6E6E6).copy(alpha = 0.9f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress / 100f)
                .clip(RoundedCornerShape(20.dp))
                .background(MuyuHeader),
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun MerchantMyPlotsScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            MerchantMyPlotsScreen(onPlotEvidenceClick = {}, onTabSelected = {})
        }
    }
}
