package com.example.muyu_app.features.user_management.presentation.profile

import com.example.muyu_app.ui.theme.MuyuBackground
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.example.muyu_app.R
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuappTheme

private val BankCardShape = RoundedCornerShape(12.dp)
private val BankBorder = Color(0xFF6F806E)
private val SecondaryText = Color(0xFF777777)

@Composable
fun BankAccountScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onAddCard: () -> Unit = {},
    onYapeClick: () -> Unit = {},
    onPlinClick: () -> Unit = {},
    onSave: () -> Unit = {},
) {
    val container = LocalAppContainer.current
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    var balance by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(lifecycleState.value) {
        if (!lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) return@LaunchedEffect
        val farmerEmail = container.fakeStore.currentUser?.email.orEmpty()
        balance = if (farmerEmail.isBlank()) {
            0.0
        } else {
            container.getPlotsUseCase().getOrNull()
                .orEmpty()
                .filter { plot ->
                    plot.ownerEmail.equals(farmerEmail, ignoreCase = true) && plot.status == "funded"
                }
                .map { plot ->
                    container.fakeStore.ensureMilestones(plot.id)
                        ?.let(container.fakeStore::toDomain)
                        ?: plot
                }
                .sumOf(Plot::releasedBalance)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MuyuNavBar)
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.bank_account_back),
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .clickable(onClick = onBack),
            )
            Spacer(modifier = Modifier.width(18.dp))
            Text(
                text = stringResource(R.string.bank_account_title),
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp),
        ) {
            val card = container.savedBankCard
            if (card != null) {
                DebitCard(balance = balance, card = card)
            } else {
                EmptyCard(balance = balance)
            }
            Spacer(modifier = Modifier.height(12.dp))
            AddCardButton(
                label = stringResource(
                    if (card == null) R.string.bank_account_add_card else R.string.bank_account_replace_card,
                ),
                onClick = onAddCard,
            )

            Spacer(modifier = Modifier.height(24.dp))
            BankSectionTitle(text = stringResource(R.string.bank_account_details))
            Spacer(modifier = Modifier.height(10.dp))
            AccountDetailsCard(card = card)

            Spacer(modifier = Modifier.height(24.dp))
            BankSectionTitle(text = stringResource(R.string.bank_account_digital_wallets))
            Spacer(modifier = Modifier.height(10.dp))
            DigitalWalletRow(
                logoRes = R.drawable.yape_logo,
                title = stringResource(R.string.bank_account_yape),
                subtitle = container.yapeAccount?.let { account ->
                    if (account.isPrimary) {
                        stringResource(
                            R.string.bank_account_yape_linked_default_value,
                            maskWalletPhone(account.phone),
                        )
                    } else {
                        stringResource(
                            R.string.bank_account_yape_linked_value,
                            maskWalletPhone(account.phone),
                        )
                    }
                } ?: stringResource(R.string.yape_not_linked),
                onClick = onYapeClick,
            )
            Spacer(modifier = Modifier.height(10.dp))
            DigitalWalletRow(
                logoRes = R.drawable.plin_logo,
                title = stringResource(R.string.bank_account_plin),
                subtitle = container.plinAccount?.let { account ->
                    if (account.isPrimary) {
                        stringResource(
                            R.string.bank_account_plin_linked_default_value,
                            maskWalletPhone(account.phone),
                        )
                    } else {
                        stringResource(
                            R.string.bank_account_plin_linked_value,
                            maskWalletPhone(account.phone),
                        )
                    }
                } ?: stringResource(R.string.plin_not_linked),
                onClick = onPlinClick,
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Las billeteras tienen tope por transferencia: los desembolsos grandes van a la cuenta.
            Text(
                text = stringResource(R.string.bank_account_wallet_limit_note),
                color = SecondaryText,
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BankSectionTitle(text: String) {
    Text(
        text = text,
        color = Color(0xFF171717),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun DebitCard(
    balance: Double,
    card: SavedBankCard,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .shadow(4.dp, BankCardShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF263B6D), Color(0xFF5756AE)),
                ),
                shape = BankCardShape,
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(
                    if (card.type == BankCardType.Debit) {
                        R.string.bank_account_debit_label
                    } else {
                        R.string.bank_account_credit_label
                    },
                ),
                color = Color.White,
                fontSize = 18.sp,
            )
            CardBrandMark(
                brand = card.brand,
                unknownTint = Color.White,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = maskedCardNumber(card.lastFour),
            color = Color.White,
            fontSize = 18.sp,
            letterSpacing = 1.sp,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.bank_account_expiration_value, card.expiration),
                color = Color.White,
                fontSize = 16.sp,
            )
            Text(
                text = stringResource(R.string.bank_account_cvv_masked),
                color = Color.White,
                fontSize = 16.sp,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(
                R.string.bank_account_balance,
                PlotFinance.formatAmount(balance),
            ),
            color = Color.White,
            fontSize = 17.sp,
        )
    }
}

private fun Plot.releasedBalance(): Double {
    val disbursements = PlotFinance.disbursement(PlotFinance.parseAmount(amount))
    return milestoneStates.sumOf { milestone ->
        if (!milestone.isApproved) return@sumOf 0.0
        val index = MilestoneCatalog.all.indexOfFirst { definition ->
            definition.key == milestone.key
        }
        disbursements.getOrNull(index)?.amount ?: 0.0
    }
}

@Composable
private fun EmptyCard(balance: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 172.dp)
            .background(Color.White, BankCardShape)
            .border(1.5.dp, BankBorder, BankCardShape)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.CreditCard,
            contentDescription = null,
            tint = MuyuOlive,
            modifier = Modifier.size(32.dp),
        )
        Text(
            text = stringResource(R.string.bank_account_no_card_title),
            color = Color(0xFF171717),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.bank_account_no_card_body),
            color = SecondaryText,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.bank_account_balance, PlotFinance.formatAmount(balance)),
            color = MuyuOlive,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AccountDetailsCard(card: SavedBankCard?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, BankCardShape)
            .border(1.dp, BankBorder, BankCardShape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        if (card == null) {
            Text(
                text = stringResource(R.string.bank_account_no_details),
                color = Color(0xFF557052),
                fontSize = 13.sp,
            )
        } else {
            AccountDetailRow(
                label = stringResource(R.string.bank_account_holder_label),
                value = card.holderName.ifBlank { "—" },
            )
            AccountDetailRow(
                label = stringResource(R.string.bank_account_card_label),
                value = bankCardSummary(card),
            )
        }
    }
}

@Composable
private fun AccountDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.width(96.dp),
            color = Color(0xFF557052),
            fontSize = 13.sp,
        )
        Text(
            text = value,
            color = Color(0xFF557052),
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun AddCardButton(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, BankCardShape)
            .border(1.5.dp, MuyuOlive, BankCardShape)
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MuyuOlive,
            modifier = Modifier.size(25.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            color = MuyuOlive,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun DigitalWalletRow(
    @DrawableRes logoRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, BankCardShape)
            .border(1.dp, BankBorder, BankCardShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(logoRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(7.dp)),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color(0xFF181818),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = SecondaryText, fontSize = 13.sp)
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF333333),
            modifier = Modifier.size(25.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun BankAccountScreenPreview() {
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            BankAccountScreen(onBack = {})
        }
    }
}
