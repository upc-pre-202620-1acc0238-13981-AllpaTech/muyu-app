package com.example.muyu_app.features.plot_financing.presentation.evidence

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.core.plot.refreshSelectedPlot
import com.example.muyu_app.core.plot.rememberLiveFundedPlot
import com.example.muyu_app.features.plot_approval.domain.milestoneForEvidence
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistExtraBold
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val BadgeShape = RoundedCornerShape(8.dp)
private val Muted = Color(0xFF4F5A50)
private val RedReject = Color(0xFFB84444)

@Composable
fun MerchantApproveRejectScreen(
    onBack: () -> Unit,
    onFinished: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    val plot = rememberLiveFundedPlot()
    val milestoneKey = container.selectedMilestoneKey
    val definition = MilestoneCatalog.definition(milestoneKey)
    val milestoneTitle = stringResource(definition.titleRes)
    val plotName = plot?.name.orEmpty()
    val farmerName = plot?.ownerName.orEmpty()
    val milestone = plot?.milestoneForEvidence(milestoneKey)
    val plotId = plot?.id
    val merchantEmail = container.fakeStore.currentUser?.email.orEmpty()
    val submittedAt = resolveMilestoneSubmittedAt(milestone, plotId, container.fakeStore, merchantEmail)
    val evidenceBadge = if (submittedAt > 0L) {
        stringResource(
            R.string.merchant_evidence_submitted_badge,
            formatEvidenceShortDate(submittedAt, context.resources.configuration.locales[0]),
            formatEvidenceTime(submittedAt),
        )
    } else {
        stringResource(R.string.merchant_sent_at_unknown, plotName)
    }
    val releaseAmount = plotId?.let { container.fakeStore.milestoneReleaseAmountFor(it, milestoneKey) }.orEmpty()
    val releaseLabel = releaseAmount.ifBlank { "S/ 0" }
    val escrow = plot?.let { computeEscrowSummary(it, milestoneKey) }
    val escrowBalance = escrow?.balanceLabel ?: releaseLabel
    val escrowAfter = escrow?.afterReleaseLabel ?: releaseLabel

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.merchant_approve_reject_title),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 32.dp, bottom = MuyuScrollBottomInset),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuForest, CardShape)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(
                                R.string.merchant_approve_summary_title,
                                milestoneTitle,
                                plotName,
                            ),
                            style = TextStyle(
                                fontFamily = UrbanistSemiBold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.Black,
                            ),
                        )
                        Text(
                            text = stringResource(
                                R.string.merchant_approve_farmer_line,
                                plotName,
                                farmerName,
                            ),
                            style = TextStyle(
                                fontFamily = UrbanistLight,
                                fontWeight = FontWeight.Light,
                                fontSize = 16.sp,
                                color = Muted,
                            ),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(BadgeShape)
                            .background(Color(0xFFFFF3E0))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = evidenceBadge,
                            style = TextStyle(
                                fontFamily = UrbanistSemiBold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color(0xFF9A5B00),
                            ),
                        )
                    }
                }

                DecisionOptionCard(
                    title = stringResource(R.string.merchant_approve_option),
                    body = stringResource(
                        R.string.merchant_approve_explainer,
                        releaseLabel,
                        escrowBalance,
                        escrowAfter,
                    ),
                    borderColor = MuyuForest,
                    leading = {
                        MuyuAsset(
                            fileName = "publish/check.svg",
                            contentDescription = null,
                            width = 24.dp,
                            height = 24.dp,
                        )
                    },
                )

                DecisionOptionCard(
                    title = stringResource(R.string.merchant_reject_option),
                    body = stringResource(R.string.merchant_reject_explainer, releaseLabel),
                    borderColor = RedReject,
                    leading = {
                        Text(
                            text = "!",
                            style = TextStyle(
                                fontFamily = UrbanistBold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = RedReject,
                            ),
                            modifier = Modifier.size(24.dp),
                        )
                    },
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryActionButton(
                        text = stringResource(R.string.merchant_approve_release),
                        background = MuyuAction,
                        onClick = {
                            if (plotId != null) {
                                container.fakeStore.reviewMilestone(plotId, milestoneKey, approved = true)
                                container.refreshSelectedPlot()
                            }
                            onFinished()
                        },
                    )
                    PrimaryActionButton(
                        text = stringResource(R.string.merchant_reject_explain),
                        background = RedReject,
                        onClick = {
                            if (plotId != null) {
                                container.fakeStore.reviewMilestone(plotId, milestoneKey, approved = false)
                                container.refreshSelectedPlot()
                            }
                            onFinished()
                        },
                    )
                }
            }
        }
        MerchantBottomBar(
            selectedTab = container.merchantEvidenceBottomTab(),
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

private data class EscrowSummary(
    val balanceLabel: String,
    val afterReleaseLabel: String,
)

private fun computeEscrowSummary(
    plot: com.example.muyu_app.features.plot_approval.domain.Plot,
    milestoneKey: String,
): EscrowSummary {
    val total = PlotFinance.parseAmount(plot.amount)
    val steps = PlotFinance.disbursement(total)
    val states = plot.milestoneStates
    val released = MilestoneCatalog.all.indices.sumOf { index ->
        val key = MilestoneCatalog.all[index].key
        val approved = states.find { it.key == key }?.status == MilestoneState.STATUS_APPROVED
        if (approved) steps.getOrNull(index)?.amount ?: 0.0 else 0.0
    }
    val releaseIndex = MilestoneCatalog.all.indexOfFirst { it.key == milestoneKey }.coerceAtLeast(0)
    val releaseAmount = steps.getOrNull(releaseIndex)?.amount ?: 0.0
    val balance = (total - released).coerceAtLeast(0.0)
    val after = (balance - releaseAmount).coerceAtLeast(0.0)
    return EscrowSummary(
        balanceLabel = PlotFinance.formatAmount(balance),
        afterReleaseLabel = PlotFinance.formatAmount(after),
    )
}

@Composable
private fun DecisionOptionCard(
    title: String,
    body: String,
    borderColor: Color,
    leading: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, borderColor, CardShape)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leading()
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                ),
            )
        }
        Text(
            text = body,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Muted,
                lineHeight = 22.sp,
            ),
        )
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    background: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(background)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color(0xFFFEFEFE),
            ),
        )
    }
}
