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
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
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

@Composable
fun MerchantPaymentHistoryScreen(
    onBack: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit,
    onPaymentClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
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
                // Card Morada de Total Liberado
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
                            text = "S/ 11,500",
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

                Spacer(modifier = Modifier.height(20.dp))

                // Item 1: Esperanza - Planting
                MerchantPaymentCard(
                    title = "Esperanza — ${stringResource(R.string.merchant_planting)}",
                    subtitle = "M. Quispe · 15 Sep 2026",
                    paymentMethod = stringResource(R.string.merchant_bank),
                    amount = "S/ 2,000",
                    status = stringResource(R.string.merchant_pay_released),
                    imageRes = R.drawable.moneyimg,
                    onClick = onPaymentClick,
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Item 2: Los Álamos - Planting
                MerchantPaymentCard(
                    title = "Los Álamos — ${stringResource(R.string.merchant_planting)}",
                    subtitle = "C. Mamani · 10 Sep 2026",
                    paymentMethod = stringResource(R.string.merchant_agent),
                    amount = "S/ 2,000",
                    status = stringResource(R.string.merchant_pay_released),
                    imageRes = R.drawable.moneyimg,
                    onClick = onPaymentClick,
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Item 3: Chacra Verde - Fertilization
                MerchantPaymentCard(
                    title = "Chacra Verde\n— ${stringResource(R.string.merchant_fertilization)}",
                    subtitle = "R. Huanca · 1 Sep 2026",
                    paymentMethod = stringResource(R.string.merchant_wallet),
                    amount = "S/ 3,000",
                    status = stringResource(R.string.merchant_pay_released),
                    imageRes = R.drawable.moneyimg,
                    onClick = onPaymentClick,
                )
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
            // Contenedor de la Imagen o Icono de Billete
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
                // Badge del método de pago
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
                // Badge de estado Morado
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
    MuyuappTheme {
        MerchantPaymentHistoryScreen(onBack = {}, onTabSelected = {})
    }
}