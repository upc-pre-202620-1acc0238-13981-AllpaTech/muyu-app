package com.example.muyu_app.features.escrow_custody.presentation.payments

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.features.plot_financing.presentation.evidence.mockTransactionId
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme

private val CardShape = RoundedCornerShape(20.dp)
private val LightGreenBg = Color(0xFFDCF0DD)
private val DarkGreenText = Color(0xFF2E6930)
private val LightBlueBg = Color(0xFFE1F0FA)
private val DarkBlueText = Color(0xFF1E5B82)

@Composable
fun PaymentDetailsScreen(
    onBack: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    onDownloadPdf: () -> Unit = {},
    modifier: Modifier = Modifier,
    bottomBar: (@Composable BoxScope.() -> Unit)? = null,
) {
    val container = LocalAppContainer.current
    val plot = container.selectedPlot
    val milestone = plot?.milestoneStates?.firstOrNull { it.key == container.selectedMilestoneKey }
    val stepIndex = MilestoneCatalog.all.indexOfFirst { it.key == container.selectedMilestoneKey }
    val amount = plot?.let { current ->
        PlotFinance.disbursement(PlotFinance.parseAmount(current.amount)).getOrNull(stepIndex)?.amount
    }
    val amountLabel = amount?.let { PlotFinance.formatAmount(it) }.orEmpty()
    val milestoneTitle = stringResource(MilestoneCatalog.definition(container.selectedMilestoneKey).titleRes)
    val plotName = plot?.name.orEmpty()
    val releaseDate = when {
        (milestone?.submittedAtMillis ?: 0L) > 0L -> PlotFinance.shortDateFromMillis(milestone?.submittedAtMillis ?: 0L)
        !milestone?.recordedAt.isNullOrBlank() -> milestone?.recordedAt.orEmpty()
        else -> ""
    }
    val completed = milestone?.isApproved == true
    val transactionId = mockTransactionId(container.selectedMilestoneKey, milestone?.submittedAtMillis ?: 0L)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.payment_details_title),
                onBack = onBack,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Icono grande de check verde
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC8E6C9)),
                    contentAlignment = Alignment.Center,
                ) {
                    CheckMarkIcon(
                        color = DarkGreenText,
                        modifier = Modifier.size(42.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Monto principal
                Text(
                    text = amountLabel,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreenText,
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Badge de estado
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(LightGreenBg)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = stringResource(
                            if (completed) R.string.payments_filter_completed else R.string.payments_filter_pending,
                        ),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreenText,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Card con detalles de la transacción
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, DarkGreenText.copy(alpha = 0.6f), CardShape)
                        .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.payment_transaction_details),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DetailRow(label = stringResource(R.string.payment_label_milestone), value = milestoneTitle)
                        Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                        DetailRow(label = stringResource(R.string.payment_label_plot), value = plotName)
                        Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                        DetailRow(label = stringResource(R.string.payment_label_release_date), value = releaseDate)
                        Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                        DetailRow(
                            label = stringResource(R.string.payment_label_account),
                            value = container.fakeStore.currentUser?.phone.orEmpty(),
                        )
                        Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                        DetailRow(label = stringResource(R.string.payment_label_txid), value = transactionId)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card explicativa de Escrow
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(LightBlueBg)
                        .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.payment_escrow_how),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBlueText,
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            EscrowStepItem(
                                title = stringResource(R.string.payment_evidence_approved),
                                isChecked = milestone?.isApproved == true || milestone?.isSubmitted == true,
                                modifier = Modifier.weight(1f),
                            )

                            Box(
                                modifier = Modifier
                                    .weight(0.6f)
                                    .height(3.dp)
                                    .background(DarkGreenText),
                            )

                            EscrowStepItem(
                                title = stringResource(R.string.payment_funds_released),
                                isChecked = completed,
                                modifier = Modifier.weight(1f),
                            )

                            Box(
                                modifier = Modifier
                                    .weight(0.6f)
                                    .height(3.dp)
                                    .background(DarkGreenText),
                            )

                            EscrowStepItem(
                                title = stringResource(R.string.payment_received),
                                isChecked = completed,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón descargar recibo PDF
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, DarkGreenText, CardShape)
                        .clickable { onDownloadPdf() },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MuyuAsset(
                            fileName = "publish/pdf.svg",
                            contentDescription = null,
                            width = 24.dp,
                            height = 24.dp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.payment_download_pdf),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreenText,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón volver al historial
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(CardShape)
                        .background(MuyuForest)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.payment_back_history),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
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
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreenText,
        )
    }
}

@Composable
private fun EscrowStepItem(
    title: String,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isChecked) DarkGreenText else Color.Gray),
            contentAlignment = Alignment.Center,
        ) {
            CheckMarkIcon(
                color = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = DarkBlueText,
            lineHeight = 13.sp,
        )
    }
}

@Composable
private fun CheckMarkIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.5f)
            lineTo(size.width * 0.42f, size.height * 0.72f)
            lineTo(size.width * 0.8f, size.height * 0.28f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = size.width * 0.15f),
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun PaymentDetailsScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            PaymentDetailsScreen(onBack = {}, onTabSelected = {})
        }
    }
}