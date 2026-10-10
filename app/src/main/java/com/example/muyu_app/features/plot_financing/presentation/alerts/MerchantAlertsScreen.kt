package com.example.muyu_app.features.plot_financing.presentation.alerts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.di.MerchantEvidenceOrigin
import com.example.muyu_app.core.environment.Environment
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.core.network.MerchantNotificationRecord
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuHeader
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import java.util.concurrent.TimeUnit

private val CardShape = RoundedCornerShape(20.dp)
private val IconShape = RoundedCornerShape(20.dp)

@Composable
fun MerchantAlertsScreen(
    onPlotClick: () -> Unit,
    onEvidenceClick: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    var items by remember { mutableStateOf<List<MerchantNotificationRecord>>(emptyList()) }
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    fun reloadNotifications() {
        val email = container.fakeStore.currentUser?.email
            ?: Environment.DemoCredentials.MERCHANT_EMAIL
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
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MuyuHeader)
                    .statusBarsPadding()
                    .padding(horizontal = 32.dp)
                    .padding(top = 16.dp, bottom = 20.dp),
            ) {
                Text(
                    text = stringResource(R.string.merchant_notifications_title),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                    ),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
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
                        Spacer(modifier = Modifier.height(12.dp))
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
    val time = formatRelativeTime(item.createdAtMillis)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(IconShape)
                .background(MuyuApprovedBg),
            contentAlignment = Alignment.Center,
        ) {
            NotificationIcon(type = item.type)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                    ),
                )
                Text(
                    text = time,
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (time == stringResource(R.string.merchant_time_yesterday)) 12.sp else 16.sp,
                        color = Color.Black.copy(alpha = 0.5f),
                    ),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                style = TextStyle(
                    fontFamily = UrbanistLight,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.5f),
                    lineHeight = 22.sp,
                ),
            )
        }
    }
}

@Composable
private fun NotificationIcon(type: String) {
    when (type) {
        MerchantNotificationRecord.TYPE_EXTENSION -> {
            Image(
                painter = painterResource(R.drawable.plot_calendar),
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                contentScale = ContentScale.Fit,
            )
        }
        MerchantNotificationRecord.TYPE_HARVEST -> {
            MuyuAsset(
                fileName = "evidence/harvest.svg",
                contentDescription = null,
                width = 30.dp,
                height = 30.dp,
            )
        }
        else -> {
            MuyuAsset(
                fileName = "merchant/alert_payment.svg",
                contentDescription = null,
                width = 36.dp,
                height = 22.dp,
            )
        }
    }
}

@Composable
private fun notificationTitle(item: MerchantNotificationRecord): String = when (item.type) {
    MerchantNotificationRecord.TYPE_EXTENSION -> stringResource(R.string.merchant_alert_extension_title)
    MerchantNotificationRecord.TYPE_HARVEST -> stringResource(R.string.merchant_alert_harvest_title)
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

@Composable
private fun formatRelativeTime(createdAtMillis: Long): String {
    if (createdAtMillis <= 0L) return stringResource(R.string.merchant_time_now)
    val now = System.currentTimeMillis()
    val diff = (now - createdAtMillis).coerceAtLeast(0L)
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> stringResource(R.string.merchant_time_now)
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff).coerceAtLeast(1)
            stringResource(R.string.merchant_time_min, minutes)
        }
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff).coerceAtLeast(1)
            stringResource(R.string.merchant_time_hours, hours)
        }
        diff < TimeUnit.DAYS.toMillis(2) -> stringResource(R.string.merchant_time_yesterday)
        else -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(2)
            stringResource(R.string.merchant_time_days, days)
        }
    }
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
