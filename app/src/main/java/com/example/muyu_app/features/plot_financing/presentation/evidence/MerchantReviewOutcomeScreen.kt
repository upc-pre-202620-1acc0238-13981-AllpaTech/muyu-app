package com.example.muyu_app.features.plot_financing.presentation.evidence

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.core.plot.merchantDisplayName
import com.example.muyu_app.core.plot.rememberLiveFundedPlot
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.features.plot_approval.domain.milestoneForEvidence
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val RejectChipShape = RoundedCornerShape(100.dp)
private val RejectPink = Color(0xFFFDECEC)
private val RejectTitle = Color(0xFF7A2A2A)
private val RejectChipText = Color(0xFF9E2F2F)

@Composable
fun MerchantReviewOutcomeScreen(
    onBack: () -> Unit,
    onViewMilestone: () -> Unit,
    onBackToPlot: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    val plot = rememberLiveFundedPlot()
    val milestoneKey = container.selectedMilestoneKey
    val definition = MilestoneCatalog.definition(milestoneKey)
    val milestoneTitle = stringResource(definition.titleRes)
    val milestone = plot?.milestoneForEvidence(milestoneKey)
    val locale = context.resources.configuration.locales[0]
    val merchantEmail = container.fakeStore.currentUser?.email.orEmpty()
    val reviewedAt = container.reviewCompletedAt.takeIf { it > 0L }
        ?: resolveMilestoneSubmittedAt(milestone, plot?.id, container.fakeStore, merchantEmail)
    val dateLabel = formatEvidenceLongDateTime(reviewedAt, locale)
    val farmerName = plot?.ownerName.orEmpty()
    val farmerFirst = farmerName.substringBefore(" ").ifBlank { farmerName }
    val trader = container.fakeStore.currentUser?.displayName?.ifBlank { null }
        ?: container.merchantDisplayName(plot)
    val reason = container.rejectionReason.ifBlank { "—" }
    val correction = container.rejectionCorrection.ifBlank { "—" }
    val escrow = plot?.let { computeEscrowSummary(it, milestoneKey) }
    val heldLabel = escrow?.releaseLabel
        ?: plot?.id?.let { container.fakeStore.milestoneReleaseAmountFor(it, milestoneKey) }.orEmpty()
        .ifBlank { "S/ 0" }
    val totals = plot?.let { computeEscrowTotals(it) }
    val depositDate = plot?.publishedAt?.ifBlank { null } ?: PlotFinance.publishedToday()
    val andWord = stringResource(R.string.merchant_list_and)
    val approvedNames = MilestoneCatalog.all.mapNotNull { item ->
        val approved = plot?.milestoneStates?.find { it.key == item.key }?.status ==
            MilestoneState.STATUS_APPROVED
        if (approved && item.key != milestoneKey) stringResource(item.titleRes) else null
    }
    val remainApproved = when {
        approvedNames.isEmpty() -> stringResource(R.string.merchant_no_disbursement_none)
        else -> stringResource(R.string.merchant_no_disbursement, joinWithAnd(approvedNames, andWord))
    }

    MerchantReviewScaffold(
        title = stringResource(R.string.merchant_review_outcome_title),
        onBack = onBack,
        onTabSelected = onTabSelected,
        eyebrow = stringResource(R.string.merchant_trader_eyebrow, trader),
        modifier = modifier,
    ) {
        Text(
            text = dateLabel,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 14.sp,
                color = Color(0xFF774308),
            ),
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(RejectPink),
                contentAlignment = Alignment.Center,
            ) {
                MuyuAsset(
                    fileName = "merchant/circle_x.svg",
                    contentDescription = null,
                    width = 34.dp,
                    height = 34.dp,
                )
            }
            Text(
                text = stringResource(R.string.merchant_milestone_rejected_heading, milestoneTitle),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = RejectTitle,
                    textAlign = TextAlign.Center,
                ),
            )
            Text(
                text = stringResource(
                    R.string.merchant_farmer_notified_rejection,
                    farmerFirst.ifBlank { "—" },
                ),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 14.sp,
                    color = ReviewSoftMuted,
                    textAlign = TextAlign.Center,
                ),
            )
            Row(
                modifier = Modifier
                    .clip(RejectChipShape)
                    .background(Color(0xFFFFF3E0))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MuyuAsset(
                    fileName = "merchant/clock.svg",
                    contentDescription = null,
                    width = 14.dp,
                    height = 14.dp,
                )
                Text(
                    text = stringResource(R.string.merchant_rejected_correction_chip),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color(0xFF774308),
                    ),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ReviewSoftShape)
                .background(RejectPink)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MuyuAsset(
                    fileName = "merchant/circle_alert.svg",
                    contentDescription = null,
                    width = 14.dp,
                    height = 14.dp,
                )
                Text(
                    text = stringResource(R.string.merchant_reason_sent_to, farmerFirst.ifBlank { "—" }),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = RejectChipText,
                    ),
                )
            }
            Text(
                text = reason,
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = RejectTitle,
                ),
            )
        }

        ReviewSoftCard {
            Text(
                text = stringResource(R.string.merchant_follow_up),
                style = cardTitleStyle(),
            )
            Text(
                text = correction,
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.Black,
                ),
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_installment_held),
                value = heldLabel,
            )
            Text(
                text = stringResource(
                    R.string.merchant_review_when_submitted,
                    farmerFirst.ifBlank { "—" },
                ),
                style = footerStyle(),
            )
        }

        ReviewSoftCard {
            Text(
                text = stringResource(R.string.merchant_financial_status),
                style = cardTitleStyle(),
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_approved_milestones),
                value = "${totals?.approvedCount ?: 0}/${totals?.totalCount ?: 4}",
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_total_released),
                value = totals?.releasedLabel ?: "S/ 0",
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_escrow_balance),
                value = totals?.remainingLabel ?: "S/ 0",
            )
            Text(
                text = stringResource(
                    R.string.merchant_deposit_line,
                    totals?.contractLabel ?: "S/ 0",
                    depositDate,
                ),
                style = footerStyle(),
            )
        }

        Text(
            text = remainApproved,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = ReviewSoftMuted,
            ),
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ReviewPillButton(
                text = stringResource(R.string.merchant_view_milestone),
                background = MuyuNavBar,
                contentColor = Color.White,
                bordered = false,
                onClick = onViewMilestone,
            )
            ReviewPillButton(
                text = stringResource(R.string.merchant_back_to_plot),
                background = Color.White,
                contentColor = Color.Black,
                bordered = true,
                onClick = onBackToPlot,
            )
        }
    }
}

private fun joinWithAnd(items: List<String>, andWord: String): String = when {
    items.size <= 1 -> items.firstOrNull().orEmpty()
    items.size == 2 -> "${items[0]}$andWord${items[1]}"
    else -> items.dropLast(1).joinToString(", ") + andWord + items.last()
}

private fun cardTitleStyle() = TextStyle(
    fontFamily = UrbanistSemiBold,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    color = Color.Black,
)

private fun footerStyle() = TextStyle(
    fontFamily = UrbanistRegular,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    color = ReviewSoftMuted,
)
