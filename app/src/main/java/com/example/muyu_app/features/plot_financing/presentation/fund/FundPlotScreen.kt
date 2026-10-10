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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import com.example.muyu_app.ui.theme.FredokaSemiBold
import com.example.muyu_app.ui.theme.MuyuCheckbox
import com.example.muyu_app.ui.theme.MuyuEscrowInfo
import com.example.muyu_app.ui.theme.MuyuEscrowStep
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuFundBanner
import com.example.muyu_app.ui.theme.MuyuGrayChip
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
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
private val MethodShape = RoundedCornerShape(10.dp)
private val Muted = Color.Black.copy(alpha = 0.5f)
private val TitleMuted = Color.Black.copy(alpha = 0.6f)
private val SoftWhite = Color.White.copy(alpha = 0.7f)

private enum class PayMethod { Card, Wallet, Agent, Bank }

@Composable
fun FundPlotScreen(
    onBack: () -> Unit,
    onAcceptTerms: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val plot = container.selectedPlot
    val amount = plot?.amount ?: stringResource(R.string.merchant_esperanza_price)
    val plotName = plot?.name ?: stringResource(R.string.merchant_esperanza)
    val crop = plot?.crop ?: stringResource(R.string.merchant_esperanza_crop)
    val area = plot?.area ?: stringResource(R.string.merchant_esperanza_area)
    var method by remember { mutableStateOf(PayMethod.Card) }
    var accepted by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.merchant_fund_title),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(MuyuFundBanner)
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.merchant_total_investment),
                        style = TextStyle(
                            fontFamily = UrbanistMedium,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = SoftWhite,
                        ),
                    )
                    Text(
                        text = amount,
                        style = TextStyle(
                            fontFamily = FredokaSemiBold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 32.sp,
                            color = Color.White,
                        ),
                    )
                    Text(
                        text = "$plotName  ·  $crop  ·  $area",
                        style = TextStyle(
                            fontFamily = UrbanistMedium,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = SoftWhite,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(69.dp)
                        .clip(CardShape)
                        .background(MuyuSkip)
                        .border(1.dp, MuyuForest, CardShape)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.merchant_listed_amount),
                        style = TextStyle(
                            fontFamily = UrbanistSemiBold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Muted,
                        ),
                    )
                    Box(
                        modifier = Modifier
                            .height(23.dp)
                            .clip(ChipShape)
                            .background(MuyuGrayChip)
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.merchant_fixed),
                            style = TextStyle(
                                fontFamily = UrbanistBold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MuyuForest,
                            ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                DisbursementCard()
                Spacer(modifier = Modifier.height(12.dp))
                EscrowProtectCard()
                Spacer(modifier = Modifier.height(12.dp))
                PaymentMethodCard(method = method, onMethod = { method = it })
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(MuyuTermsBg)
                        .border(1.dp, MuyuForest, CardShape)
                        .clickable { accepted = !accepted }
                        .padding(horizontal = 18.dp, vertical = 22.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (accepted) MuyuOlive else MuyuCheckbox),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.merchant_terms),
                        style = TextStyle(
                            fontFamily = UrbanistMedium,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(CardShape)
                        .background(MuyuTermsBg)
                        .border(1.dp, Color.Black, CardShape)
                        .clickable {
                            scope.launch {
                                val id = plot?.id
                                if (id != null) {
                                    container.fundPlotUseCase(id).onSuccess {
                                        container.selectedPlot = it
                                    }
                                }
                                onAcceptTerms()
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.merchant_accept_first),
                        style = TextStyle(
                            fontFamily = UrbanistExtraBold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Muted,
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
private fun DisbursementCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = stringResource(R.string.merchant_disbursement),
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = TitleMuted,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        PlanRow(1, R.string.merchant_planting, R.string.merchant_pct_23)
        MuyuAsset("merchant/plan_line.svg", null, 297.dp, 1.dp)
        PlanRow(2, R.string.merchant_irrigation, R.string.merchant_pct_18)
        MuyuAsset("merchant/plan_line.svg", null, 297.dp, 1.dp)
        PlanRow(3, R.string.merchant_fertilization, R.string.merchant_pct_23)
        MuyuAsset("merchant/plan_line.svg", null, 297.dp, 1.dp)
        PlanRow(4, R.string.merchant_harvest, R.string.merchant_pct_36)
    }
}

@Composable
private fun PlanRow(step: Int, title: Int, percent: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(contentAlignment = Alignment.Center) {
            MuyuAsset("merchant/step_dot.svg", null, 40.dp, 40.dp)
            Text(
                text = step.toString(),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MuyuForest,
                ),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(title),
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black,
            ),
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(R.string.merchant_step_amount),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MuyuForest,
                ),
            )
            Text(
                text = stringResource(percent),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Muted,
                ),
            )
        }
    }
}

@Composable
private fun EscrowProtectCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(MuyuEscrowInfo)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.merchant_escrow_how),
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = TitleMuted,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            EscrowStep("1", stringResource(R.string.merchant_escrow_1))
            EscrowStep("2", stringResource(R.string.merchant_escrow_2))
            EscrowStep("3", stringResource(R.string.merchant_escrow_3))
        }
    }
}

@Composable
private fun EscrowStep(number: String, label: String) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(MuyuEscrowStep),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number,
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFFFFFEFE),
                ),
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Muted,
            ),
        )
    }
}

@Composable
private fun PaymentMethodCard(
    method: PayMethod,
    onMethod: (PayMethod) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = stringResource(R.string.merchant_payment_method),
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = TitleMuted,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MethodChip(
                        text = stringResource(R.string.merchant_card),
                        selected = method == PayMethod.Card,
                        onClick = { onMethod(PayMethod.Card) },
                    )
                    MethodChip(
                        text = stringResource(R.string.merchant_wallet),
                        selected = method == PayMethod.Wallet,
                        onClick = { onMethod(PayMethod.Wallet) },
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                MethodChip(
                    text = stringResource(R.string.merchant_agent),
                    selected = method == PayMethod.Agent,
                    onClick = { onMethod(PayMethod.Agent) },
                )
                Spacer(modifier = Modifier.height(8.dp))
                MethodChip(
                    text = stringResource(R.string.merchant_bank),
                    selected = method == PayMethod.Bank,
                    onClick = { onMethod(PayMethod.Bank) },
                )
            }
            Image(
                painter = painterResource(R.drawable.merchant_pago),
                contentDescription = stringResource(R.string.merchant_card),
                modifier = Modifier
                    .width(114.dp)
                    .height(107.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun MethodChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(MethodShape)
            .background(if (selected) MuyuOlive else MuyuGrayChip)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = if (selected) Color.White else Color.Black,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun FundPlotScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            FundPlotScreen(onBack = {}, onAcceptTerms = {}, onTabSelected = {})
        }
    }
}
