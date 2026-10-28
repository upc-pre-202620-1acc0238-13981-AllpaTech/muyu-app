package com.example.muyu_app.features.plot_financing.presentation.myplots

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuFinancedBg
import com.example.muyu_app.ui.theme.MuyuGrayChip
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistMedium
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(16.dp)
private val ChipShape = RoundedCornerShape(100.dp)
private val WeatherImageShape = RoundedCornerShape(12.dp)
private val Muted = Color.Black.copy(alpha = 0.5f)
private val CardOutline = Color(0xFFCAC4D0)
private val OnSurface = Color(0xFF1D1B20)
private val WarningText = Color(0xFF774308)
private val FundedText = Color(0xFF244398)
private val TempChipBg = Color(0xFFFEF7FF)
private val WeatherStart = Color(0xFF162A50)
private val WeatherEnd = Color(0xFF524FA0)

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
        val email = container.fakeStore.currentUser?.email.orEmpty()
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MuyuNavBar)
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
                        fontFamily = UrbanistMedium,
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
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = MuyuScrollBottomInset),
            ) {
                if (plots.isNotEmpty()) {
                    RainRiskBanner(
                        plotCount = plots.size,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                ReviewFirstToggle(
                    enabled = reviewFirst,
                    onCheckedChange = { reviewFirst = it },
                    modifier = Modifier.padding(horizontal = 16.dp),
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
    val body = if (plotCount == 1) {
        stringResource(R.string.merchant_rain_risk_body, plotCount)
    } else {
        stringResource(R.string.merchant_rain_risk_body_plural, plotCount)
    }
    Row(
        modifier = modifier
            .clip(CardShape)
            .background(Brush.horizontalGradient(listOf(WeatherStart, WeatherEnd)))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.weather_rain),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(WeatherImageShape),
            contentScale = ContentScale.Fit,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.merchant_rain_risk_title),
                style = TextStyle(
                    fontFamily = UrbanistMedium,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFFFEFEFE),
                ),
            )
            Text(
                text = body,
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.White,
                ),
            )
        }
        Box(
            modifier = Modifier
                .clip(ChipShape)
                .background(TempChipBg)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.merchant_rain_risk_temp),
                style = TextStyle(
                    fontFamily = UrbanistMedium,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = OnSurface,
                ),
            )
        }
    }
}

@Composable
private fun ReviewFirstToggle(
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(MuyuPageGray)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.merchant_show_review_first),
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = OnSurface,
            ),
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MuyuAction,
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = CardOutline,
                uncheckedBorderColor = Color.Transparent,
            ),
        )
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
            .border(1.dp, CardOutline, CardShape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plot.crop,
                        style = TextStyle(
                            fontFamily = UrbanistLight,
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp,
                            color = Muted,
                        ),
                    )
                    Text(
                        text = "  ·  ",
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 14.sp,
                            color = Muted,
                        ),
                    )
                    Text(
                        text = plot.ownerName.ifBlank { "—" },
                        style = TextStyle(
                            fontFamily = UrbanistLight,
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp,
                            color = Muted,
                        ),
                    )
                }
            }
            StatusChip(
                label = if (needsReview) {
                    stringResource(R.string.merchant_in_progress)
                } else {
                    stringResource(R.string.merchant_status_funded_chip)
                },
                needsReview = needsReview,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.merchant_milestones_fraction, approved, total),
                    style = MutedLabelStyle,
                )
                Text(
                    text = "$progress%",
                    style = MutedLabelStyle,
                )
            }
            ProgressBar(progress = progress)
        }
        if (needsReview && milestoneTitle.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.merchant_current_milestone),
                    style = MutedLabelStyle,
                )
                StatusChip(
                    label = stringResource(R.string.merchant_needs_your_review),
                    needsReview = true,
                )
                Text(
                    text = milestoneTitle,
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = OnSurface,
                    ),
                )
            }
        } else if (milestoneTitle.isNotBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.merchant_next_milestone),
                    style = MutedLabelStyle,
                )
                Text(
                    text = milestoneTitle,
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = OnSurface,
                    ),
                )
                if (!nextDue.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(ChipShape)
                            .background(MuyuGrayChip)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = nextDue,
                            style = TextStyle(
                                fontFamily = UrbanistMedium,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
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
    val bg = if (needsReview) MuyuPendingBg else MuyuFinancedBg
    val fg = if (needsReview) WarningText else FundedText
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
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
            .height(12.dp)
            .clip(ChipShape)
            .background(MuyuGrayChip),
    ) {
        if (progress > 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress / 100f)
                    .clip(ChipShape)
                    .background(MuyuAction),
            )
        }
    }
}

private val MutedLabelStyle = TextStyle(
    fontFamily = UrbanistRegular,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    color = Muted,
)

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
