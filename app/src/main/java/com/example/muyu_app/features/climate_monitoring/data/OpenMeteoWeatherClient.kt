package com.example.muyu_app.features.climate_monitoring.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class OpenMeteoResponse(
    val elevation: Double? = null,
    val current: CurrentWeather? = null,
    val daily: DailyForecast? = null,
    val hourly: HourlyForecast? = null,
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double? = null,
    @SerializedName("weather_code") val weatherCode: Int? = null,
)

data class DailyForecast(
    val time: List<String> = emptyList(),
    @SerializedName("weather_code") val weatherCode: List<Int> = emptyList(),
    @SerializedName("temperature_2m_max") val tempMax: List<Double> = emptyList(),
    @SerializedName("temperature_2m_min") val tempMin: List<Double> = emptyList(),
    @SerializedName("precipitation_probability_max") val precipProbMax: List<Int> = emptyList(),
)

data class HourlyForecast(
    val time: List<String> = emptyList(),
    @SerializedName("temperature_2m") val temperature: List<Double> = emptyList(),
    @SerializedName("precipitation_probability") val precipProb: List<Int> = emptyList(),
)

object OpenMeteoWeatherClient {
    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    fun fetch(latitude: Double, longitude: Double): OpenMeteoResponse? {
        val url =
            "https://api.open-meteo.com/v1/forecast?" +
                "latitude=$latitude&longitude=$longitude" +
                "&current=temperature_2m,weather_code" +
                "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max" +
                "&hourly=temperature_2m,precipitation_probability" +
                "&forecast_days=5&timezone=auto"
        val request = Request.Builder().url(url).get().build()
        return runCatching {
            http.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                gson.fromJson(body, OpenMeteoResponse::class.java)
            }
        }.getOrNull()
    }
}
