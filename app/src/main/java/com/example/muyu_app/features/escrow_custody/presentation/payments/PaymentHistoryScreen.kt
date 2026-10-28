package com.example.muyu_app.features.escrow_custody.presentation.payments

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.TextStyle
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
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuNearText
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.MuyuSkip
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistExtraBold
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private enum class PaymentFilter { All, Completed, Pending }

private data class FarmerPayment(
    val plot: Plot,
    val milestoneKey: String,
    val amount: Double,
    val completed: Boolean,
    val dateLabel: String,
    val expectedLabel: String,
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
        val expected = milestone.extensionDate.ifBlank { milestone.dueLabel }
        FarmerPayment(this, milestone.key, step.amount, completed, date, expected)
    }
}

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(100.dp)
private val TotalCardBg = Color(0xFFC4ECA8)
private val MutedGreen = Color(0xFF4F7A3A)

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
                title = stringResource(R.string.payment_details_title),
                onBack = onBack,
                backgroundColor = MuyuNavBar,
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
                        .background(TotalCardBg)
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.payments_total_received),
                            style = TextStyle(
                                fontFamily = UrbanistSemiBold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MuyuForest,
                            ),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = PlotFinance.formatAmount(totalReceived),
                            style = TextStyle(
                                fontFamily = UrbanistExtraBold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp,
                                color = MuyuForest,
                            ),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.payments_completed_count, completedCount),
                            style = TextStyle(
                                fontFamily = UrbanistLight,
                                fontWeight = FontWeight.Light,
                                fontSize = 14.sp,
                                color = MutedGreen,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        text = stringResource(R.string.payments_filter_all),
                        isSelected = selectedFilter == PaymentFilter.All,
                        onClick = { selectedFilter = PaymentFilter.All },
                    )
                    FilterChip(
                        text = stringResource(R.string.payments_filter_completed),
                        isSelected = selectedFilter == PaymentFilter.Completed,
                        onClick = { selectedFilter = PaymentFilter.Completed },
                    )
                    FilterChip(
                        text = stringResource(R.string.payments_filter_pending),
                        isSelected = selectedFilter == PaymentFilter.Pending,
                        onClick = { selectedFilter = PaymentFilter.Pending },
                    )
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
                    val expectedText = payment.expectedLabel.ifBlank { payment.dateLabel }
                    PaymentItemCard(
                        title = stringResource(definition.titleRes),
                        subtitle = if (payment.completed) {
                            listOf(payment.plot.name, payment.dateLabel)
                                .filter { it.isNotBlank() }
                                .joinToString(" ")
                        } else {
                            payment.plot.name
                        },
                        expected = if (!payment.completed && expectedText.isNotBlank()) {
                            stringResource(R.string.payments_expected, expectedText)
                        } else {
                            null
                        },
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
) {
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(ChipShape)
            .background(if (isSelected) MuyuNavBar else MuyuSkip)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (isSelected) Color.White else MuyuNearText,
            ),
        )
    }
}

@Composable
private fun PaymentItemCard(
    title: String,
    subtitle: String,
    expected: String?,
    amount: String,
    status: String,
    isCompleted: Boolean,
    @DrawableRes imageRes: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Fit,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 13.sp,
                        color = Color.Black.copy(alpha = 0.45f),
                    ),
                )
                if (!expected.isNullOrBlank()) {
                    Text(
                        text = expected,
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 13.sp,
                            color = Color.Black.copy(alpha = 0.45f),
                        ),
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount,
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                    ),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(ChipShape)
                        .background(if (isCompleted) MuyuApprovedBg else MuyuPendingBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = status,
                        style = TextStyle(
                            fontFamily = UrbanistSemiBold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = if (isCompleted) MuyuForest else MuyuPendingText,
                        ),
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