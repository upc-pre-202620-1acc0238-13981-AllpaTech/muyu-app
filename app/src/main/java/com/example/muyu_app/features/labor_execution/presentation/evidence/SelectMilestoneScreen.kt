package com.example.muyu_app.features.labor_execution.presentation.evidence

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(16.dp)
private val ChipShape = RoundedCornerShape(16.dp)

@Composable
fun SelectMilestoneScreen(
    onBack: () -> Unit,
    onMilestoneSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val selectedPlotId = container.selectedPlot?.id
    val plot = remember(selectedPlotId) {
        selectedPlotId
            ?.let(container.fakeStore::plotById)
            ?.let(container.fakeStore::toDomain)
    }
    val states = plot?.milestoneStates.orEmpty()
    val year = PlotFinance.seasonYear(plot?.harvestDate.orEmpty())
    val unavailableMessage = when {
        selectedPlotId == null ->
            R.string.evidence_plot_not_selected_title to R.string.evidence_plot_not_selected_body
        plot == null ->
            R.string.evidence_plot_load_error_title to R.string.evidence_plot_load_error_body
        !plot.status.equals("funded", ignoreCase = true) ->
            R.string.evidence_plot_not_funded_title to R.string.evidence_plot_not_funded_body
        states.isEmpty() ->
            R.string.evidence_milestones_missing_title to R.string.evidence_milestones_missing_body
        !MilestoneCatalog.all.all { definition -> states.any { it.key == definition.key } } ->
            R.string.evidence_milestones_incomplete_title to R.string.evidence_milestones_incomplete_body
        else -> null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEAF9E9)),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.evidence_select_title),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp),
            ) {
                if (unavailableMessage != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CardShape)
                            .background(Color.White)
                            .border(1.dp, Color.Black.copy(alpha = 0.08f), CardShape)
                            .padding(16.dp),
                    ) {
                        Text(
                            text = stringResource(unavailableMessage.first),
                            style = TextStyle(
                                fontFamily = UrbanistSemiBold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.Black,
                            ),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(unavailableMessage.second),
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.55f),
                            ),
                        )
                    }
                } else {
                    Text(
                        text = stringResource(
                            R.string.plot_meta_short,
                            plot?.name.orEmpty(),
                            plot?.crop.orEmpty(),
                        ) + "  ·  " + stringResource(R.string.evidence_season, year),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Color.Black.copy(alpha = 0.55f),
                        ),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MilestoneCatalog.all.forEach { definition ->
                        val state = states.first { it.key == definition.key }
                        MilestoneChoice(
                            state = state,
                            title = stringResource(definition.titleRes),
                            hint = stringResource(definition.hintRes),
                            onClick = {
                                container.selectedMilestoneKey = definition.key
                                onMilestoneSelected()
                            },
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectionChip(online: Boolean) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(Color.White.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = stringResource(if (online) R.string.sync_connected_chip else R.string.sync_offline),
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF424242),
            ),
        )
    }
}

@Composable
private fun MilestoneChoice(
    state: MilestoneState,
    title: String,
    hint: String,
    onClick: () -> Unit,
) {
    val enabled = state.canRecord
    val statusLabel = when (state.status) {
        MilestoneState.STATUS_APPROVED -> stringResource(R.string.plot_approved)
        MilestoneState.STATUS_SUBMITTED -> stringResource(R.string.plot_status_submitted)
        MilestoneState.STATUS_QUEUED -> stringResource(R.string.plot_status_queued)
        MilestoneState.STATUS_NEEDS_CORRECTION -> stringResource(R.string.plot_needs_correction)
        MilestoneState.STATUS_PENDING -> stringResource(R.string.plot_status_pending_milestone)
        else -> state.status
    }
    val statusBackground = when (state.status) {
        MilestoneState.STATUS_APPROVED -> MuyuApprovedBg
        MilestoneState.STATUS_NEEDS_CORRECTION -> MuyuPendingBg
        else -> Color(0xFFF1F1F1)
    }
    val statusColor = when (state.status) {
        MilestoneState.STATUS_APPROVED -> MuyuForest
        MilestoneState.STATUS_NEEDS_CORRECTION -> MuyuPendingText
        else -> Color(0xFF555555)
    }
    val border = when {
        state.isApproved -> Color.Black.copy(alpha = 0.08f)
        enabled -> MuyuForest
        else -> Color.Black.copy(alpha = 0.08f)
    }
    val iconDrawableRes = milestoneImage(state.key)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(if (!enabled) Color(0xFFF4F4F4) else Color.White)
            .border(1.dp, border, CardShape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (iconDrawableRes != null) {
            Image(
                painter = painterResource(iconDrawableRes),
                contentDescription = title,
                modifier = Modifier.size(44.dp),
            )
        } else {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = title,
                tint = Color(0xFF777777),
                modifier = Modifier.size(44.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
            )
            Text(
                text = hint,
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = Color.Black.copy(alpha = 0.45f),
                ),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(ChipShape)
                .background(statusBackground)
                .padding(horizontal = 8.dp, vertical = 5.dp),
        ) {
            Text(
                text = statusLabel,
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontSize = 11.sp,
                    color = statusColor,
                ),
            )
        }
    }
}

private fun milestoneImage(key: String): Int? = when (key.lowercase()) {
    MilestoneState.KEY_PLANTING -> R.drawable.milestone_planting
    MilestoneState.KEY_IRRIGATION -> R.drawable.milestone_irrigation
    MilestoneState.KEY_FERTILIZATION -> R.drawable.milestone_fertilization
    MilestoneState.KEY_HARVEST -> R.drawable.milestone_harvest
    else -> null
}