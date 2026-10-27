package com.example.muyu_app.features.labor_execution.presentation.evidence

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.PlotPhoto
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.plot.rememberLiveFundedPlot
import com.example.muyu_app.features.plot_approval.domain.currentMilestone
import com.example.muyu_app.features.plot_approval.domain.DisbursementStep
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.features.plot_approval.presentation.myplots.formatPlotArea
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuCalendarBg
import com.example.muyu_app.ui.theme.MuyuFinancedBg
import com.example.muyu_app.ui.theme.MuyuFinancedText
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuHeader
import com.example.muyu_app.ui.theme.MuyuMutedChip
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistExtraBold
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(20.dp)

@Composable
fun FundedPlotScreen(
    onBack: () -> Unit,
    onRecordEvidence: () -> Unit,
    onRequestExtension: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    selectedBottomTab: MuyuTab = MuyuTab.Home,
    modifier: Modifier = Modifier,
) {
    val plot = rememberLiveFundedPlot()
    val isFunded = plot?.status.equals(
        "funded",
        ignoreCase = true,
    )
    val name = plot?.name.orEmpty()
    val states = if (isFunded) {
        plot?.milestoneStates.orEmpty()
    } else {
        emptyList()
    }
    val currentKey = plot.currentMilestone()?.key
    val totalAmount = PlotFinance.parseAmount(plot?.amount.orEmpty())
    val disbursements = PlotFinance.disbursement(totalAmount)
    val cycleLabel = PlotFinance.cycleLabel(plot?.harvestDate.orEmpty())
    val fundedLabel = stringResource(R.string.plot_status_financed)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEAF9E9)),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            FundedPlotHeader(
                title = stringResource(R.string.home_plot_title_fmt, name),
                onBack = onBack,
            )
            PlotPhoto(
                imageUri = plot?.imageUri,
                description = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 19.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "🇵🇪",
                        fontSize = 18.sp,
                    )
                    Text(
                        text = "PERU",
                        fontFamily = UrbanistRegular,
                        fontSize = 12.sp,
                        color = Color(0xFF555555),
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(
                        R.string.home_plot_status_fmt,
                        name,
                        if (isFunded) fundedLabel else plot?.status.orEmpty(),
                    ),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 21.sp,
                        color = MuyuForest,
                    ),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${plot?.crop.orEmpty()} · ${formatPlotArea(plot?.area.orEmpty())}",
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black.copy(alpha = 0.5f),
                    ),
                )
                Text(
                    text = stringResource(
                        R.string.plot_subtitle_place_cycle_fmt,
                        plot?.place.orEmpty(),
                        cycleLabel,
                    ),
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black.copy(alpha = 0.5f),
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (isFunded) {
                    CycleMilestonesCard(
                        states = states,
                        currentKey = currentKey,
                        disbursements = disbursements,
                        totalFormatted = PlotFinance.formatAmount(totalAmount),
                        harvestDate = plot?.harvestDate.orEmpty(),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PlotActionButton(
                        text = stringResource(R.string.plot_record_evidence),
                        background = MuyuAction,
                        contentColor = Color.White,
                        extraBold = true,
                        onClick = onRecordEvidence,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.plot_request_extension),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onRequestExtension)
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontFamily = UrbanistSemiBold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MuyuOlive,
                        ),
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = MuyuForest,
                                shape = RoundedCornerShape(20.dp),
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.home_plot_tracking_title),
                            fontFamily = UrbanistSemiBold,
                            fontSize = 16.sp,
                            color = MuyuForest,
                        )
                        Text(
                            text = stringResource(R.string.home_plot_not_funded),
                            fontFamily = UrbanistRegular,
                            fontSize = 14.sp,
                            color = Color(0xFF555555),
                        )
                        Text(
                            text = stringResource(R.string.home_plot_milestones_unavailable),
                            fontFamily = UrbanistRegular,
                            fontSize = 14.sp,
                            color = Color(0xFF555555),
                        )
                    }
                }
            }
        }
        MuyuBottomBar(
            selectedTab = selectedBottomTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun FundedPlotHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MuyuHeader)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = title,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
            ),
        )
    }
}

@Composable
private fun CycleMilestonesCard(
    states: List<MilestoneState>,
    currentKey: String?,
    disbursements: List<DisbursementStep>,
    totalFormatted: String,
    harvestDate: String,
) {
    val currentIndex = MilestoneCatalog.all.indexOfFirst { it.key == currentKey }.coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Text(
            text = stringResource(R.string.plot_milestones_amount_fmt, totalFormatted),
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.6f),
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        MilestoneCatalog.all.forEachIndexed { index, definition ->
            val state = states.find { it.key == definition.key } ?: MilestoneState(key = definition.key)
            val title = if (definition.key == MilestoneState.KEY_PLANTING) {
                stringResource(R.string.plot_sowing)
            } else {
                stringResource(definition.titleRes)
            }
            val amount = PlotFinance.formatAmount(disbursements.getOrNull(index)?.amount ?: 0.0)
            val statusLabel = milestoneStatusLabel(state, index, currentIndex)
            val detail = milestoneDetailLine(
                state = state,
                index = index,
                currentIndex = currentIndex,
                amount = amount,
                harvestDate = harvestDate,
                definitionKey = definition.key,
            )
            if (index > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color.Black.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(10.dp))
            }
            Text(
                text = stringResource(
                    R.string.plot_milestone_line_fmt,
                    index + 1,
                    title,
                    statusLabel,
                ),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = detail,
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.5f),
                ),
            )
        }
    }
}

@Composable
private fun milestoneStatusLabel(
    state: MilestoneState,
    index: Int,
    currentIndex: Int,
): String = when {
    state.isApproved -> stringResource(R.string.plot_approved)
    state.isSubmitted -> stringResource(R.string.plot_status_submitted)
    state.needsCorrection -> stringResource(R.string.plot_needs_correction)
    index == currentIndex -> stringResource(R.string.plot_status_pending_milestone)
    index > currentIndex -> stringResource(R.string.plot_status_upcoming)
    else -> stringResource(R.string.plot_status_pending_milestone)
}

@Composable
private fun milestoneDetailLine(
    state: MilestoneState,
    index: Int,
    currentIndex: Int,
    amount: String,
    harvestDate: String,
    definitionKey: String,
): String {
    return when {
        state.isApproved -> {
            val date = when {
                state.submittedAtMillis > 0L -> PlotFinance.shortDateFromMillis(state.submittedAtMillis)
                state.recordedAt.isNotBlank() -> PlotFinance.shortDate(state.recordedAt)
                state.dueLabel.isNotBlank() -> state.dueLabel
                else -> PlotFinance.shortDate()
            }
            stringResource(R.string.plot_milestone_detail_fmt, date, amount)
        }
        state.isSubmitted || state.isQueued -> {
            stringResource(R.string.plot_milestone_detail_fmt, stringResource(R.string.plot_status_submitted), amount)
        }
        index > currentIndex -> {
            val estimated = when {
                definitionKey == MilestoneState.KEY_HARVEST && harvestDate.isNotBlank() ->
                    PlotFinance.shortDate(harvestDate)
                state.dueLabel.isNotBlank() -> state.dueLabel
                else -> stringResource(R.string.plot_date_today)
            }
            stringResource(R.string.plot_milestone_upcoming_fmt, estimated, amount)
        }
        else -> stringResource(R.string.plot_evidence_not_recorded_fmt, amount)
    }
}

@Composable
private fun PlotActionButton(
    text: String,
    background: Color,
    contentColor: Color,
    extraBold: Boolean = false,
    bordered: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(CardShape)
            .background(background)
            .then(if (bordered) Modifier.border(1.dp, MuyuForest, CardShape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = if (extraBold) UrbanistExtraBold else UrbanistBold,
                fontWeight = if (extraBold) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = 20.sp,
                color = contentColor,
            ),
        )
    }
}

@Composable
private fun StatusChip(
    text: String,
    background: Color,
    contentColor: Color,
) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(background)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = contentColor,
            ),
        )
    }
}

private fun Plot?.currentMilestone(): MilestoneState? = this?.currentMilestone()
