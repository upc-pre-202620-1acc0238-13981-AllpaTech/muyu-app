package com.example.muyu_app.features.plot_financing.presentation.fund

import com.example.muyu_app.ui.theme.MuyuBackground
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.muyu_app.features.user_management.presentation.profile.merchantWalletSummary
import com.example.muyu_app.features.user_management.presentation.profile.merchantWallet
import com.example.muyu_app.features.user_management.presentation.profile.DigitalWalletProvider
import com.example.muyu_app.ui.theme.MuyuEscrowText
import com.example.muyu_app.core.designsystem.components.MascotMood
import com.example.muyu_app.core.designsystem.components.MascotMessage
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
import com.example.muyu_app.ui.theme.MuyuSkip
import com.example.muyu_app.ui.theme.MuyuTermsBg
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.features.user_management.presentation.profile.bankCardSummary
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
private enum class FundingStage { Summary, Payment, Review, Verifying, Rejected, Unavailable }

// Coordinates the merchant financing wizard and delays the API mutation until final confirmation.

@Composable
fun FundPlotScreen(
    onBack: () -> Unit,
    onBackToCatalog: () -> Unit,
    onCheckTransactions: () -> Unit,
    onAcceptTerms: () -> Unit,
    onAddCard: () -> Unit = {},
    onAddWallet: (DigitalWalletProvider) -> Unit = {},
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
    var pending by remember(plot?.id) { mutableStateOf(plot?.id?.let { container.pendingFunding(it) }) }
    var stage by rememberSaveable(plot?.id) {
        mutableStateOf(
            when {
                // Una orden en verificación bloquea nuevos intentos sobre la misma parcela.
                pending != null -> FundingStage.Verifying
                container.resumeFundingAtPayment -> FundingStage.Payment
                else -> FundingStage.Summary
            },
        )
    }
    var checking by remember(plot?.id) { mutableStateOf(false) }
    var stillUnknown by remember(plot?.id) { mutableStateOf(false) }
    LaunchedEffect(Unit) { container.resumeFundingAtPayment = false }
    // Bloquea nuevas solicitudes mientras el financiamiento está en curso.
    var funding by remember(plot?.id) { mutableStateOf(false) }
    // Pagar con tarjeta exige una tarjeta registrada por el comerciante.
    val savedCard = container.savedBankCard
    val cardLabel = savedCard?.let(::bankCardSummary).orEmpty()
    val wallet = container.merchantWallet()
    val walletLabel = wallet?.let(::merchantWalletSummary).orEmpty()
    // Depósito en agente y transferencia usan un código de referencia de la orden.
    val referenceCode = paymentReference(plot?.id ?: 0, container.currentUserEmail().orEmpty())
    val methodReady = when (method) {
        PayMethod.Card -> savedCard != null
        PayMethod.Wallet -> wallet != null
        else -> true
    }
    // Con los términos aceptados el botón avanza o, si falta la tarjeta, abre su registro.
    val canContinue = accepted
    val methodLabel = payMethodLabel(method, cardLabel, walletLabel, referenceCode)

    val stageBack: () -> Unit = {
        when (stage) {
            FundingStage.Summary -> onBack()
            FundingStage.Payment -> stage = FundingStage.Summary
            FundingStage.Review -> stage = FundingStage.Payment
            FundingStage.Verifying -> onBack()
            FundingStage.Rejected -> stage = FundingStage.Payment
            FundingStage.Unavailable -> onBackToCatalog()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        GreenTitleHeader(
            title = stringResource(
                if (stage == FundingStage.Review) R.string.merchant_confirm_financing
                else if (stage == FundingStage.Verifying) R.string.funding_verifying_title
                else if (stage == FundingStage.Rejected) R.string.funding_rejected_title
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
        } else if (stage == FundingStage.Verifying) {
            PaymentVerifyingContent(
                amount = amount,
                pending = pending,
                checking = checking,
                stillUnknown = stillUnknown,
                onCheckStatus = {
                    val id = plot?.id
                    if (id != null && !checking) {
                        checking = true
                        scope.launch {
                            when (container.checkPendingFunding(id)) {
                                FundingCheck.Funded -> onAcceptTerms()
                                FundingCheck.TakenByOther -> stage = FundingStage.Unavailable
                                FundingCheck.NotPaid -> stage = FundingStage.Rejected
                                FundingCheck.StillUnknown -> stillUnknown = true
                            }
                            pending = container.pendingFunding(id)
                            checking = false
                        }
                    }
                },
                onCheckTransactions = onCheckTransactions,
            )
        } else if (stage == FundingStage.Rejected) {
            PaymentUnconfirmedContent(
                amount = amount,
                methodLabel = methodLabel,
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
                    Spacer(modifier = Modifier.height(12.dp))
                    when (method) {
                        PayMethod.Card -> SavedCardRow(cardLabel = cardLabel, onAddCard = onAddCard)
                        PayMethod.Wallet -> WalletRow(
                            walletLabel = walletLabel,
                            onLink = onAddWallet,
                            onChange = { wallet?.let { onAddWallet(it.provider) } },
                        )
                        PayMethod.Agent -> ReferenceRow(
                            title = stringResource(R.string.merchant_agent_info),
                            code = referenceCode,
                        )
                        PayMethod.Bank -> ReferenceRow(
                            title = stringResource(R.string.merchant_bank_info),
                            code = referenceCode,
                        )
                    }
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
                    SelectedPaymentCard(methodLabel)
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
                        .background(if (stage == FundingStage.Payment && !canContinue) MuyuTermsBg else MuyuOlive)
                        .border(1.dp, Color.Black, CardShape)
                        .clickable(enabled = stage != FundingStage.Payment || canContinue) {
                            when (stage) {
                                FundingStage.Summary -> stage = FundingStage.Payment
                                FundingStage.Payment -> when {
                                    methodReady -> stage = FundingStage.Review
                                    method == PayMethod.Wallet -> onAddWallet(DigitalWalletProvider.Yape)
                                    else -> onAddCard()
                                }
                                FundingStage.Review -> {
                                    if (funding) return@clickable
                                    funding = true
                                    container.fundingMethodLabel = methodLabel
                                    scope.launch {
                                        try {
                                            val id = plot?.id ?: return@launch
                                            container.fundPlotUseCase(id).onSuccess {
                                                container.selectedPlot = it
                                                onAcceptTerms()
                                            }.onFailure { error ->
                                                val reason = error.message.orEmpty().lowercase()
                                                stage = when {
                                                    // Sin respuesta del procesador: no se sabe si se cobró.
                                                    error is java.io.IOException -> {
                                                        val started = PendingFunding(methodLabel, PlotFinance.timestampNow())
                                                        container.savePendingFunding(id, started)
                                                        pending = started
                                                        stillUnknown = false
                                                        FundingStage.Verifying
                                                    }
                                                    "already" in reason || "funded" in reason ||
                                                        "available" in reason || "409" in reason -> FundingStage.Unavailable
                                                    else -> FundingStage.Rejected
                                                }
                                            }
                                        } finally {
                                            funding = false
                                        }
                                    }
                                }
                                FundingStage.Verifying -> Unit
                                FundingStage.Rejected -> Unit
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
                            accepted && !methodReady && method == PayMethod.Wallet -> stringResource(R.string.merchant_link_wallet)
                            accepted && !methodReady -> stringResource(R.string.merchant_register_card)
                            accepted -> stringResource(R.string.merchant_continue)
                            else -> stringResource(R.string.merchant_accept_first)
                        },
                        style = TextStyle(
                            fontFamily = UrbanistExtraBold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = if (stage == FundingStage.Payment && !canContinue) Muted else Color.White,
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
private fun SavedCardRow(cardLabel: String, onAddCard: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .clickable(onClick = onAddCard)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (cardLabel.isBlank()) {
                stringResource(R.string.merchant_card_required)
            } else {
                stringResource(R.string.merchant_card_in_use, cardLabel)
            },
            modifier = Modifier.weight(1f),
            style = TextStyle(fontFamily = UrbanistMedium, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Black),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(
                if (cardLabel.isBlank()) R.string.merchant_register_card else R.string.merchant_change_card,
            ),
            style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MuyuOlive),
        )
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
private fun payMethodLabel(
    method: PayMethod,
    cardLabel: String,
    walletLabel: String,
    referenceCode: String,
): String = when (method) {
    PayMethod.Card -> cardLabel.ifBlank { stringResource(R.string.merchant_card) }
    PayMethod.Wallet -> walletLabel.ifBlank { stringResource(R.string.merchant_wallet) }
    PayMethod.Agent -> stringResource(R.string.merchant_method_with_code, stringResource(R.string.merchant_agent), referenceCode)
    PayMethod.Bank -> stringResource(R.string.merchant_method_with_code, stringResource(R.string.merchant_bank), referenceCode)
}

/** Código fijo por parcela y comerciante para identificar el depósito o la transferencia. */
private fun paymentReference(plotId: Int, email: String): String =
    "MUYU-%04d-%04d".format(plotId, kotlin.math.abs(email.hashCode()) % 10000)

@Composable
private fun WalletRow(
    walletLabel: String,
    onLink: (DigitalWalletProvider) -> Unit,
    onChange: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (walletLabel.isBlank()) {
                stringResource(R.string.merchant_wallet_required)
            } else {
                stringResource(R.string.merchant_card_in_use, walletLabel)
            },
            modifier = Modifier.weight(1f),
            style = TextStyle(fontFamily = UrbanistMedium, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Black),
        )
        val actions = if (walletLabel.isBlank()) {
            listOf(
                stringResource(R.string.bank_account_yape) to { onLink(DigitalWalletProvider.Yape) },
                stringResource(R.string.bank_account_plin) to { onLink(DigitalWalletProvider.Plin) },
            )
        } else {
            listOf(stringResource(R.string.merchant_change_card) to onChange)
        }
        actions.forEach { (label, onClick) ->
            Text(
                text = label,
                modifier = Modifier
                    .clip(MethodShape)
                    .clickable(onClick = onClick)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MuyuOlive),
            )
        }
    }
}

@Composable
private fun ReferenceRow(title: String, code: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = title,
            style = TextStyle(fontFamily = UrbanistMedium, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Black),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.merchant_reference_code, code),
            style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MuyuOlive),
        )
    }
}

@Composable
private fun SelectedPaymentCard(label: String) {
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
    methodLabel: String,
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
        MascotMessage(
            mood = MascotMood.Sad,
            title = stringResource(R.string.funding_rejected_title),
            body = stringResource(R.string.funding_rejected_body),
            titleColor = Color(0xFFD83939),
        )
        Spacer(Modifier.height(18.dp))
        Column(
            modifier = Modifier.fillMaxWidth().clip(CardShape).background(Color.White).border(1.dp, Color(0xFFD83939), CardShape).padding(16.dp),
        ) {
            ReviewLine(stringResource(R.string.merchant_status), stringResource(R.string.funding_rejected_status))
            ReviewLine(stringResource(R.string.merchant_amount_funded), amount)
            ReviewLine(stringResource(R.string.merchant_payment_method_label), methodLabel)
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
private fun PaymentVerifyingContent(
    amount: String,
    pending: PendingFunding?,
    checking: Boolean,
    stillUnknown: Boolean,
    onCheckStatus: () -> Unit,
    onCheckTransactions: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MascotMessage(
            mood = MascotMood.Reviewing,
            title = stringResource(R.string.funding_verifying_title),
            body = stringResource(R.string.funding_verifying_body),
        )
        Spacer(Modifier.height(18.dp))
        Column(
            modifier = Modifier.fillMaxWidth().clip(CardShape).background(Color.White).border(1.dp, MuyuForest, CardShape).padding(16.dp),
        ) {
            ReviewLine(stringResource(R.string.merchant_status), stringResource(R.string.funding_verifying_status))
            ReviewLine(stringResource(R.string.merchant_amount_funded), amount)
            ReviewLine(stringResource(R.string.merchant_payment_method_label), pending?.methodLabel.orEmpty())
            ReviewLine(stringResource(R.string.funding_verifying_started), pending?.startedAt.orEmpty())
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(CardShape).background(MuyuEscrowInfo).padding(14.dp),
        ) {
            Text(
                stringResource(if (stillUnknown) R.string.funding_verifying_still else R.string.funding_verifying_locked),
                style = TextStyle(fontFamily = UrbanistSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MuyuEscrowText),
            )
        }
        Spacer(Modifier.height(18.dp))
        FundingAction(
            stringResource(if (checking) R.string.funding_verifying_checking else R.string.funding_verifying_check),
            true,
            onCheckStatus,
        )
        Spacer(Modifier.height(10.dp))
        FundingAction(stringResource(R.string.merchant_check_transactions), false, onCheckTransactions)
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
