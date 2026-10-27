package com.example.muyu_app.features.climate_monitoring.presentation.alert

import android.content.Context
import android.location.Location
import com.example.muyu_app.R
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.features.climate_monitoring.data.OpenMeteoResponse
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.core.location.bestLastKnownLocation
import com.example.muyu_app.core.location.hasLocationPermission
import com.example.muyu_app.core.location.requestCurrentLocation
import com.example.muyu_app.core.location.reverseGeocode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

enum class WeatherRisk { High, Medium, Low }

data class WeatherForecastDay(
    val dayLabel: String,
    val iconRes: Int,
    val tempHighC: Int?,
    val tempLowC: Int?,
)

data class WeatherAlertContent(
    val plotTitle: String,
    val cycleLabel: String,
    val alertTitleRes: Int,
    val alertTitleText: String?,
    val areaLine: String,
    val estTemp: String,
    val estTime: String,
    val riskLabelRes: Int,
    val risk: WeatherRisk,
    val alertIconRes: Int,
    val recommendations: List<Int>,
    val forecastDays: List<WeatherForecastDay>,
)

fun AppContainer.farmerPlotForWeather(): Plot? {
    selectedPlot?.let { return it }
    val email = fakeStore.currentUser?.email.orEmpty()
    val records = fakeStore.plots.filter { it.ownerEmail.equals(email, ignoreCase = true) }
    val withGps = records.firstOrNull { it.latitude != null && it.longitude != null }
    return (withGps ?: records.firstOrNull())?.let { fakeStore.toDomain(it) }
}

suspend fun Context.resolveWeatherLocation(plot: Plot?): Location? {
    if (hasLocationPermission(this)) {
        awaitFreshOrLastLocation()?.let { return it }
    }
    val plotLat = plot?.latitude
    val plotLng = plot?.longitude
    if (plotLat != null && plotLng != null) {
        return Location("plot").apply {
            latitude = plotLat
            longitude = plotLng
        }
    }
    return null
}

private suspend fun Context.awaitFreshOrLastLocation(): Location? {
    bestLastKnownLocation(this)?.let { return it }
    if (!hasLocationPermission(this)) return null
    return suspendCancellableCoroutine { cont ->
        requestCurrentLocation(
            context = this,
            preferCachedMaxAgeMs = 120_000L,
            timeoutMs = 8_000L,
            onResult = { if (cont.isActive) cont.resume(it) },
            onError = {
                if (cont.isActive) cont.resume(bestLastKnownLocation(this))
            },
        )
    }
}

fun buildWeatherAlertContent(
    context: Context,
    plot: Plot?,
    location: Location,
    response: OpenMeteoResponse,
): WeatherAlertContent {
    val locale = Locale.getDefault()
    val place = reverseGeocode(context, location.latitude, location.longitude)
    val placeLine = listOfNotNull(
        place?.district?.ifBlank { null },
        place?.province?.ifBlank { null },
        plot?.crop?.ifBlank { null },
    ).joinToString(", ")
    val elevation = response.elevation ?: 0.0
    val areaLine = when {
        placeLine.isNotBlank() -> "$placeLine · ${elevation.roundInt()} m"
        plot?.place?.isNotBlank() == true -> "${plot.place} · ${elevation.roundInt()} m"
        else -> "${PlotFinance.formatCoordinates(location.latitude, location.longitude)} · ${elevation.roundInt()} m"
    }

    val plotName = plot?.name.orEmpty()
    val crop = plot?.crop?.ifBlank { null } ?: "—"
    val cycleYear = plot?.harvestDate?.let { PlotFinance.seasonYear(it) }
        ?: Calendar.getInstance().get(Calendar.YEAR).toString()
    val plotTitle = "$plotName · $crop"
    val cycleLabel = context.getString(R.string.weather_cycle_fmt, cycleYear)

    val daily = response.daily
    val hourly = response.hourly
    val tonightMin = tonightMinimumCelsius(hourly)
    val todayMaxRain = daily?.precipProbMax?.firstOrNull() ?: 0
    val todayMax = daily?.tempMax?.firstOrNull()
    val todayMin = daily?.tempMin?.firstOrNull()
    val todayCode = daily?.weatherCode?.firstOrNull() ?: response.current?.weatherCode ?: 0
    val currentTemp = response.current?.temperature ?: nearestHourlyTemperature(hourly)

    val kind = when {
        tonightMin != null && tonightMin <= 0 -> AlertKind(
            R.string.weather_severe,
            R.drawable.weather_snow,
            WeatherRisk.High,
            context.getString(R.string.weather_tonight),
        )
        tonightMin != null && tonightMin <= 3 -> AlertKind(
            R.string.weather_frost_moderate,
            R.drawable.weather_snow,
            WeatherRisk.Medium,
            context.getString(R.string.weather_tonight),
        )
        todayMaxRain >= 70 -> AlertKind(
            R.string.weather_heavy_rain,
            R.drawable.weather_rain_alert,
            WeatherRisk.High,
            context.getString(R.string.weather_today),
        )
        todayMaxRain >= 45 -> AlertKind(
            R.string.weather_rain_moderate,
            R.drawable.weather_rain,
            WeatherRisk.Medium,
            context.getString(R.string.weather_today),
        )
        else -> AlertKind(
            R.string.weather_stable,
            weatherIconFor(
                code = todayCode,
                tempMax = todayMax,
                tempMin = todayMin,
                precipProb = todayMaxRain,
            ),
            WeatherRisk.Low,
            context.getString(R.string.weather_today),
        )
    }

    val alertIcon = when {
        kind.risk != WeatherRisk.Low || kind.icon == R.drawable.weather_nublado -> kind.icon
        else -> weatherIconFor(
            code = response.current?.weatherCode ?: todayCode,
            tempMax = todayMax,
            tempMin = todayMin,
            tempNow = currentTemp,
            precipProb = todayMaxRain,
        )
    }

    val estTemp = when {
        currentTemp != null -> "${currentTemp.roundInt()}°C"
        todayMax != null && todayMin != null ->
            context.getString(R.string.weather_temp_range_fmt, todayMax.roundInt(), todayMin.roundInt())
        tonightMin != null && tonightMin <= 3 -> "${tonightMin.roundInt()}°C"
        todayMin != null -> "${todayMin.roundInt()}°C"
        else -> "—"
    }

    val riskLabelRes = when (kind.risk) {
        WeatherRisk.High -> R.string.weather_high
        WeatherRisk.Medium -> R.string.weather_risk_medium
        WeatherRisk.Low -> R.string.weather_risk_low
    }

    val recommendations = recommendationsFor(kind.risk, tonightMin, todayMaxRain)

    val forecastDays = daily?.time.orEmpty().take(5).mapIndexed { index, isoDate ->
        val code = daily?.weatherCode?.getOrNull(index) ?: 0
        val maxC = daily?.tempMax?.getOrNull(index)
        val minC = daily?.tempMin?.getOrNull(index)
        val rainProb = daily?.precipProbMax?.getOrNull(index)
        WeatherForecastDay(
            dayLabel = dayLabelFor(context, isoDate, index, locale),
            iconRes = weatherIconFor(code, maxC, minC, rainProb),
            tempHighC = maxC?.roundInt(),
            tempLowC = minC?.roundInt(),
        )
    }

    return WeatherAlertContent(
        plotTitle = plotTitle,
        cycleLabel = cycleLabel,
        alertTitleRes = kind.titleRes,
        alertTitleText = null,
        areaLine = areaLine,
        estTemp = estTemp,
        estTime = kind.time,
        riskLabelRes = riskLabelRes,
        risk = kind.risk,
        alertIconRes = if (kind.risk == WeatherRisk.Low && kind.titleRes == R.string.weather_stable) {
            alertIcon
        } else {
            kind.icon
        },
        recommendations = recommendations,
        forecastDays = forecastDays,
    )
}

private data class AlertKind(
    val titleRes: Int,
    val icon: Int,
    val risk: WeatherRisk,
    val time: String,
)

private fun tonightMinimumCelsius(hourly: com.example.muyu_app.features.climate_monitoring.data.HourlyForecast?): Double? {
    val times = hourly?.time.orEmpty()
    val temps = hourly?.temperature.orEmpty()
    if (times.isEmpty() || temps.isEmpty()) return null
    val now = Calendar.getInstance()
    val end = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 12) }
    var min: Double? = null
    times.forEachIndexed { index, iso ->
        val cal = parseIsoHour(iso) ?: return@forEachIndexed
        if (cal.after(now) && cal.before(end)) {
            val t = temps.getOrNull(index) ?: return@forEachIndexed
            min = if (min == null) t else minOf(min!!, t)
        }
    }
    return min ?: temps.take(24).minOrNull()
}

private fun parseIsoHour(iso: String): Calendar? = runCatching {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
    Calendar.getInstance().apply { time = parser.parse(iso)!! }
}.getOrNull()

private fun dayLabelFor(context: Context, isoDate: String, index: Int, locale: Locale): String {
    if (index == 0) return context.getString(R.string.weather_today)
    return runCatching {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(isoDate)!!
        SimpleDateFormat("EEE", locale).format(date).replaceFirstChar { it.uppercase() }
    }.getOrElse { isoDate.takeLast(5) }
}

private fun nearestHourlyTemperature(
    hourly: com.example.muyu_app.features.climate_monitoring.data.HourlyForecast?,
): Double? {
    val times = hourly?.time.orEmpty()
    val temps = hourly?.temperature.orEmpty()
    if (times.isEmpty() || temps.isEmpty()) return null
    val now = System.currentTimeMillis()
    var bestIndex = 0
    var bestDelta = Long.MAX_VALUE
    times.forEachIndexed { index, iso ->
        val cal = parseIsoHour(iso) ?: return@forEachIndexed
        val delta = kotlin.math.abs(cal.timeInMillis - now)
        if (delta < bestDelta) {
            bestDelta = delta
            bestIndex = index
        }
    }
    return temps.getOrNull(bestIndex)
}

private fun weatherIconFor(
    code: Int,
    tempMax: Double?,
    tempMin: Double?,
    precipProb: Int? = null,
    tempNow: Double? = null,
): Int {
    val rainFromCode = code in 51..67 || code in 80..82 || code in 95..99
    val rainFromProb = (precipProb ?: 0) >= 55
    if (rainFromCode || rainFromProb) {
        return when {
            code in 95..99 || (precipProb ?: 0) >= 75 -> R.drawable.weather_rain_alert
            else -> R.drawable.weather_rain
        }
    }
    if (code in 71..77 || code in 85..86) return R.drawable.weather_snow
    val minRef = tempMin ?: tempNow
    val maxRef = tempMax ?: tempNow
    if (minRef != null && minRef <= 0) return R.drawable.weather_snow
    if (maxRef != null && maxRef >= 30) return R.drawable.weather_sunny
    if (maxRef != null && maxRef >= 24) return R.drawable.weather_sun
    return when (code) {
        0 -> R.drawable.weather_sunny
        1, 2 -> R.drawable.weather_sun
        3 -> R.drawable.weather_nublado
        45, 48 -> R.drawable.weather_nublado
        else -> R.drawable.weather_nublado
    }
}

private fun recommendationsFor(
    risk: WeatherRisk,
    tonightMin: Double?,
    rainProb: Int,
): List<Int> = when {
    tonightMin != null && tonightMin <= 3 -> listOf(
        R.string.weather_rec_cover,
        R.string.weather_rec_sprinklers,
        R.string.weather_rec_avoid,
        R.string.weather_rec_advisor,
    )
    rainProb >= 45 -> listOf(
        R.string.weather_rec_drainage,
        R.string.weather_rec_cover,
        R.string.weather_rec_avoid,
        R.string.weather_rec_advisor,
    )
    else -> listOf(
        R.string.weather_rec_monitor,
        R.string.weather_rec_irrigation,
        R.string.weather_rec_avoid,
        R.string.weather_rec_advisor,
    )
}

private fun Double.roundInt(): Int = kotlin.math.round(this).toInt()
