package com.example.muyu_app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.muyu_app.ui.theme.FredokaSemiBold
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuForest

@Composable
fun BlankTabScreen(
    tab: MuyuTab,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        Text(
            text = stringResource(tab.label),
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                fontFamily = FredokaSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                color = MuyuForest,
            ),
        )
        MuyuBottomBar(
            selectedTab = tab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun BlankMerchantTabScreen(
    tab: MerchantTab,
    onTabSelected: (MerchantTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        Text(
            text = stringResource(tab.label),
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                fontFamily = FredokaSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                color = MuyuForest,
            ),
        )
        MerchantBottomBar(
            selectedTab = tab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
