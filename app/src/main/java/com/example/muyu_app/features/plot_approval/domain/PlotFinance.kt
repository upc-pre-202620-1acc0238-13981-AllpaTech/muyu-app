package com.example.muyu_app.features.plot_approval.domain

import com.example.muyu_app.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

data class DisbursementStep(
    val titleRes: Int,
    val amount: Double,
    val percent: Double,
)

object PlotFinance {
    private val locale = Locale("es", "PE")
    private val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
    )

    fun parseAmount(raw: String): Double {
        val digits = raw.replace(Regex("[^0-9.]"), "")
        return digits.toDoubleOrNull() ?: 8500.0
    }

    fun formatAmount(value: Double): String {
        val rounded = value.roundToInt().toString()
        val grouped = rounded.reversed().chunked(3).joinToString(" ").reversed()
        return "S/ $grouped"
    }

    fun defaultAmount(draft: PlotDraft): Double {
        return if (draft.amount.isBlank()) 8500.0 else parseAmount(draft.amount)
    }

    fun disbursement(total: Double): List<DisbursementStep> {
        val planting = total * 0.2353
        val irrigation = total * 0.2353
        val fertilization = total * 0.2353
        val harvest = total - planting - irrigation - fertilization
        return listOf(
            DisbursementStep(R.string.publish_plan_planting, planting, 23.53),
            DisbursementStep(R.string.publish_plan_irrigation, irrigation, 23.53),
            DisbursementStep(R.string.publish_plan_fertilization, fertilization, 23.53),
            DisbursementStep(R.string.publish_plan_harvest, harvest, 29.41),
        )
    }

    fun cycleLabel(harvestDate: String): String {
        val harvest = parseDate(harvestDate) ?: return "6 months · 4 milestones"
        val start = Calendar.getInstance()
        val end = Calendar.getInstance().apply { time = harvest }
        return "${monthAbbr(start)} ${start.get(Calendar.YEAR)} to ${monthAbbr(end)} ${end.get(Calendar.YEAR)} · 4 milestones"
    }

    fun publishedToday(): String {
        val now = Calendar.getInstance()
        return "${now.get(Calendar.DAY_OF_MONTH)} ${monthAbbr(now)} ${now.get(Calendar.YEAR)}"
    }

    fun shortDate(value: String = publishedToday()): String {
        val parsed = parseDate(value)
        val calendar = Calendar.getInstance()
        if (parsed != null) calendar.time = parsed
        return "${calendar.get(Calendar.DAY_OF_MONTH)} ${monthAbbr(calendar)}"
    }

    fun shortDateFromMillis(millis: Long): String {
        if (millis <= 0L) return "—"
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        return "${calendar.get(Calendar.DAY_OF_MONTH)} ${monthAbbr(calendar)}"
    }

    fun seasonYear(harvestDate: String): String {
        val parsed = parseDate(harvestDate) ?: return Calendar.getInstance().get(Calendar.YEAR).toString()
        return Calendar.getInstance().apply { time = parsed }.get(Calendar.YEAR).toString()
    }

    fun formatCoordinates(lat: Double, lng: Double): String {
        val latDir = if (lat < 0) "S" else "N"
        val lngDir = if (lng < 0) "W" else "E"
        return "${"%.4f".format(locale, abs(lat))} $latDir, ${"%.4f".format(locale, abs(lng))} $lngDir"
    }

    fun parseDate(value: String): Date? {
        if (value.isBlank()) return null
        val formats = listOf("d MMM yyyy", "dd MMM yyyy", "d MMMM yyyy", "yyyy-MM-dd")
        formats.forEach { pattern ->
            runCatching {
                return SimpleDateFormat(pattern, Locale.ENGLISH).parse(value)
            }
            runCatching {
                return SimpleDateFormat(pattern, locale).parse(value)
            }
        }
        return null
    }

    private fun monthAbbr(calendar: Calendar): String = months[calendar.get(Calendar.MONTH)]
}
