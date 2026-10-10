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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme

private enum class PaymentFilter { All, Completed, Pending }

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
    var selectedFilter by remember { mutableStateOf(PaymentFilter.All) }

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
                // Card de Total Recibido
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
                            text = "S/ 4,000",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.payments_completed_count),
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Filtros
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

                // Item 1: Fertilization
                PaymentItemCard(
                    title = stringResource(R.string.plot_fertilization),
                    subtitle = "El Rosal Plot  30 Sep 2026",
                    amount = "+ S/ 2,000",
                    status = stringResource(R.string.payments_filter_completed),
                    isCompleted = true,
                    imageRes = R.drawable.payment01,
                    iconAsset = "publish/leaf.svg",
                    onClick = onPaymentClick,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Item 2: Irrigation
                PaymentItemCard(
                    title = stringResource(R.string.plot_irrigation),
                    subtitle = "El Rosal Plot  15 Sep 2026",
                    amount = "+ S/ 2,000",
                    status = stringResource(R.string.payments_filter_completed),
                    isCompleted = true,
                    imageRes = R.drawable.payment02,
                    iconAsset = "publish/water.svg",
                    onClick = onPaymentClick,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Item 3: Harvest
                PaymentItemCard(
                    title = stringResource(R.string.plot_harvest),
                    subtitle = "Los Pinos Plot  Expected 20 Oct",
                    amount = "S/ 2,500",
                    status = stringResource(R.string.payments_filter_pending),
                    isCompleted = false,
                    imageRes = R.drawable.payment03,
                    iconAsset = "publish/harvest.svg",
                    onClick = onPaymentClick,
                )
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
    MuyuappTheme {
        PaymentHistoryScreen(onBack = {}, onTabSelected = {})
    }
}