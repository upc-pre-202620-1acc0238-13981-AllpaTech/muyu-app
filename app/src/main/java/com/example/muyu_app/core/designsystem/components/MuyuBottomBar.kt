package com.example.muyu_app.core.designsystem.components

import androidx.annotation.DrawableRes
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
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuNavSelected
import com.example.muyu_app.ui.theme.UrbanistSemiBold

val MuyuScrollBottomInset = 140.dp

enum class MuyuTab(
    @param:DrawableRes val icon: Int,
    @param:StringRes val label: Int,
) {
    Home(R.drawable.ic_nav_home, R.string.nav_home),
    Plot(R.drawable.ic_nav_plot, R.string.nav_plot),
    Sync(R.drawable.ic_nav_sync, R.string.nav_sync),
    Payments(R.drawable.ic_nav_payments, R.string.nav_payments),
    Profile(R.drawable.ic_nav_profile, R.string.nav_profile),
}

@Composable
fun MuyuBottomBar(
    selectedTab: MuyuTab,
    onTabSelected: (MuyuTab) -> Unit,
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
            MuyuTab.entries.forEach { tab ->
                NavTab(
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
private fun NavTab(
    tab: MuyuTab,
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
            Icon(
                painter = painterResource(tab.icon),
                contentDescription = stringResource(tab.label),
                modifier = Modifier.size(24.dp),
                tint = Color.White,
            )
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
