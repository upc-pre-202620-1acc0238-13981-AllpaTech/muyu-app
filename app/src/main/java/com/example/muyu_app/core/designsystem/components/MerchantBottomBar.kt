package com.example.muyu_app.core.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuNavSelected
import com.example.muyu_app.ui.theme.UrbanistSemiBold

enum class MerchantTab(
    @param:StringRes val label: Int,
) {
    Catalog(R.string.nav_catalog),
    MyPlots(R.string.nav_my_plots),
    Alerts(R.string.nav_alerts),
    Payments(R.string.nav_payments),
    Profile(R.string.nav_profile),
}

@Composable
fun MerchantBottomBar(
    selectedTab: MerchantTab,
    onTabSelected: (MerchantTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(MuyuNavBar)
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MerchantTab.entries.forEach { tab ->
                MerchantNavTab(
                    tab = tab,
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun MerchantNavTab(
    tab: MerchantTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .width(62.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MuyuNavSelected),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (tab) {
                MerchantTab.Catalog -> MuyuAsset(
                    fileName = "merchant/ic_catalog.svg",
                    contentDescription = stringResource(tab.label),
                    width = 22.dp,
                    height = 20.dp,
                )
                MerchantTab.MyPlots -> Icon(
                    painter = painterResource(R.drawable.ic_nav_plot),
                    contentDescription = stringResource(tab.label),
                    modifier = Modifier.size(24.dp),
                    tint = Color.White,
                )
                MerchantTab.Alerts -> MuyuAsset(
                    fileName = "merchant/ic_alerts.svg",
                    contentDescription = stringResource(tab.label),
                    width = 24.dp,
                    height = 28.dp,
                )
                MerchantTab.Payments -> Icon(
                    painter = painterResource(R.drawable.ic_nav_payments),
                    contentDescription = stringResource(tab.label),
                    modifier = Modifier.size(24.dp),
                    tint = Color.White,
                )
                MerchantTab.Profile -> Icon(
                    painter = painterResource(R.drawable.ic_nav_profile),
                    contentDescription = stringResource(tab.label),
                    modifier = Modifier.size(24.dp),
                    tint = Color.White,
                )
            }
            Text(
                text = stringResource(tab.label),
                maxLines = 1,
                overflow = TextOverflow.Clip,
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = Color.White,
                ),
            )
        }
    }
}
