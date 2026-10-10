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
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuEscrowInfo
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistExtraBold
import com.example.muyu_app.ui.theme.UrbanistMedium
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(20.dp)
private val Muted = Color.Black.copy(alpha = 0.5f)
private val TitleMuted = Color.Black.copy(alpha = 0.6f)

@Composable
fun MerchantPlotDetailScreen(
    onBack: () -> Unit,
    onFundPlot: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val plot = LocalAppContainer.current.selectedPlot
    val plotName = plot?.name ?: stringResource(R.string.merchant_esperanza)
    val farmerName = plot?.ownerName?.ifBlank { null }
        ?: stringResource(R.string.merchant_esperanza_farmer)
    val crop = plot?.crop ?: stringResource(R.string.merchant_esperanza_crop)
    val area = plot?.area ?: stringResource(R.string.merchant_esperanza_area)
    val place = plot?.place ?: stringResource(R.string.merchant_esperanza_place)
    val amount = plot?.amount ?: stringResource(R.string.merchant_esperanza_price)
    val harvest = plot?.harvestDate ?: stringResource(R.string.merchant_harvest_date)
    val cycle = plot?.cycle ?: stringResource(R.string.merchant_cycle_value)
    val milestones = plot?.milestones?.ifBlank { null } ?: "4"
    val statusLabel = if (plot?.status == "funded") {
        stringResource(R.string.farmer_status_funded)
    } else {
        stringResource(R.string.merchant_available)
    }
    val farmerFirst = farmerName.substringBefore(" ").ifBlank { farmerName }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.merchant_plot_details),
                onBack = onBack,
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
                    .padding(top = 12.dp, bottom = MuyuScrollBottomInset),
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
        }
        MerchantBottomBar(
            selectedTab = MerchantTab.Catalog,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
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
