package com.example.muyu_app.features.climate_monitoring.presentation.alert

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.climate_monitoring.data.OpenMeteoWeatherClient
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuStatCard
import com.example.muyu_app.ui.theme.MuyuWeatherCard
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistExtraBold
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistMedium
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val CardShape = RoundedCornerShape(20.dp)

@Composable
fun WeatherAlertScreen(
    onBack: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val container = LocalAppContainer.current
    var content by remember { mutableStateOf<WeatherAlertContent?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        loading = true
        errorMessage = null
        val plot = container.farmerPlotForWeather()
        val location = withContext(Dispatchers.IO) {
            context.resolveWeatherLocation(plot)
        }
        if (location == null) {
            loading = false
            errorMessage = context.getString(R.string.weather_location_unavailable)
            return@LaunchedEffect
        }
        val response = withContext(Dispatchers.IO) {
            OpenMeteoWeatherClient.fetch(location.latitude, location.longitude)
        }
        if (response == null) {
            loading = false
            errorMessage = context.getString(R.string.weather_location_unavailable)
            return@LaunchedEffect
        }
        content = buildWeatherAlertContent(context, plot, location, response)
        loading = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.weather_alert_title),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 21.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                when {
                    loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MuyuForest)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = stringResource(R.string.weather_loading),
                                    style = metaStyle(),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                    errorMessage != null -> {
                        Text(
                            text = errorMessage.orEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = metaStyle(),
                        )
                    }
                    content != null -> {
                        val data = content!!
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = data.plotTitle, style = metaStyle(), modifier = Modifier.weight(1f))
                            Text(text = " · ", style = metaStyle())
                            Text(text = data.cycleLabel, style = metaStyle())
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        AlertCard(data)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.weather_recommendations),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontFamily = UrbanistLight,
                                fontWeight = FontWeight.Light,
                                fontSize = 20.sp,
                                color = Color.Black,
                            ),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        RecommendationsCard(data.recommendations)
                        Spacer(modifier = Modifier.height(16.dp))
                        ForecastCard(data.forecastDays)
                    }
                }
            }
        }
        MuyuBottomBar(
            selectedTab = MuyuTab.Home,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun AlertCard(data: WeatherAlertContent) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(MuyuWeatherCard)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(data.alertIconRes),
            contentDescription = null,
            modifier = Modifier
                .width(71.dp)
                .height(69.dp),
            contentScale = ContentScale.Fit,
        )
        Text(
            text = data.alertTitleText ?: stringResource(data.alertTitleRes),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = data.areaLine,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Metric(
                modifier = Modifier.weight(1f),
                value = data.estTemp,
                label = stringResource(R.string.weather_est_temp),
            )
            MuyuAsset(
                fileName = "weather/divider.svg",
                contentDescription = null,
                width = 1.dp,
                height = 48.dp,
                modifier = Modifier.rotate(-90f),
            )
            Metric(
                modifier = Modifier.weight(1f),
                value = data.estTime,
                label = stringResource(R.string.weather_est_time),
            )
            MuyuAsset(
                fileName = "weather/divider.svg",
                contentDescription = null,
                width = 1.dp,
                height = 48.dp,
                modifier = Modifier.rotate(-90f),
            )
            Metric(
                modifier = Modifier.weight(1f),
                value = stringResource(data.riskLabelRes),
                label = stringResource(R.string.weather_risk),
            )
        }
    }
}

@Composable
private fun Metric(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
            ),
        )
        Text(
            text = label,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = Color.Black,
            ),
        )
    }
}

@Composable
private fun RecommendationsCard(recommendationIds: List<Int>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        recommendationIds.forEach { RecommendationRow(it) }
    }
}

@Composable
private fun RecommendationRow(@StringRes text: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MuyuAsset(
            fileName = "weather/check.svg",
            contentDescription = null,
            width = 19.dp,
            height = 14.dp,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(text),
            style = TextStyle(
                fontFamily = UrbanistMedium,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black,
            ),
        )
    }
}

@Composable
private fun ForecastCard(days: List<WeatherForecastDay>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(MuyuStatCard)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = stringResource(R.string.weather_forecast),
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            days.forEach { day ->
                ForecastDay(
                    dayLabel = day.dayLabel,
                    icon = day.iconRes,
                    tempHighC = day.tempHighC,
                    tempLowC = day.tempLowC,
                )
            }
        }
    }
}

@Composable
private fun ForecastDay(
    dayLabel: String,
    @DrawableRes icon: Int,
    tempHighC: Int?,
    tempLowC: Int?,
) {
    val tempLine = when {
        tempHighC != null && tempLowC != null -> "$tempHighC° / $tempLowC°"
        tempHighC != null -> "$tempHighC°"
        tempLowC != null -> "$tempLowC°"
        else -> "—"
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = dayLabel,
            style = TextStyle(
                fontFamily = UrbanistMedium,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.5f),
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(icon),
            contentDescription = dayLabel,
            modifier = Modifier
                .width(44.dp)
                .height(40.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = tempLine,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.Black,
            ),
        )
    }
}

private fun metaStyle() = TextStyle(
    fontFamily = UrbanistRegular,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = Color.Black.copy(alpha = 0.5f),
)

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun WeatherAlertScreenPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme {
        WeatherAlertScreen(onBack = {}, onTabSelected = {})
    }
}
