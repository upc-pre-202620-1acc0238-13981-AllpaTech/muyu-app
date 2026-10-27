package com.example.muyu_app.features.escrow_custody.presentation.payments

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.example.muyu_app.R
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme

private enum class PaymentFilter { All, Completed, Pending }

private data class FarmerPayment(
    val plot: Plot,
    val milestoneKey: String,
    val amount: Double,
    val completed: Boolean,
    val dateLabel: String,
)

private fun Plot.toPayments(): List<FarmerPayment> {
    val total = amount.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: return emptyList()
    val steps = PlotFinance.disbursement(total)
    return milestoneStates.mapNotNull { milestone ->
        val index = MilestoneCatalog.all.indexOfFirst { it.key == milestone.key }
        val step = steps.getOrNull(index) ?: return@mapNotNull null
        val completed = milestone.isApproved
        if (!completed && !milestone.isSubmitted && !milestone.isQueued) return@mapNotNull null
        val date = when {
            milestone.submittedAtMillis > 0L -> PlotFinance.shortDateFromMillis(milestone.submittedAtMillis)
            else -> milestone.recordedAt
        }
        FarmerPayment(this, milestone.key, step.amount, completed, date)
    }
}

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(12.dp)

private val LightGreenBg = Color(0xFFDCF0DD)
private val DarkGreenText = Color(0xFF2E6930)
private val OrangeChipBg = Color(0xFFFFE0B2)
private val DarkOrangeText = Color(0xFFE65100)

@Composable
fun PaymentHistoryScreen(
    onBack: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    onPaymentClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    bottomBar: (@Composable BoxScope.() -> Unit)? = null,
) {
    val container = LocalAppContainer.current
    var selectedFilter by remember { mutableStateOf(PaymentFilter.All) }
    var payments by remember { mutableStateOf<List<FarmerPayment>>(emptyList()) }
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()

    LaunchedEffect(lifecycleState.value) {
        if (!lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) return@LaunchedEffect
        val email = container.fakeStore.currentUser?.email.orEmpty()
        if (email.isBlank()) {
            payments = emptyList()
            return@LaunchedEffect
        }
        container.getPlotsUseCase().onSuccess { all ->
            payments = all
                .filter { it.ownerEmail.equals(email, ignoreCase = true) && it.status == "funded" }
                .map { plot ->
                    container.fakeStore.ensureMilestones(plot.id)?.let { container.fakeStore.toDomain(it) } ?: plot
                }
                .flatMap { it.toPayments() }
        }
    }

    val totalReceived = payments.filter { it.completed }.sumOf { it.amount }
    val completedCount = payments.count { it.completed }
    val visiblePayments = remember(payments, selectedFilter) {
        payments.filter { payment ->
            when (selectedFilter) {
                PaymentFilter.All -> true
                PaymentFilter.Completed -> payment.completed
                PaymentFilter.Pending -> !payment.completed
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.payments_title),
                onBack = onBack,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(MuyuOlive)
                        .padding(20.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.payments_total_received),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = PlotFinance.formatAmount(totalReceived),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.payments_completed_count, completedCount),
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, Color.LightGray.copy(alpha = 0.6f), CardShape)
                        .padding(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        FilterChip(
                            text = stringResource(R.string.payments_filter_all),
                            isSelected = selectedFilter == PaymentFilter.All,
                            onClick = { selectedFilter = PaymentFilter.All },
                            modifier = Modifier.weight(1f),
                        )
                        FilterChip(
                            text = stringResource(R.string.payments_filter_completed),
                            isSelected = selectedFilter == PaymentFilter.Completed,
                            onClick = { selectedFilter = PaymentFilter.Completed },
                            modifier = Modifier.weight(1f),
                        )
                        FilterChip(
                            text = stringResource(R.string.payments_filter_pending),
                            isSelected = selectedFilter == PaymentFilter.Pending,
                            onClick = { selectedFilter = PaymentFilter.Pending },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (visiblePayments.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.payments_empty_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.payments_empty_body),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                visiblePayments.forEach { payment ->
                    val definition = MilestoneCatalog.definition(payment.milestoneKey)
                    PaymentItemCard(
                        title = stringResource(definition.titleRes),
                        subtitle = listOf(payment.plot.name, payment.dateLabel)
                            .filter { it.isNotBlank() }
                            .joinToString("  "),
                        amount = if (payment.completed) {
                            "+ ${PlotFinance.formatAmount(payment.amount)}"
                        } else {
                            PlotFinance.formatAmount(payment.amount)
                        },
                        status = stringResource(
                            if (payment.completed) R.string.payments_filter_completed else R.string.payments_filter_pending,
                        ),
                        isCompleted = payment.completed,
                        imageRes = definition.iconRes,
                        onClick = {
                            container.selectedPlot = payment.plot
                            container.selectedMilestoneKey = payment.milestoneKey
                            onPaymentClick()
                        },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (bottomBar != null) {
            bottomBar()
        } else {
            MuyuBottomBar(
                selectedTab = MuyuTab.Payments,
                onTabSelected = onTabSelected,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) MuyuOlive else Color(0xFFEEEEEE))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black,
        )
    }
}

@Composable
private fun PaymentItemCard(
    title: String,
    subtitle: String,
    amount: String,
    status: String,
    isCompleted: Boolean,
    @DrawableRes imageRes: Int? = null,
    iconAsset: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuOlive.copy(alpha = 0.5f), CardShape)
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            } else if (iconAsset != null) {
                MuyuAsset(
                    fileName = iconAsset,
                    contentDescription = null,
                    width = 36.dp,
                    height = 36.dp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = amount,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    MuyuAsset(
                        fileName = "publish/chevron.svg",
                        contentDescription = null,
                        width = 10.dp,
                        height = 10.dp,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(ChipShape)
                        .background(if (isCompleted) LightGreenBg else OrangeChipBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) DarkGreenText else DarkOrangeText,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun PaymentHistoryScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            PaymentHistoryScreen(onBack = {}, onTabSelected = {})
        }
    }
}