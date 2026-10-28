package com.example.muyu_app.features.plot_financing.presentation.evidence

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.muyu_app.core.plot.rememberLiveFundedPlot
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.features.plot_approval.domain.milestoneForEvidence
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuCoverText
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val ApprovedChipShape = RoundedCornerShape(100.dp)

@Composable
fun MerchantMilestoneApprovedScreen(
    onBack: () -> Unit,
    onViewPlot: () -> Unit,
    onViewReceipt: () -> Unit,
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
    val escrow = plot?.let { computeEscrowSummary(it, milestoneKey) }
    val releaseLabel = escrow?.releaseLabel
        ?: plot?.id?.let { container.fakeStore.milestoneReleaseAmountFor(it, milestoneKey) }.orEmpty()
        .ifBlank { "S/ 0" }
    val totals = plot?.let { computeEscrowTotals(it) }
    val depositDate = plot?.publishedAt?.ifBlank { null } ?: PlotFinance.publishedToday()
    val plotLine = stringResource(
        R.string.merchant_plot_funded_line,
        plot?.name.orEmpty().ifBlank { "—" },
        stringResource(R.string.farmer_status_funded),
    )
    val transaction = mockTransactionId(milestoneKey, reviewedAt)

    MerchantReviewScaffold(
        title = stringResource(R.string.merchant_milestone_approval_title),
        onBack = onBack,
        onTabSelected = onTabSelected,
        modifier = modifier,
    ) {
        Text(
            text = dateLabel,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 14.sp,
                color = ReviewChipTextColor,
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
                    .background(ReviewMintCircle),
                contentAlignment = Alignment.Center,
            ) {
                MuyuAsset(
                    fileName = "merchant/circle_check_fill.svg",
                    contentDescription = null,
                    width = 36.dp,
                    height = 36.dp,
                )
            }
            Text(
                text = stringResource(R.string.merchant_milestone_approved_heading, milestoneTitle),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                ),
            )
            Text(
                text = stringResource(
                    R.string.merchant_released_to,
                    releaseLabel,
                    farmerName.ifBlank { "—" },
                ),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 14.sp,
                    color = ReviewSoftMuted,
                    textAlign = TextAlign.Center,
                ),
            )
            Box(
                modifier = Modifier
                    .clip(ApprovedChipShape)
                    .background(MuyuApprovedBg)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(
                    text = stringResource(R.string.merchant_status_approved),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MuyuCoverText,
                    ),
                )
            }
        }

        ReviewSoftCard {
            Text(
                text = stringResource(R.string.merchant_sample_receipt),
                style = cardTitleStyle(),
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_review_plot),
                value = plotLine,
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_mock_transaction),
                value = transaction,
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_verified_time),
                value = dateLabel,
            )
            Text(
                text = stringResource(
                    R.string.merchant_farmer_notified,
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

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ReviewPillButton(
                text = stringResource(R.string.merchant_view_plot),
                background = MuyuNavBar,
                contentColor = Color.White,
                bordered = false,
                onClick = onViewPlot,
            )
            ReviewPillButton(
                text = stringResource(R.string.merchant_view_receipt),
                background = Color.White,
                contentColor = Color.Black,
                bordered = true,
                onClick = onViewReceipt,
            )
        }
    }
}

private val ReviewChipTextColor = Color(0xFF774308)

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
