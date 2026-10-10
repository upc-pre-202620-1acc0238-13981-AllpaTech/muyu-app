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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.plot.liveFundedPlot
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(16.dp)

@Composable
fun SelectMilestoneScreen(
    onBack: () -> Unit,
    onMilestoneSelected: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    val plot = remember { container.liveFundedPlot() }
    val states = plot?.milestoneStates.orEmpty().ifEmpty {
        MilestoneCatalog.seed(plot?.stage.orEmpty(), plot?.progress ?: 0)
    }
    val online by rememberNetworkOnline(context)
    val year = PlotFinance.seasonYear(plot?.harvestDate.orEmpty())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.evidence_select_title),
                onBack = onBack,
                trailing = { ConnectionChip(online = online) },
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
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
                    val state = states.find { it.key == definition.key }
                        ?: MilestoneState(key = definition.key)
                    val enabled = state.canRecord
                    val completed = state.isApproved || state.isSubmitted
                    MilestoneChoice(
                        key = definition.key,
                        title = stringResource(definition.titleRes),
                        hint = stringResource(definition.hintRes),
                        completed = completed,
                        enabled = enabled,
                        correction = state.needsCorrection,
                        onClick = {
                            container.selectedMilestoneKey = definition.key
                            onMilestoneSelected()
                        },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
        MuyuBottomBar(
            selectedTab = MuyuTab.Home,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
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
    key: String,
    title: String,
    hint: String,
    completed: Boolean,
    enabled: Boolean,
    correction: Boolean,
    onClick: () -> Unit,
) {
    val border = when {
        completed -> Color.Black.copy(alpha = 0.08f)
        enabled -> MuyuForest
        else -> Color.Black.copy(alpha = 0.08f)
    }

    // Mapeo directo a las imágenes de res/drawable
    val iconDrawableRes = when {
        key.contains("sowing", ignoreCase = true) || key.contains("plant", ignoreCase = true) -> R.drawable.payment04
        key.contains("irrigation", ignoreCase = true) -> R.drawable.payment02
        key.contains("fertiliz", ignoreCase = true) -> R.drawable.payment01
        else -> R.drawable.payment03
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(if (completed || !enabled) Color(0xFFF4F4F4) else Color.White)
            .border(1.dp, border, CardShape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Carga el recurso desde res/drawable
        Image(
            painter = painterResource(iconDrawableRes),
            contentDescription = title,
            modifier = Modifier.size(56.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (completed) Color.Black.copy(alpha = 0.45f) else Color.Black,
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
        if (completed) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MuyuApprovedBg),
                contentAlignment = Alignment.Center,
            ) {
                MuyuAsset(
                    fileName = "publish/check.svg",
                    contentDescription = null,
                    width = 18.dp,
                    height = 18.dp,
                )
            }
        } else if (correction) {
            Box(
                modifier = Modifier
                    .clip(ChipShape)
                    .background(MuyuPendingBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text = stringResource(R.string.plot_needs_correction),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontSize = 11.sp,
                        color = MuyuPendingText,
                    ),
                )
            }
        }
    }
}