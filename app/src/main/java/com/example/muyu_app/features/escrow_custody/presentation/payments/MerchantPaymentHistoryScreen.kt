package com.example.muyu_app.features.escrow_custody.presentation.payments

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
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(12.dp)

private val PurpleCardBg = Color(0xFF454388)
private val PurpleChipBg = Color(0xFFB5C0EC)
private val PurpleChipText = Color(0xFF23255A)
private val MethodChipBg = Color(0xFFE0E0E0)
private val IconBgColor = Color(0xFFBBE395)

private data class MerchantRelease(
    val plot: Plot,
    val milestoneKey: String,
    val amount: Double,
    val dateLabel: String,
)

private fun Plot.toReleases(): List<MerchantRelease> {
    val total = amount.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: return emptyList()
    val steps = PlotFinance.disbursement(total)
    return milestoneStates.filter { it.isApproved }.mapNotNull { milestone ->
        val index = MilestoneCatalog.all.indexOfFirst { it.key == milestone.key }
        val step = steps.getOrNull(index) ?: return@mapNotNull null
        val date = if (milestone.submittedAtMillis > 0L) {
            PlotFinance.shortDateFromMillis(milestone.submittedAtMillis)
        } else {
            milestone.recordedAt
        }
        MerchantRelease(this, milestone.key, step.amount, date)
    }
}

@Composable
fun MerchantPaymentHistoryScreen(
    onBack: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit,
    onPaymentClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    var releases by remember { mutableStateOf<List<MerchantRelease>>(emptyList()) }
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()

    LaunchedEffect(lifecycleState.value) {
        if (!lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) return@LaunchedEffect
        val email = container.fakeStore.currentUser?.email.orEmpty()
        if (email.isBlank()) {
            releases = emptyList()
            return@LaunchedEffect
        }
        container.getPlotsUseCase().onSuccess { all ->
            releases = all
                .filter { it.fundedByEmail.equals(email, ignoreCase = true) && it.status == "funded" }
                .map { plot ->
                    container.fakeStore.ensureMilestones(plot.id)?.let { container.fakeStore.toDomain(it) } ?: plot
                }
                .flatMap { it.toReleases() }
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
                        .background(PurpleCardBg)
                        .padding(20.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.merchant_payments_total_released),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = PlotFinance.formatAmount(releases.sumOf { it.amount }),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.payments_completed_count, releases.size),
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (releases.isEmpty()) {
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
                            text = stringResource(R.string.merchant_payments_empty_body),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                releases.forEach { release ->
                    val definition = MilestoneCatalog.definition(release.milestoneKey)
                    MerchantPaymentCard(
                        title = "${release.plot.name} — ${stringResource(definition.titleRes)}",
                        subtitle = listOf(release.plot.ownerName, release.dateLabel)
                            .filter { it.isNotBlank() }
                            .joinToString(" · "),
                        paymentMethod = "",
                        amount = PlotFinance.formatAmount(release.amount),
                        status = stringResource(R.string.merchant_pay_released),
                        imageRes = R.drawable.moneyimg,
                        onClick = {
                            container.selectedPlot = release.plot
                            container.selectedMilestoneKey = release.milestoneKey
                            onPaymentClick()
                        },
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }

        MerchantBottomBar(
            selectedTab = MerchantTab.Payments,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun MerchantPaymentCard(
    title: String,
    subtitle: String,
    paymentMethod: String,
    amount: String,
    status: String,
    @DrawableRes imageRes: Int? = null,
    iconAsset: String = "merchant/cash.svg",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest.copy(alpha = 0.5f), CardShape)
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(IconBgColor),
                contentAlignment = Alignment.Center,
            ) {
                if (imageRes != null) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    MuyuAsset(
                        fileName = iconAsset,
                        contentDescription = null,
                        width = 24.dp,
                        height = 24.dp,
                    )
                }
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
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(ChipShape)
                        .background(MethodChipBg)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = paymentMethod,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(ChipShape)
                        .background(PurpleChipBg)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleChipText,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun MerchantPaymentHistoryScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            MerchantPaymentHistoryScreen(onBack = {}, onTabSelected = {})
        }
    }
}