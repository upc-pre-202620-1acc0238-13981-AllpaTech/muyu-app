package com.example.muyu_app.features.plot_financing.presentation.fund

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuEscrowBg
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuInfoBlue
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuReceiptGreen
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBlack
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(18.dp)
private val ButtonShape = RoundedCornerShape(16.dp)
private val ChipShape = RoundedCornerShape(20.dp)
private val LabelGray = Color(0xFF777777)

@Composable
fun FundSuccessScreen(
    onBack: () -> Unit,
    onViewPlot: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val amount = LocalAppContainer.current.selectedPlot?.amount
        ?: stringResource(R.string.merchant_esperanza_price)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.merchant_success_title),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.merchant_success_mascot),
                    contentDescription = stringResource(R.string.merchant_funds_secured),
                    modifier = Modifier
                        .width(128.dp)
                        .height(120.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.merchant_funds_secured),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = UrbanistBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color(0xFF111111),
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.merchant_success_body),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black.copy(alpha = 0.5f),
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuForest, CardShape)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    ReceiptRow(
                        label = stringResource(R.string.merchant_amount_funded),
                        value = amount,
                        extraBold = true,
                    )
                    MuyuAsset("merchant/receipt_line.svg", null, 340.dp, 1.dp)
                    ReceiptRow(
                        label = stringResource(R.string.merchant_payment_method_label),
                        value = stringResource(R.string.merchant_card_masked),
                    )
                    MuyuAsset("merchant/receipt_line2.svg", null, 340.dp, 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.merchant_status),
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = LabelGray,
                            ),
                        )
                        Box(
                            modifier = Modifier
                                .height(23.dp)
                                .clip(ChipShape)
                                .background(MuyuApprovedBg)
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.merchant_held),
                                style = TextStyle(
                                    fontFamily = UrbanistBold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MuyuForest,
                                ),
                            )
                        }
                    }
                    MuyuAsset("merchant/receipt_line2.svg", null, 340.dp, 1.dp)
                    ReceiptRow(
                        label = stringResource(R.string.merchant_date),
                        value = stringResource(R.string.merchant_date_value),
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(MuyuEscrowBg)
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.merchant_release_note),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MuyuInfoBlue,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .clip(ButtonShape)
                        .background(MuyuOlive)
                        .clickable(onClick = onViewPlot),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.merchant_view_plot),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFFFFFEFE),
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, MuyuForest, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.merchant_download),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MuyuForest,
                        ),
                    )
                }
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
private fun ReceiptRow(
    label: String,
    value: String,
    extraBold: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = LabelGray,
            ),
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = if (extraBold) UrbanistBlack else UrbanistSemiBold,
                fontWeight = if (extraBold) FontWeight.Black else FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MuyuReceiptGreen,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun FundSuccessScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            FundSuccessScreen(onBack = {}, onViewPlot = {}, onTabSelected = {})
        }
    }
}
