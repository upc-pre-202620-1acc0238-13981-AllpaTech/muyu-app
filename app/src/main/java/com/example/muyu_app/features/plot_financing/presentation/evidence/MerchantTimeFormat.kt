package com.example.muyu_app.features.plot_financing.presentation.evidence

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.muyu_app.R
import com.example.muyu_app.core.network.MerchantNotificationRecord
import com.example.muyu_app.core.network.FakeApiStore
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun formatMerchantRelativeTime(createdAtMillis: Long): String {
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

fun formatEvidenceTime(millis: Long): String {
    if (millis <= 0L) return "—"
    return DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(millis))
}

fun formatEvidenceShortDate(millis: Long, locale: Locale = Locale.getDefault()): String {
    if (millis <= 0L) return "—"
    return SimpleDateFormat("d MMM", locale).format(Date(millis))
}

fun formatEvidenceDateTimeLine(millis: Long, locale: Locale = Locale.getDefault()): String {
    if (millis <= 0L) return "—"
    val date = SimpleDateFormat("d MMM", locale).format(Date(millis))
    val time = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(millis))
    return "$date · $time"
}

fun isSameCalendarDay(millis: Long, nowMillis: Long = System.currentTimeMillis()): Boolean {
    if (millis <= 0L) return false
    val a = Calendar.getInstance().apply { timeInMillis = millis }
    val b = Calendar.getInstance().apply { timeInMillis = nowMillis }
    return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
        a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}

fun resolveMilestoneSubmittedAt(
    milestone: MilestoneState?,
    plotId: Int?,
    store: FakeApiStore,
    merchantEmail: String,
): Long {
    milestone?.submittedAtMillis?.takeIf { it > 0L }?.let { return it }
    if (plotId != null && milestone != null) {
        store.notificationsForMerchant(merchantEmail)
            .firstOrNull {
                it.type == MerchantNotificationRecord.TYPE_EVIDENCE &&
                    it.plotId == plotId &&
                    it.milestoneKey == milestone.key &&
                    it.createdAtMillis > 0L
            }
            ?.createdAtMillis
            ?.let { return it }
    }
    milestone?.recordedAt?.takeIf { it.isNotBlank() }?.let { PlotFinance.parseDate(it)?.time }?.let { return it }
    return 0L
}
