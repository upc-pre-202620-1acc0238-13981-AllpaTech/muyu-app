package com.example.muyu_app.features.user_management.presentation.profile

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
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
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme

private val BankCardShape = RoundedCornerShape(12.dp)
private val BankBorder = Color(0xFF6F806E)
private val SecondaryText = Color(0xFF777777)

@Composable
fun BankAccountScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onAddCard: () -> Unit = {},
    onCardOptions: () -> Unit = {},
    onYapeClick: () -> Unit = {},
    onPlinClick: () -> Unit = {},
    onSave: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
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
            BankSectionTitle(text = stringResource(R.string.bank_account_cards))
            Spacer(modifier = Modifier.height(10.dp))
            SavedCardRow(onOptionsClick = onCardOptions)
            Spacer(modifier = Modifier.height(12.dp))
            AddCardButton(onClick = onAddCard)

            Spacer(modifier = Modifier.height(28.dp))
            BankSectionTitle(text = stringResource(R.string.bank_account_digital_wallets))
            Spacer(modifier = Modifier.height(10.dp))
            DigitalWalletRow(
                logoRes = R.drawable.yape_logo,
                title = stringResource(R.string.bank_account_yape),
                subtitle = stringResource(R.string.bank_account_yape_linked),
                onClick = onYapeClick,
            )
            Spacer(modifier = Modifier.height(10.dp))
            DigitalWalletRow(
                logoRes = R.drawable.plin_logo,
                title = stringResource(R.string.bank_account_plin),
                subtitle = stringResource(R.string.bank_account_default_wallet),
                onClick = onPlinClick,
            )

            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MuyuOlive, BankCardShape)
                    .clickable(onClick = onSave)
                    .padding(vertical = 17.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.bank_account_save_changes),
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
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
private fun SavedCardRow(onOptionsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, BankCardShape)
            .border(1.dp, BankBorder, BankCardShape)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrandBadge(text = "VISA", color = Color(0xFF2148B5))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.bank_account_debit_ending),
                color = Color(0xFF181818),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.bank_account_primary_card),
                color = SecondaryText,
                fontSize = 13.sp,
            )
        }
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.bank_account_card_options),
            tint = Color(0xFF3F3F3F),
            modifier = Modifier
                .size(25.dp)
                .clickable(onClick = onOptionsClick),
        )
    }
}

@Composable
private fun AddCardButton(onClick: () -> Unit) {
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
            text = stringResource(R.string.bank_account_add_card),
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

@Composable
private fun BrandBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 40.dp)
            .background(color, RoundedCornerShape(7.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = if (text == "VISA") 14.sp else 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun BankAccountScreenPreview() {
    MuyuappTheme {
        BankAccountScreen(onBack = {})
    }
}
