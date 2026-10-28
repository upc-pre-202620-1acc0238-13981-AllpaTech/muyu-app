package com.example.muyu_app.features.plot_financing.presentation.evidence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import com.example.muyu_app.features.plot_approval.domain.milestoneForEvidence
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val PhotoShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(100.dp)
private val FieldShape = RoundedCornerShape(20.dp)
private val ChipBg = Color(0xFFFFF3E0)
private val ChipText = Color(0xFF774308)

private enum class RejectReason {
    Photo,
    Input,
    Other,
}

@Composable
fun MerchantRejectionReasonScreen(
    onBack: () -> Unit,
    onConfirmed: () -> Unit,
    onCancelAndReview: () -> Unit = onBack,
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
    val sentLabel = if (submittedAt > 0L) {
        formatEvidenceLongDateTime(submittedAt, locale)
    } else {
        "—"
    }
    val gpsShort = when {
        lat == null || lng == null -> stringResource(R.string.merchant_gps_not_recorded)
        locationOk -> stringResource(R.string.merchant_gps_valid_short)
        else -> stringResource(R.string.merchant_location_flagged)
    }
    val inputRate = listOfNotNull(
        milestone?.inputUsed?.ifBlank { null },
        milestone?.amountApplied?.ifBlank { null },
    ).joinToString(" · ").ifBlank { "—" }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    var comment by rememberSaveable { mutableStateOf("") }
    val selectedReason = selected?.let { runCatching { RejectReason.valueOf(it) }.getOrNull() }
    val reasonPhoto = stringResource(R.string.merchant_reject_reason_photo)
    val reasonInput = stringResource(R.string.merchant_reject_reason_input_amount)
    val reasonOther = stringResource(R.string.merchant_reject_reason_other)
    val fixPhoto = stringResource(R.string.merchant_reject_fix_photo)
    val fixInput = stringResource(R.string.merchant_reject_fix_input)
    val fixOther = stringResource(R.string.merchant_reject_fix_other)
    val options = listOf(
        RejectReason.Photo to reasonPhoto,
        RejectReason.Input to reasonInput,
        RejectReason.Other to reasonOther,
    )
    val presets = setOf(reasonPhoto, reasonInput)
    val canConfirm = selectedReason != null && comment.isNotBlank()

    MerchantReviewScaffold(
        title = stringResource(R.string.merchant_rejection_reason_title),
        onBack = onBack,
        onTabSelected = onTabSelected,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .clip(ChipShape)
                .background(ChipBg)
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
                    color = ChipText,
                ),
            )
        }

        ReviewSoftCard {
            Text(
                text = stringResource(
                    R.string.merchant_farmer_evidence,
                    farmerFirst.ifBlank { "—" },
                    plot?.name.orEmpty(),
                ),
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
                        .clip(PhotoShape),
                    contentScale = ContentScale.Crop,
                )
            }
            Text(
                text = stringResource(R.string.merchant_date_gps, sentLabel, gpsShort),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 14.sp,
                    color = ReviewSoftMuted,
                ),
            )
            ReviewSoftRow(
                label = stringResource(R.string.merchant_input_rate),
                value = inputRate,
            )
        }

        ReviewSoftCard {
            Text(
                text = stringResource(R.string.merchant_choose_reason),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
            )
            options.forEach { (reason, label) ->
                RejectRadioRow(
                    text = label,
                    selected = selectedReason == reason,
                    onClick = {
                        selected = reason.name
                        if (reason == RejectReason.Other) {
                            if (comment in presets) comment = ""
                        } else if (comment.isBlank() || comment in presets) {
                            comment = label
                        }
                    },
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.merchant_reason_for_farmer, farmerFirst.ifBlank { "—" }),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
                    .clip(FieldShape)
                    .background(Color.White)
                    .border(1.dp, ReviewSoftOutline, FieldShape)
                    .padding(16.dp),
            ) {
                if (comment.isBlank()) {
                    Text(
                        text = stringResource(R.string.merchant_reject_comment_hint),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = ReviewSoftMuted,
                        ),
                    )
                }
                BasicTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.Black,
                    ),
                    cursorBrush = SolidColor(Color.Black),
                )
            }
            Text(
                text = stringResource(R.string.merchant_reject_comment_help),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = ReviewSoftMuted,
                ),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ReviewSoftShape)
                .background(ChipBg)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MuyuAsset(
                fileName = "merchant/info_warn.svg",
                contentDescription = null,
                width = 22.dp,
                height = 22.dp,
            )
            Text(
                text = stringResource(R.string.merchant_amount_remains_held, releaseLabel),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 14.sp,
                    color = ChipText,
                ),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ReviewPillButton(
                text = stringResource(R.string.merchant_confirm_rejection),
                background = MuyuNavBar,
                contentColor = Color.White,
                bordered = false,
                enabled = canConfirm,
                onClick = {
                    val extra = comment.trim()
                    val reasonText = when (selectedReason) {
                        RejectReason.Photo -> reasonPhoto
                        RejectReason.Input -> reasonInput
                        RejectReason.Other -> extra
                        null -> extra
                    }
                    val correction = when (selectedReason) {
                        RejectReason.Photo -> fixPhoto
                        RejectReason.Input -> fixInput
                        RejectReason.Other, null -> extra.ifBlank { fixOther }
                    }
                    container.rejectionReason = if (
                        selectedReason != null &&
                        selectedReason != RejectReason.Other &&
                        extra.isNotBlank() &&
                        extra != reasonText
                    ) {
                        "$reasonText. $extra"
                    } else {
                        reasonText
                    }
                    container.rejectionCorrection = correction
                    val plotId = plot?.id
                    if (plotId != null) {
                        container.fakeStore.reviewMilestone(plotId, milestoneKey, approved = false)
                        container.refreshSelectedPlot()
                    }
                    container.reviewCompletedAt = System.currentTimeMillis()
                    onConfirmed()
                },
            )
            ReviewPillButton(
                text = stringResource(R.string.merchant_cancel_and_review),
                background = Color.White,
                contentColor = Color.Black,
                bordered = true,
                onClick = onCancelAndReview,
            )
        }
    }
}

@Composable
private fun RejectRadioRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, if (selected) MuyuAction else ReviewSoftMuted, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MuyuAction),
                )
            }
        }
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color.Black,
            ),
        )
    }
}
