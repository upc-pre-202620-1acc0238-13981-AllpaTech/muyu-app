package com.example.muyu_app.features.plot_financing.presentation.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.di.MerchantEvidenceOrigin
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.core.network.MerchantNotificationRecord
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_financing.presentation.evidence.formatMerchantRelativeTime
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(12.dp)
private val IconPillShape = RoundedCornerShape(100.dp)
private val IconPillBg = Color(0xFFA0C97F)
private val OnSurface = Color(0xFF1D1B20)

@Composable
fun MerchantAlertsScreen(
    onPlotClick: () -> Unit,
    onEvidenceClick: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    var items by remember { mutableStateOf<List<MerchantNotificationRecord>>(emptyList()) }
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    fun reloadNotifications() {
        val email = container.fakeStore.currentUser?.email.orEmpty()
        items = container.fakeStore.notificationsForMerchant(email)
    }

    LaunchedEffect(lifecycleState.value) {
        if (lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) {
            reloadNotifications()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.merchant_notifications_title),
                onBack = onBack,
                backgroundColor = MuyuNavBar,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = MuyuScrollBottomInset),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                if (items.isEmpty()) {
                    Text(
                        text = stringResource(R.string.merchant_alert_empty),
                        style = TextStyle(
                            fontFamily = UrbanistLight,
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.5f),
                        ),
                    )
                } else {
                    items.forEach { item ->
                        NotificationCard(
                            item = item,
                            onClick = {
                                container.fakeStore.ensureMilestones(item.plotId)?.let { record ->
                                    container.selectedPlot = container.fakeStore.toDomain(record)
                                    if (item.type == MerchantNotificationRecord.TYPE_EVIDENCE &&
                                        item.milestoneKey.isNotBlank()
                                    ) {
                                        container.selectedMilestoneKey = item.milestoneKey
                                        container.merchantEvidenceOrigin = MerchantEvidenceOrigin.Alerts
                                        onEvidenceClick()
                                    } else {
                                        onPlotClick()
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
        MerchantBottomBar(
            selectedTab = MerchantTab.Alerts,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun NotificationCard(
    item: MerchantNotificationRecord,
    onClick: () -> Unit,
) {
    val title = notificationTitle(item)
    val body = notificationBody(item)
    val time = formatMerchantRelativeTime(item.createdAtMillis)
    val icon = notificationIcon(item.type)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 39.dp, height = 37.dp)
                    .clip(IconPillShape)
                    .background(IconPillBg),
                contentAlignment = Alignment.Center,
            ) {
                MuyuAsset(
                    fileName = icon.fileName,
                    contentDescription = null,
                    width = icon.width,
                    height = icon.height,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = OnSurface,
                    ),
                )
                Text(
                    text = time,
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = OnSurface,
                    ),
                )
            }
        }
        Text(
            text = body,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp, end = 8.dp),
            style = TextStyle(
                fontFamily = UrbanistLight,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.5f),
            ),
        )
    }
}

private data class NotificationIconSpec(
    val fileName: String,
    val width: Dp,
    val height: Dp,
)

private fun notificationIcon(type: String): NotificationIconSpec = when (type) {
    MerchantNotificationRecord.TYPE_EXTENSION ->
        NotificationIconSpec("merchant/notif_calendar.png", 18.dp, 17.dp)
    MerchantNotificationRecord.TYPE_HARVEST ->
        NotificationIconSpec("merchant/notif_crop.png", 17.dp, 21.dp)
    MerchantNotificationRecord.TYPE_WEATHER ->
        NotificationIconSpec("merchant/notif_rain.png", 15.dp, 15.dp)
    else ->
        NotificationIconSpec("merchant/notif_payment.png", 21.dp, 12.dp)
}

@Composable
private fun notificationTitle(item: MerchantNotificationRecord): String = when (item.type) {
    MerchantNotificationRecord.TYPE_EXTENSION -> stringResource(R.string.merchant_alert_extension_title)
    MerchantNotificationRecord.TYPE_HARVEST -> stringResource(R.string.merchant_alert_harvest_title)
    MerchantNotificationRecord.TYPE_WEATHER -> stringResource(R.string.weather_alert_title)
    else -> stringResource(R.string.merchant_alert_payment_review)
}

@Composable
private fun notificationBody(item: MerchantNotificationRecord): String {
    val milestone = milestoneLabel(item.milestoneKey)
    return when (item.type) {
        MerchantNotificationRecord.TYPE_EXTENSION -> stringResource(
            R.string.merchant_alert_extension_body,
            item.farmerName,
            milestone,
            item.extensionDate,
            item.body,
        )
        MerchantNotificationRecord.TYPE_HARVEST -> stringResource(
            R.string.merchant_alert_harvest_body,
            item.plotName,
            item.body,
        )
        MerchantNotificationRecord.TYPE_WEATHER -> item.body.ifBlank {
            stringResource(R.string.merchant_alert_weather_body, item.plotName)
        }
        else -> stringResource(
            R.string.merchant_alert_payment_body,
            item.farmerName,
            milestone,
            item.amountLabel.ifBlank { stringResource(R.string.merchant_step_amount) },
        )
    }
}

@Composable
private fun milestoneLabel(key: String): String = when (key) {
    MilestoneState.KEY_PLANTING -> stringResource(R.string.publish_plan_planting)
    MilestoneState.KEY_IRRIGATION -> stringResource(R.string.publish_plan_irrigation)
    MilestoneState.KEY_FERTILIZATION -> stringResource(R.string.publish_plan_fertilization)
    MilestoneState.KEY_HARVEST -> stringResource(R.string.publish_plan_harvest)
    else -> key
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun MerchantAlertsScreenPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme {
        CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
            MerchantAlertsScreen(onPlotClick = {}, onEvidenceClick = {}, onTabSelected = {})
        }
    }
}
