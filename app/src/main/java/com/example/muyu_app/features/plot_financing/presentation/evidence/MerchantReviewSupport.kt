package com.example.muyu_app.features.plot_financing.presentation.evidence

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import java.util.Calendar

internal val ReviewCardShape = RoundedCornerShape(20.dp)
internal val ReviewBadgeShape = RoundedCornerShape(8.dp)
internal val ReviewMuted = Color(0xFF4F5A50)
internal val ReviewCardOutline = Color(0xFFD6E2CF)
internal val ReviewBadgeFill = Color(0xFFFFF3E0)
internal val ReviewBadgeText = Color(0xFF9A5B00)
internal val ReviewWarningBg = Color(0xFFFED2BD)
internal val ReviewWarningText = Color(0xFF774308)
internal val ReviewRejectRed = Color(0xFF9E2F2F)
internal val ReviewRejectBanner = Color(0xFFF6C6C6)
internal val ReviewDisabled = Color(0xFFBDBDBD)
internal val ReviewSoftShape = RoundedCornerShape(24.dp)
internal val ReviewPillShape = RoundedCornerShape(100.dp)
internal val ReviewSoftMuted = Color(0xFF8A8A8A)
internal val ReviewSoftOutline = Color(0xFFE5E5E5)
internal val ReviewMintCircle = Color(0xFFD8F3C4)

data class EscrowSummary(
    val releaseLabel: String,
    val balanceLabel: String,
    val afterReleaseLabel: String,
    val remainingApprovedLabel: String,
)

fun computeEscrowSummary(plot: Plot, milestoneKey: String): EscrowSummary {
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
    val alreadyApproved = states.find { it.key == milestoneKey }?.status == MilestoneState.STATUS_APPROVED
    val balance = (total - released).coerceAtLeast(0.0)
    val after = if (alreadyApproved) balance else (balance - releaseAmount).coerceAtLeast(0.0)
    val remainingApproved = if (alreadyApproved) balance else after
    return EscrowSummary(
        releaseLabel = PlotFinance.formatAmount(releaseAmount),
        balanceLabel = PlotFinance.formatAmount(balance),
        afterReleaseLabel = PlotFinance.formatAmount(after),
        remainingApprovedLabel = PlotFinance.formatAmount(remainingApproved),
    )
}

fun milestoneReceiptNumber(plotId: Int, milestoneKey: String): String {
    val year = Calendar.getInstance().get(Calendar.YEAR)
    val index = MilestoneCatalog.all.indexOfFirst { it.key == milestoneKey }.coerceAtLeast(0) + 1
    val seq = (plotId * 10 + index).toString().padStart(3, '0')
    return "MY-REL-$year-$seq"
}

fun milestoneImageRes(key: String): Int = when (key.lowercase()) {
    MilestoneState.KEY_PLANTING -> R.drawable.milestone_planting
    MilestoneState.KEY_IRRIGATION -> R.drawable.milestone_irrigation
    MilestoneState.KEY_FERTILIZATION -> R.drawable.milestone_fertilization
    MilestoneState.KEY_HARVEST -> R.drawable.milestone_harvest
    else -> R.drawable.milestone_fertilization
}

@Composable
fun MerchantReviewScaffold(
    title: String,
    onBack: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val container = LocalAppContainer.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = title,
                onBack = onBack,
                backgroundColor = MuyuNavBar,
                eyebrow = eyebrow,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 32.dp, bottom = MuyuScrollBottomInset),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                content = content,
            )
        }
        MerchantBottomBar(
            selectedTab = container.merchantEvidenceBottomTab(),
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun ReviewSummaryCard(
    milestoneKey: String,
    milestoneTitle: String,
    plotName: String,
    farmerName: String,
    badgeText: String,
    badgeBackground: Color = ReviewBadgeFill,
    badgeColor: Color = ReviewBadgeText,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ReviewCardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, ReviewCardShape)
            .padding(20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(milestoneImageRes(milestoneKey)),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "$milestoneTitle · $plotName",
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                    ),
                )
                Text(
                    text = farmerName.ifBlank { "—" },
                    style = TextStyle(
                        fontFamily = UrbanistLight,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                        color = ReviewMuted,
                    ),
                )
            }
            Box(
                modifier = Modifier
                    .clip(ReviewBadgeShape)
                    .background(badgeBackground)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = badgeText,
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = badgeColor,
                    ),
                )
            }
        }
    }
}

@Composable
fun ReviewDataCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ReviewCardShape)
            .background(Color.White)
            .border(1.dp, ReviewCardOutline, ReviewCardShape)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

@Composable
fun ReviewDataRow(
    label: String,
    value: String,
    valueColor: Color = MuyuForest,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = ReviewMuted,
            ),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = valueColor,
                textAlign = TextAlign.End,
            ),
        )
    }
}

@Composable
fun ReviewInfoBanner(
    text: String,
    iconFile: String,
    background: Color = ReviewWarningBg,
    contentColor: Color = ReviewWarningText,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ReviewCardShape)
            .background(background)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        MuyuAsset(
            fileName = iconFile,
            contentDescription = null,
            width = 22.dp,
            height = 22.dp,
        )
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = contentColor,
            ),
        )
    }
}

@Composable
fun ReviewStatusBanner(
    text: String,
    iconFile: String,
    background: Color,
    contentColor: Color,
    iconSize: androidx.compose.ui.unit.Dp = 34.dp,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ReviewCardShape)
            .background(background)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MuyuAsset(
            fileName = iconFile,
            contentDescription = null,
            width = iconSize,
            height = iconSize,
        )
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = contentColor,
            ),
        )
    }
}

@Composable
fun ReviewPrimaryButton(
    text: String,
    background: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ReviewCardShape)
            .background(if (enabled) background else ReviewDisabled)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                color = Color.White,
            ),
        )
    }
}

@Composable
fun ReviewOutlineButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ReviewCardShape)
            .background(Color.White)
            .border(1.dp, MuyuAction, ReviewCardShape)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                color = MuyuAction,
            ),
        )
    }
}

@Composable
fun ReviewReasonOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ReviewCardShape)
            .background(Color.White)
            .border(1.dp, if (selected) MuyuForest else ReviewCardOutline, ReviewCardShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(2.dp, if (selected) MuyuAction else ReviewMuted, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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

@Composable
fun ReviewSoftCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ReviewSoftShape)
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

@Composable
fun ReviewSoftRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 14.sp,
                color = ReviewSoftMuted,
            ),
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 14.sp,
                color = MuyuForest,
            ),
        )
    }
}

@Composable
fun ReviewPillButton(
    text: String,
    background: Color,
    contentColor: Color,
    bordered: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val fill = if (enabled) background else Color(0xFFE8E8E8)
    val textColor = if (enabled) contentColor else Color(0xFF9A9A9A)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(ReviewPillShape)
            .background(fill)
            .then(
                if (bordered && enabled) Modifier.border(1.dp, ReviewSoftOutline, ReviewPillShape) else Modifier,
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = textColor,
            ),
        )
    }
}

fun mockTransactionId(milestoneKey: String, millis: Long): String {
    val abbr = when (milestoneKey.lowercase()) {
        MilestoneState.KEY_PLANTING -> "PLA"
        MilestoneState.KEY_IRRIGATION -> "IRR"
        MilestoneState.KEY_FERTILIZATION -> "FER"
        MilestoneState.KEY_HARVEST -> "HAR"
        else -> milestoneKey.take(3).uppercase()
    }
    val stamp = if (millis > 0L) {
        java.text.SimpleDateFormat("ddMMyy", java.util.Locale.US).format(java.util.Date(millis))
    } else {
        "000000"
    }
    return "MUY-$abbr-$stamp"
}

data class EscrowTotals(
    val approvedCount: Int,
    val totalCount: Int,
    val releasedLabel: String,
    val remainingLabel: String,
    val contractLabel: String,
)

fun computeEscrowTotals(plot: Plot): EscrowTotals {
    val total = PlotFinance.parseAmount(plot.amount)
    val steps = PlotFinance.disbursement(total)
    val approvedAmount = MilestoneCatalog.all.indices.sumOf { index ->
        val key = MilestoneCatalog.all[index].key
        val approved = plot.milestoneStates.find { it.key == key }?.status == MilestoneState.STATUS_APPROVED
        if (approved) steps.getOrNull(index)?.amount ?: 0.0 else 0.0
    }
    val approvedCount = plot.milestoneStates.count { it.status == MilestoneState.STATUS_APPROVED }
    return EscrowTotals(
        approvedCount = approvedCount,
        totalCount = MilestoneCatalog.all.size,
        releasedLabel = PlotFinance.formatAmount(approvedAmount),
        remainingLabel = PlotFinance.formatAmount((total - approvedAmount).coerceAtLeast(0.0)),
        contractLabel = PlotFinance.formatAmount(total),
    )
}

@Composable
fun ReviewActionColumn(
    primaryText: String,
    primaryColor: Color,
    primaryEnabled: Boolean = true,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ReviewPrimaryButton(
            text = primaryText,
            background = primaryColor,
            onClick = onPrimary,
            enabled = primaryEnabled,
        )
        ReviewOutlineButton(
            text = secondaryText,
            onClick = onSecondary,
        )
    }
}
