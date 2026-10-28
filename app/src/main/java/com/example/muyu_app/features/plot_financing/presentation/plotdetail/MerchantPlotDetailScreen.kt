package com.example.muyu_app.features.plot_financing.presentation.plotdetail

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import coil3.compose.AsyncImage
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.di.MerchantEvidenceOrigin
import com.example.muyu_app.core.di.MerchantPlotReturn
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.features.plot_approval.presentation.myplots.formatPlotArea
import com.example.muyu_app.features.plot_financing.presentation.evidence.formatEvidenceTime
import com.example.muyu_app.features.plot_financing.presentation.myplots.evidenceMilestoneKey
import com.example.muyu_app.features.plot_financing.presentation.myplots.milestoneStatesOrSeed
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuEscrowInfo
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuLocateBg
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuSkip
import com.example.muyu_app.ui.theme.MuyuTermsBg
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistExtraBold
import com.example.muyu_app.ui.theme.UrbanistMedium
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(20.dp)
private val CtaShape = RoundedCornerShape(15.dp)
private val Muted = Color.Black.copy(alpha = 0.5f)
private val TitleMuted = Color.Black.copy(alpha = 0.6f)
private val SecondaryText = Color(0xFF4F5A50)

@Composable
fun MerchantPlotDetailScreen(
    onBack: () -> Unit,
    onFundPlot: () -> Unit,
    onViewEvidence: () -> Unit = {},
    onTabSelected: (MerchantTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    var plot by remember { mutableStateOf(container.selectedPlot) }
    LaunchedEffect(container.selectedPlot?.id) {
        val current = container.selectedPlot ?: return@LaunchedEffect
        val withMilestones = container.fakeStore.ensureMilestones(current.id)
            ?.let { container.fakeStore.toDomain(it) } ?: current
        plot = withMilestones
        container.selectedPlot = withMilestones
    }
    val plotName = plot?.name.orEmpty()
    val isFunded = plot?.status == "funded"
    val statusLabel = if (isFunded) {
        stringResource(R.string.merchant_status_funded_chip)
    } else {
        stringResource(R.string.merchant_available)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuLocateBg),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.merchant_plot_details),
                onBack = onBack,
                backgroundColor = MuyuNavBar,
            )
            if (!plot?.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = plot?.imageUri,
                    contentDescription = plotName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(239.dp),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.merchant_plot_hero),
                    contentDescription = plotName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(239.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                plot?.let { current ->
                    FundedPlotBody(
                        plot = current,
                        statusLabel = statusLabel,
                        funderName = container.fakeStore.currentUser?.displayName
                            ?.ifBlank { null }
                            ?: stringResource(R.string.merchant_default_name),
                        companyName = stringResource(R.string.merchant_company_name),
                        showHarvestReceipt = isFunded,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 88.dp)
                .height(55.dp)
                .clip(CtaShape)
                .background(MuyuAction)
                .clickable {
                    if (isFunded) {
                        plot?.let {
                            container.selectedMilestoneKey = it.evidenceMilestoneKey()
                            container.merchantEvidenceOrigin = MerchantEvidenceOrigin.PlotDetail
                        }
                        onViewEvidence()
                    } else {
                        onFundPlot()
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(
                    if (isFunded) R.string.merchant_view_evidence else R.string.merchant_fund_plot,
                ),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White,
                ),
            )
        }
        MerchantBottomBar(
            selectedTab = when (container.merchantPlotReturn) {
                MerchantPlotReturn.Catalog -> MerchantTab.Catalog
                MerchantPlotReturn.MyPlots -> MerchantTab.MyPlots
                MerchantPlotReturn.Alerts -> MerchantTab.Alerts
            },
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun AvailablePlotBody(
    plotName: String,
    statusLabel: String,
    crop: String,
    area: String,
    place: String,
    farmerName: String,
    farmerFirst: String,
    harvest: String,
    amount: String,
    cycle: String,
    milestones: String,
    onFundPlot: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = plotName,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 25.sp,
                color = Color.Black,
            ),
        )
        StatusChip(statusLabel)
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = "$crop  ·  $area  ·  $place",
        style = TextStyle(
            fontFamily = UrbanistRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Muted,
        ),
    )
    Spacer(modifier = Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .clip(CardShape)
            .background(MuyuAction)
            .border(1.dp, MuyuForest, CardShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.merchant_whatsapp_contact, farmerFirst),
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFF2F2F2),
                textDecoration = TextDecoration.Underline,
            ),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    FarmerCard(farmerName = farmerName)
    Spacer(modifier = Modifier.height(12.dp))
    LocationCard(place = place)
    Spacer(modifier = Modifier.height(12.dp))
    CropDetailsCard(crop = crop, area = area, harvest = harvest, amount = amount)
    Spacer(modifier = Modifier.height(12.dp))
    TraceabilityCard()
    Spacer(modifier = Modifier.height(12.dp))
    FundingSummaryCard(amount = amount, cycle = cycle, milestones = milestones)
    Spacer(modifier = Modifier.height(12.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(MuyuEscrowInfo)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Text(
            text = stringResource(R.string.merchant_escrow_note),
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Muted,
            ),
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    VisitCard()
    Spacer(modifier = Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(CardShape)
            .background(MuyuAction)
            .clickable(onClick = onFundPlot),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.merchant_fund_plot),
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color.White,
            ),
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = stringResource(R.string.merchant_request_visit),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = TextStyle(
            fontFamily = UrbanistSemiBold,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MuyuForest,
        ),
    )
}

@Composable
private fun FundedPlotBody(
    plot: Plot,
    statusLabel: String,
    funderName: String,
    companyName: String,
    showHarvestReceipt: Boolean,
) {
    val area = formatPlotArea(plot.area)
    val deposit = PlotFinance.parseAmount(plot.amount)
    val steps = PlotFinance.disbursement(deposit)
    val states = plot.milestoneStatesOrSeed()
    val released = states.mapIndexed { index, state ->
        if (state.isApproved) steps.getOrNull(index)?.amount ?: 0.0 else 0.0
    }.sum()
    val approvedCount = states.count { it.isApproved }
    val percent = if (states.isEmpty()) 0 else (approvedCount * 100) / states.size
    val harvestApproved = states.any {
        it.key == MilestoneState.KEY_HARVEST && it.isApproved
    }
    val paymentValues = steps.map { PlotFinance.formatAmount(it.amount) }.distinct().joinToString(" / ")

    DetailCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = plot.name,
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 21.sp,
                    color = MuyuForest,
                    lineHeight = 27.sp,
                ),
            )
            Spacer(modifier = Modifier.height(4.dp))
            StatusChip(statusLabel)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = listOf(plot.crop, area, plot.place).filter { it.isNotBlank() }.joinToString(" · "),
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 16.sp,
                color = Muted,
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(
                R.string.merchant_farmer_funder,
                plot.ownerName,
                funderName,
                companyName,
            ),
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 16.sp,
                color = Muted,
            ),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    DetailCard {
        Text(
            text = stringResource(R.string.merchant_financial_status),
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 21.sp,
                color = MuyuForest,
                lineHeight = 27.sp,
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        FinancePair(
            leftLabel = stringResource(R.string.merchant_escrow_deposit),
            leftValue = PlotFinance.formatAmount(deposit),
            rightLabel = stringResource(R.string.merchant_funding_date),
            rightValue = plot.publishedAt.ifBlank { PlotFinance.publishedToday() },
        )
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(MuyuForest.copy(alpha = 0.25f)),
        )
        FinancePair(
            leftLabel = stringResource(R.string.merchant_funds_released_label),
            leftValue = PlotFinance.formatAmount(released),
            rightLabel = stringResource(R.string.merchant_escrow_balance),
            rightValue = PlotFinance.formatAmount((deposit - released).coerceAtLeast(0.0)),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    DetailCard {
        Text(
            text = stringResource(
                R.string.merchant_approved_progress,
                approvedCount,
                states.size.coerceAtLeast(1),
                percent,
            ),
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        states.forEachIndexed { index, state ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MuyuForest.copy(alpha = 0.2f)),
                )
            }
            val stepAmount = steps.getOrNull(index)?.let { PlotFinance.formatAmount(it.amount) }.orEmpty()
            val title = stringResource(MilestoneCatalog.definition(state.key).titleRes)
            val date = if (state.submittedAtMillis > 0L) {
                java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.ENGLISH)
                    .format(java.util.Date(state.submittedAtMillis))
            } else {
                state.extensionDate.ifBlank {
                    state.dueLabel.ifBlank { state.recordedAt.ifBlank { plot.harvestDate } }
                }
            }
            val detail = when {
                state.isApproved -> stringResource(R.string.merchant_milestone_approved_line, date, stepAmount)
                state.isSubmitted || state.isQueued -> stringResource(
                    R.string.merchant_milestone_awaiting_line,
                    date,
                    formatEvidenceTime(state.submittedAtMillis),
                    stepAmount,
                )
                else -> stringResource(R.string.merchant_milestone_pending_line, date, stepAmount)
            }
            MilestoneProgressRow(approved = state.isApproved, title = title, detail = detail)
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    DetailCard {
        Text(
            text = stringResource(R.string.merchant_crop_details),
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        CropValueRow(stringResource(R.string.merchant_crop_label), plot.crop)
        Spacer(modifier = Modifier.height(8.dp))
        CropValueRow(stringResource(R.string.merchant_area_label), area)
        Spacer(modifier = Modifier.height(8.dp))
        CropValueRow(stringResource(R.string.merchant_harvest_label), plot.harvestDate)
        Spacer(modifier = Modifier.height(8.dp))
        CropValueRow(stringResource(R.string.merchant_payment_per_milestone), paymentValues)
    }
    Spacer(modifier = Modifier.height(16.dp))
    FundedFarmerCard(farmerName = plot.ownerName)
    Spacer(modifier = Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CtaShape)
            .background(Color(0xFFFFFEFE))
            .border(1.dp, MuyuForest, CtaShape)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.merchant_contact_farmer, plot.ownerName),
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MuyuAction,
            ),
        )
    }
    if (showHarvestReceipt && !harvestApproved) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CtaShape)
                .background(Color.White)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.merchant_harvest_receipt_pending),
                style = TextStyle(
                    fontFamily = UrbanistExtraBold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    lineHeight = 24.sp,
                ),
            )
        }
    }
}

@Composable
private fun DetailCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(20.dp),
        content = { content() },
    )
}

@Composable
private fun FinancePair(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = leftLabel,
                style = TextStyle(fontFamily = UrbanistRegular, fontSize = 16.sp, color = SecondaryText),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = leftValue,
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MuyuForest,
                ),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = rightLabel,
                style = TextStyle(fontFamily = UrbanistRegular, fontSize = 16.sp, color = SecondaryText),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = rightValue,
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MuyuForest,
                ),
            )
        }
    }
}

@Composable
private fun MilestoneProgressRow(
    approved: Boolean,
    title: String,
    detail: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (approved) MuyuApprovedBg else MuyuTermsBg)
                .then(
                    if (approved) Modifier else Modifier.border(2.dp, MuyuForest, RoundedCornerShape(12.dp)),
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (approved) {
                MuyuAsset(
                    fileName = "weather/check.svg",
                    contentDescription = null,
                    width = 14.dp,
                    height = 10.dp,
                )
            }
        }
        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = UrbanistMedium,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
            )
            Text(
                text = detail,
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 14.sp,
                    color = SecondaryText,
                ),
            )
        }
    }
}

@Composable
private fun CropValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = TextStyle(fontFamily = UrbanistRegular, fontSize = 16.sp, color = SecondaryText),
        )
        Text(
            text = value,
            style = TextStyle(fontFamily = UrbanistRegular, fontSize = 16.sp, color = Color.Black),
        )
    }
}

@Composable
private fun FundedFarmerCard(farmerName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Image(
            painter = painterResource(
                if (farmerName.contains("Carlos", ignoreCase = true)) {
                    R.drawable.merchant_carlos_sm
                } else {
                    R.drawable.merchant_maria
                },
            ),
            contentDescription = farmerName,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = farmerName,
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                ),
            )
            Text(
                text = stringResource(R.string.merchant_farmer_stats),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 16.sp,
                    color = SecondaryText,
                ),
            )
            Box(
                modifier = Modifier
                    .clip(ChipShape)
                    .background(MuyuSkip)
                    .padding(horizontal = 11.dp, vertical = 3.dp),
            ) {
                Text(
                    text = stringResource(R.string.merchant_approval_rate),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MuyuAction,
                    ),
                )
            }
        }
    }
}

@Composable
private fun StatusChip(text: String) {
    Box(
        modifier = Modifier
            .height(23.dp)
            .clip(ChipShape)
            .background(MuyuApprovedBg)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MuyuForest,
            ),
        )
    }
}

@Composable
private fun FarmerCard(farmerName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.merchant_maria),
            contentDescription = farmerName,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = farmerName,
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                ),
            )
            Text(
                text = stringResource(R.string.merchant_experience),
                style = TextStyle(
                    fontFamily = UrbanistMedium,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Muted,
                ),
            )
            Text(
                text = stringResource(R.string.merchant_completed_plots),
                style = TextStyle(
                    fontFamily = UrbanistMedium,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Muted,
                ),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .height(22.dp)
                    .clip(ChipShape)
                    .background(MuyuApprovedBg)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.merchant_approval),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MuyuAction,
                    ),
                )
            }
        }
    }
}

@Composable
private fun LocationCard(place: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.merchant_exact_location),
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = TitleMuted,
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .clip(CardShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.merchant_map),
                contentDescription = stringResource(R.string.merchant_gps),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MuyuAsset(
                    fileName = "merchant/pin.svg",
                    contentDescription = null,
                    width = 21.dp,
                    height = 20.dp,
                )
                Text(
                    text = place,
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MuyuForest,
                    ),
                )
            }
        }
    }
}

@Composable
private fun CropDetailsCard(
    crop: String,
    area: String,
    harvest: String,
    amount: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.merchant_crop_details),
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = TitleMuted,
            ),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            CropField(
                label = stringResource(R.string.merchant_crop_label),
                value = crop,
                modifier = Modifier.weight(1f),
            )
            CropField(
                label = stringResource(R.string.merchant_size_label),
                value = area,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            CropField(
                label = stringResource(R.string.merchant_harvest_label),
                value = harvest,
                modifier = Modifier.weight(1f),
            )
            CropField(
                label = stringResource(R.string.merchant_milestone_pay),
                value = amount,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CropField(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Muted,
            ),
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.Black,
            ),
        )
    }
}

@Composable
private fun TraceabilityCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.merchant_traceability),
                style = TextStyle(
                    fontFamily = UrbanistExtraBold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = TitleMuted,
                ),
            )
            Box(
                modifier = Modifier
                    .height(24.dp)
                    .clip(ChipShape)
                    .background(MuyuApprovedBg)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.merchant_documented),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MuyuAction,
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        TraceRow(stringResource(R.string.merchant_soil), stringResource(R.string.merchant_soil_value))
        Spacer(modifier = Modifier.height(4.dp))
        TraceRow(stringResource(R.string.merchant_inputs), stringResource(R.string.merchant_inputs_value))
        Spacer(modifier = Modifier.height(4.dp))
        TraceRow(stringResource(R.string.merchant_practices), stringResource(R.string.merchant_practices_value))
    }
}

@Composable
private fun TraceRow(label: String, value: String) {
    Row {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Muted,
            ),
        )
    }
}

@Composable
private fun FundingSummaryCard(
    amount: String,
    cycle: String,
    milestones: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.merchant_funding),
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = TitleMuted,
            ),
        )
        Spacer(modifier = Modifier.height(10.dp))
        SummaryRow(stringResource(R.string.merchant_total_amount), amount)
        MuyuAsset("merchant/line_summary.svg", null, 312.dp, 1.dp)
        SummaryRow(stringResource(R.string.merchant_cycle), cycle)
        MuyuAsset("merchant/line_summary2.svg", null, 312.dp, 1.dp)
        SummaryRow(stringResource(R.string.merchant_payment_milestones), "$milestones staged payments")
        MuyuAsset("merchant/line_summary2.svg", null, 312.dp, 1.dp)
        SummaryRow(stringResource(R.string.merchant_return), stringResource(R.string.merchant_return_value))
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Muted,
            ),
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = MuyuAction,
            ),
        )
    }
}

@Composable
private fun VisitCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(23.dp)
                    .clip(ChipShape)
                    .background(MuyuApprovedBg)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.merchant_visit_agreement),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MuyuAction,
                    ),
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.merchant_visit_price),
                style = TextStyle(
                    fontFamily = UrbanistExtraBold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    color = MuyuAction,
                ),
            )
        }
        Box(
            modifier = Modifier
                .width(148.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(MuyuAction),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.merchant_fund_agreement),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = UrbanistExtraBold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color.White,
                ),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun MerchantPlotDetailScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            MerchantPlotDetailScreen(onBack = {}, onFundPlot = {}, onTabSelected = {})
        }
    }
}
