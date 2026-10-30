package com.example.muyu_app.features.plot_financing.presentation.fund

import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
import java.util.Locale
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import androidx.compose.runtime.saveable.rememberSaveable
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
private enum class FundingStage { Summary, Payment, Review, Unconfirmed, Unavailable }

// Coordinates the merchant financing wizard and delays the API mutation until final confirmation.

@Composable
fun FundPlotScreen(
    onBack: () -> Unit,
    onBackToCatalog: () -> Unit,
    onCheckTransactions: () -> Unit,
    onAcceptTerms: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val plot = container.selectedPlot
    val amount = plot?.amount.orEmpty()
    val plotName = plot?.name.orEmpty()
    val crop = plot?.crop.orEmpty()
    val area = plot?.area.orEmpty()
    // Conserva el método elegido al rotar y separa el estado de cada parcela.
    var method by rememberSaveable(plot?.id) { mutableStateOf(PayMethod.Card) }
    var accepted by rememberSaveable(plot?.id) { mutableStateOf(false) }
    var stage by rememberSaveable(plot?.id) { mutableStateOf(FundingStage.Summary) }
    // Bloquea nuevas solicitudes mientras el financiamiento está en curso.
    var funding by remember(plot?.id) { mutableStateOf(false) }

    val stageBack: () -> Unit = {
        when (stage) {
            FundingStage.Summary -> onBack()
            FundingStage.Payment -> stage = FundingStage.Summary
            FundingStage.Review -> stage = FundingStage.Payment
            FundingStage.Unconfirmed -> stage = FundingStage.Payment
            FundingStage.Unavailable -> onBackToCatalog()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        GreenTitleHeader(
            title = stringResource(
                if (stage == FundingStage.Review) R.string.merchant_confirm_financing
                else if (stage == FundingStage.Unconfirmed) R.string.merchant_payment_unconfirmed
                else if (stage == FundingStage.Unavailable) R.string.merchant_financing_title
                else R.string.merchant_fund_title,
            ),
            onBack = stageBack,
        )
        if (stage == FundingStage.Unavailable) {
            PlotUnavailableContent(
                plotName = plotName,
                onViewOtherPlots = onBackToCatalog,
                onBackToCatalog = onBackToCatalog,
            )
        } else if (stage == FundingStage.Unconfirmed) {
            PaymentUnconfirmedContent(
                amount = amount,
                onCheckTransactions = onCheckTransactions,
                onChangeMethod = {
                    accepted = false
                    stage = FundingStage.Payment
                },
                onRetry = { stage = FundingStage.Review },
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                if (stage == FundingStage.Summary) Column(
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
                if (stage == FundingStage.Summary) Spacer(modifier = Modifier.height(12.dp))
                if (stage == FundingStage.Summary) Row(
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
                if (stage == FundingStage.Summary) {
                    Spacer(modifier = Modifier.height(12.dp))
                    DisbursementCard(total = PlotFinance.parseAmount(amount))
                    Spacer(modifier = Modifier.height(12.dp))
                    EscrowProtectCard()
                }
                if (stage == FundingStage.Payment) {
                    PaymentMethodCard(method = method, onMethod = { method = it })
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (stage == FundingStage.Review) {
                    FinalReviewCard(
                        amount = amount,
                        plotName = plotName,
                        crop = crop,
                        area = area,
                        farmerName = plot?.ownerName.orEmpty(),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SelectedPaymentCard(method)
                    Spacer(modifier = Modifier.height(12.dp))
                    DisbursementCard(total = PlotFinance.parseAmount(amount))
                    Spacer(modifier = Modifier.height(12.dp))
                    EscrowRulesCard(amount)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (stage == FundingStage.Payment) Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(MuyuTermsBg)
                        .border(1.dp, MuyuForest, CardShape)
                        // Permite que el lector de pantalla anuncie la aceptación de términos.
                        .toggleable(value = accepted, role = Role.Checkbox, onValueChange = { accepted = it })
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
                        .background(if (stage == FundingStage.Payment && !accepted) MuyuTermsBg else MuyuOlive)
                        .border(1.dp, Color.Black, CardShape)
                        .clickable(enabled = stage != FundingStage.Payment || accepted) {
                            when (stage) {
                                FundingStage.Summary -> stage = FundingStage.Payment
                                FundingStage.Payment -> stage = FundingStage.Review
                                FundingStage.Review -> {
                                    if (funding) return@clickable
                                    funding = true
                                    scope.launch {
                                        try {
                                            val id = plot?.id ?: return@launch
                                            container.fundPlotUseCase(id).onSuccess {
                                                container.selectedPlot = it
                                                onAcceptTerms()
                                            }.onFailure { error ->
                                                val reason = error.message.orEmpty().lowercase()
                                                stage = if (
                                                    "already" in reason || "funded" in reason ||
                                                    "available" in reason || "409" in reason
                                                ) FundingStage.Unavailable else FundingStage.Unconfirmed
                                            }
                                        } finally {
                                            funding = false
                                        }
                                    }
                                }
                                FundingStage.Unconfirmed -> Unit
                                FundingStage.Unavailable -> Unit
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = when {
                            funding -> stringResource(R.string.muyu_funding_processing)
                            stage == FundingStage.Summary -> stringResource(R.string.merchant_continue)
                            stage == FundingStage.Review -> stringResource(R.string.merchant_confirm_and_pay, amount)
                            accepted -> stringResource(R.string.merchant_continue)
                            else -> stringResource(R.string.merchant_accept_first)
                        },
                        style = TextStyle(
                            fontFamily = UrbanistExtraBold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = if (stage == FundingStage.Payment && !accepted) Muted else Color.White,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
// Calcula los montos de los hitos a partir del total de la parcela.
private fun DisbursementCard(total: Double) {
    val steps = PlotFinance.disbursement(total)
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
        PlanRow(1, steps[0].titleRes, "%.2f%%".format(Locale.getDefault(), steps[0].percent), PlotFinance.formatAmount(steps[0].amount))
        MuyuAsset("merchant/plan_line.svg", null, 297.dp, 1.dp)
        PlanRow(2, steps[1].titleRes, "%.2f%%".format(Locale.getDefault(), steps[1].percent), PlotFinance.formatAmount(steps[1].amount))
        MuyuAsset("merchant/plan_line.svg", null, 297.dp, 1.dp)
        PlanRow(3, steps[2].titleRes, "%.2f%%".format(Locale.getDefault(), steps[2].percent), PlotFinance.formatAmount(steps[2].amount))
        MuyuAsset("merchant/plan_line.svg", null, 297.dp, 1.dp)
        PlanRow(4, steps[3].titleRes, "%.2f%%".format(Locale.getDefault(), steps[3].percent), PlotFinance.formatAmount(steps[3].amount))
    }
}

@Composable
// Muestra el porcentaje calculado por la misma regla de desembolso.
private fun PlanRow(step: Int, title: Int, percent: String, amountLabel: String) {
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
                text = amountLabel,
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MuyuForest,
                ),
            )
            Text(
                text = percent,
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

@Composable
private fun FinalReviewCard(
    amount: String,
    plotName: String,
    crop: String,
    area: String,
    farmerName: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.merchant_final_review),
                style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 18.sp),
            )
            Text(
                text = stringResource(R.string.merchant_terms_accepted),
                modifier = Modifier
                    .clip(ChipShape)
                    .background(MuyuGrayChip)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MuyuForest),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(amount, style = TextStyle(fontFamily = FredokaSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 30.sp))
        Spacer(Modifier.height(8.dp))
        ReviewLine(stringResource(R.string.merchant_recipient), farmerName)
        ReviewLine(stringResource(R.string.merchant_plot_label), listOf(plotName, crop, area).filter { it.isNotBlank() }.joinToString(" · "))
        ReviewLine(stringResource(R.string.merchant_funder), stringResource(R.string.merchant_company_name))
    }
}

@Composable
private fun ReviewLine(label: String, value: String) {
    Text(label, style = TextStyle(fontFamily = UrbanistRegular, fontSize = 12.sp, color = Muted))
    Text(value, style = TextStyle(fontFamily = UrbanistSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 14.sp))
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun SelectedPaymentCard(method: PayMethod) {
    val label = when (method) {
        PayMethod.Card -> stringResource(R.string.merchant_card_masked)
        PayMethod.Wallet -> stringResource(R.string.merchant_wallet)
        PayMethod.Agent -> stringResource(R.string.merchant_agent)
        PayMethod.Bank -> stringResource(R.string.merchant_bank)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(16.dp),
    ) {
        Text(stringResource(R.string.merchant_payment_method), style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 16.sp))
        Spacer(Modifier.height(8.dp))
        Text(label, style = TextStyle(fontFamily = UrbanistSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MuyuForest))
    }
}

@Composable
private fun EscrowRulesCard(amount: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(16.dp),
    ) {
        Text(stringResource(R.string.merchant_escrow_how), style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 18.sp))
        Spacer(Modifier.height(10.dp))
        ReviewLine("1", stringResource(R.string.merchant_escrow_holds_amount, amount))
        ReviewLine("2", stringResource(R.string.merchant_escrow_approval_rule))
        ReviewLine("3", stringResource(R.string.merchant_escrow_no_auto_release))
    }
}

@Composable
private fun PaymentUnconfirmedContent(
    amount: String,
    onCheckTransactions: () -> Unit,
    onChangeMethod: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("!", style = TextStyle(fontFamily = UrbanistExtraBold, fontWeight = FontWeight.ExtraBold, fontSize = 58.sp, color = Color(0xFFD83939)))
        Text(stringResource(R.string.merchant_payment_unconfirmed), style = TextStyle(fontFamily = FredokaSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 26.sp))
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.merchant_payment_unconfirmed_body),
            textAlign = TextAlign.Center,
            style = TextStyle(fontFamily = UrbanistRegular, fontSize = 14.sp, color = Muted),
        )
        Spacer(Modifier.height(18.dp))
        Column(
            modifier = Modifier.fillMaxWidth().clip(CardShape).background(Color.White).border(1.dp, Color(0xFFD83939), CardShape).padding(16.dp),
        ) {
            ReviewLine(stringResource(R.string.merchant_status), stringResource(R.string.merchant_unconfirmed))
            ReviewLine(stringResource(R.string.merchant_amount_funded), amount)
            ReviewLine(stringResource(R.string.merchant_payment_method_label), stringResource(R.string.merchant_card_masked))
        }
        Spacer(Modifier.height(14.dp))
        FundingAction(stringResource(R.string.merchant_check_transactions), true, onCheckTransactions)
        Spacer(Modifier.height(10.dp))
        FundingAction(stringResource(R.string.merchant_change_method), false, onChangeMethod)
        Spacer(Modifier.height(10.dp))
        FundingAction(stringResource(R.string.merchant_retry_payment), false, onRetry)
    }
}

@Composable
private fun PlotUnavailableContent(
    plotName: String,
    onViewOtherPlots: () -> Unit,
    onBackToCatalog: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("🔒", fontSize = 54.sp)
        Text(
            stringResource(R.string.merchant_plot_unavailable),
            textAlign = TextAlign.Center,
            style = TextStyle(fontFamily = FredokaSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 27.sp, color = Color(0xFF9A4D20)),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.merchant_plot_unavailable_body),
            textAlign = TextAlign.Center,
            style = TextStyle(fontFamily = UrbanistRegular, fontSize = 14.sp, color = Muted),
        )
        Spacer(Modifier.height(18.dp))
        Column(
            modifier = Modifier.fillMaxWidth().clip(CardShape).background(Color.White).border(1.dp, MuyuForest, CardShape).padding(14.dp),
        ) {
            Text(plotName, style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 18.sp))
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.merchant_plot_funded_by_other), style = TextStyle(fontFamily = UrbanistRegular, fontSize = 13.sp, color = Muted))
        }
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(CardShape).background(MuyuTermsBg).padding(14.dp),
        ) {
            Text(stringResource(R.string.merchant_no_confirmed_payment), style = TextStyle(fontFamily = UrbanistSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF7A4B00)))
        }
        Spacer(Modifier.height(18.dp))
        FundingAction(stringResource(R.string.merchant_view_other_plots), true, onViewOtherPlots)
        Spacer(Modifier.height(10.dp))
        FundingAction(stringResource(R.string.merchant_back_catalog), false, onBackToCatalog)
    }
}

@Composable
private fun FundingAction(label: String, primary: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(CardShape)
            .background(if (primary) MuyuOlive else Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (primary) Color.White else MuyuForest))
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun FundPlotScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            FundPlotScreen(onBack = {}, onBackToCatalog = {}, onCheckTransactions = {}, onAcceptTerms = {})
        }
    }
}
