package com.example.muyu_app.features.plot_financing.presentation.evidence

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.core.plot.refreshSelectedPlot
import com.example.muyu_app.core.plot.rememberLiveFundedPlot
import com.example.muyu_app.features.labor_execution.presentation.evidence.PLOT_MATCH_METERS
import com.example.muyu_app.features.labor_execution.presentation.evidence.distanceToPlotMeters
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.features.plot_approval.domain.milestoneForEvidence
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val ConfirmPhotoShape = RoundedCornerShape(20.dp)
private val ConfirmChipShape = RoundedCornerShape(100.dp)
private val ConfirmChipBg = Color(0xFFFFF3E0)
private val ConfirmChipText = Color(0xFF774308)
private val ConfirmMuted = Color(0xFF8A8A8A)

@Composable
fun MerchantConfirmApprovalScreen(
    onBack: () -> Unit,
    onConfirmed: () -> Unit,
    onBackToEvidence: () -> Unit = onBack,
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
    val submittedAt = resolveMilestoneSubmittedAt(milestone, plot?.id, container.fakeStore, merchantEmail)
    val photoUri = milestone?.photoUri?.takeIf { it.isNotBlank() }
    val lat = milestone?.latitude
    val lng = milestone?.longitude
    val distance = if (plot != null && lat != null && lng != null) {
        distanceToPlotMeters(lat, lng, plot)
    } else {
        null
    }
    val locationOk = distance == null || distance <= PLOT_MATCH_METERS
    val farmerName = plot?.ownerName.orEmpty()
    val farmerFirst = farmerName.substringBefore(" ").ifBlank { farmerName }
    val escrow = plot?.let { computeEscrowSummary(it, milestoneKey) }
    val releaseLabel = escrow?.releaseLabel
        ?: plot?.id?.let { container.fakeStore.milestoneReleaseAmountFor(it, milestoneKey) }.orEmpty()
        .ifBlank { "S/ 0" }
    val escrowBefore = escrow?.balanceLabel ?: releaseLabel
    val escrowAfter = escrow?.afterReleaseLabel ?: "S/ 0"
    val fullContract = plot?.amount?.takeIf { it.isNotBlank() }
        ?: plot?.let { PlotFinance.formatAmount(PlotFinance.parseAmount(it.amount)) }
        ?: "S/ 0"
    val sentLabel = if (submittedAt > 0L) {
        formatEvidenceLongDateTime(submittedAt, locale)
    } else {
        "—"
    }
    val gpsLine = when {
        lat != null && lng != null && milestone?.accuracyMeters != null -> stringResource(
            R.string.merchant_gps_with_accuracy,
            PlotFinance.formatCoordinates(lat, lng),
            milestone.accuracyMeters ?: 0,
        )
        lat != null && lng != null -> PlotFinance.formatCoordinates(lat, lng)
        else -> stringResource(R.string.merchant_gps_not_recorded)
    }
    val locationTitle = when {
        lat == null || lng == null -> stringResource(R.string.merchant_gps_not_recorded)
        locationOk -> stringResource(R.string.merchant_location_inside)
        else -> stringResource(R.string.merchant_location_outside)
    }

    MerchantReviewScaffold(
        title = stringResource(R.string.merchant_confirm_approval_title),
        onBack = onBack,
        onTabSelected = onTabSelected,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .clip(ConfirmChipShape)
                .background(ConfirmChipBg)
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
                text = stringResource(R.string.merchant_awaiting_review_chip, milestoneTitle),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = ConfirmChipText,
                ),
            )
        }

        ReviewSoftCard {
            Text(
                text = "$milestoneTitle · ${plot?.name.orEmpty()}",
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color.Black,
                ),
            )
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(168.dp)
                        .clip(ConfirmPhotoShape),
                    contentScale = ContentScale.Crop,
                )
            }
            ReviewSoftRow(
                label = stringResource(R.string.merchant_photo_sent),
                value = sentLabel,
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_input_used),
                value = milestone?.inputUsed?.ifBlank { null } ?: "—",
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_application_rate),
                value = milestone?.amountApplied?.ifBlank { null } ?: "—",
            )
        }

        ReviewSoftCard {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MuyuAsset(
                    fileName = "publish/pin.svg",
                    contentDescription = null,
                    width = 16.dp,
                    height = 20.dp,
                )
                Text(
                    text = locationTitle,
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                    ),
                )
            }
            Text(
                text = gpsLine,
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = ConfirmMuted,
                ),
            )
        }

        ReviewSoftCard {
            Text(
                text = stringResource(R.string.merchant_installment_only, milestoneTitle),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_release_to, farmerFirst.ifBlank { "—" }),
                value = releaseLabel,
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_escrow_before),
                value = escrowBefore,
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_escrow_after),
                value = escrowAfter,
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_full_contract),
                value = fullContract,
            )
            Text(
                text = stringResource(
                    R.string.merchant_recipient_contract,
                    farmerName.ifBlank { "—" },
                ),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = ConfirmMuted,
                ),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ReviewPillButton(
                text = stringResource(R.string.merchant_approve_release_amount, releaseLabel),
                background = MuyuNavBar,
                contentColor = Color.White,
                bordered = false,
                onClick = {
                    val plotId = plot?.id
                    if (plotId != null) {
                        container.fakeStore.reviewMilestone(plotId, milestoneKey, approved = true)
                        container.refreshSelectedPlot()
                    }
                    container.reviewCompletedAt = System.currentTimeMillis()
                    onConfirmed()
                },
            )
            ReviewPillButton(
                text = stringResource(R.string.merchant_back_to_evidence),
                background = Color.White,
                contentColor = Color.Black,
                bordered = true,
                onClick = onBackToEvidence,
            )
        }
    }
}
