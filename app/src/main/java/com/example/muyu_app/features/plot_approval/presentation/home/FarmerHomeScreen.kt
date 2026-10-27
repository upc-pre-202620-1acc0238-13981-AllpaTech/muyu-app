package com.example.muyu_app.features.plot_approval.presentation.home

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.LanguageToggleButton
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.designsystem.components.PlotPhoto
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.i18n.AppLanguage
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.climate_monitoring.data.OpenMeteoWeatherClient
import com.example.muyu_app.features.climate_monitoring.presentation.alert.farmerPlotForWeather
import com.example.muyu_app.features.climate_monitoring.presentation.alert.resolveWeatherLocation
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.presentation.myplots.formatPlotArea
import com.example.muyu_app.features.plot_approval.presentation.myplots.matchesSearch
import com.example.muyu_app.features.plot_approval.presentation.myplots.stageTitleRes
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuFinancedText
import com.example.muyu_app.ui.theme.MuyuField
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuLocateBg
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuNearText
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.MuyuScrollThumb
import com.example.muyu_app.ui.theme.MuyuSkip
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBlack
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistExtraBold
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val ScreenPadding = 29.dp
private val PillShape = RoundedCornerShape(20.dp)
private val CardHeight = 248.dp
private val CardPhotoHeight = 145.dp
private val CardPanelTop = 119.dp
private val CardPanelShape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp)
private val BottomBarHeight = 70.dp
private val Muted = Color(0xFF4F5A50)
private val SearchHint = Color.Black.copy(alpha = 0.5f)

private enum class HomeFilter(@param:StringRes val label: Int, val minWidth: Dp) {
    All(R.string.farmer_filter_all, 74.dp),
    Funded(R.string.farmer_status_funded, 111.dp),
    Approved(R.string.farmer_status_approved, 128.dp),
    Pending(R.string.farmer_status_pending, 128.dp),
}

private fun Plot.homeFilter(): HomeFilter = when (status.lowercase()) {
    "funded" -> HomeFilter.Funded
    "approved" -> HomeFilter.Approved
    else -> HomeFilter.Pending
}

private fun Plot.matchesHomeFilter(filter: HomeFilter): Boolean =
    filter == HomeFilter.All || homeFilter() == filter

private data class CardStyle(
    @param:StringRes val label: Int,
    val panel: Color,
    val track: Color,
    val chipBg: Color,
    val chipText: Color,
    val chipFont: FontFamily,
)

private fun Plot.cardStyle(): CardStyle = when (homeFilter()) {
    HomeFilter.Funded -> CardStyle(
        label = R.string.farmer_status_funded,
        panel = Color(0xFF362216),
        track = Color(0xFF70442A),
        chipBg = Color(0x99C8DBFF),
        chipText = MuyuFinancedText,
        chipFont = UrbanistExtraBold,
    )
    HomeFilter.Pending -> CardStyle(
        label = R.string.farmer_status_pending,
        panel = Color(0xFF765B2F),
        track = Color(0xFFA0814F),
        chipBg = Color(0x99FED2BD),
        chipText = MuyuPendingText,
        chipFont = UrbanistBold,
    )
    else -> CardStyle(
        label = R.string.farmer_status_approved,
        panel = Color(0xFF626E23),
        track = Color(0xFFBFCE6C),
        chipBg = Color(0x99B8DF98),
        chipText = MuyuForest,
        chipFont = UrbanistBold,
    )
}

private class CropArt(val fileName: String, val width: Dp, val height: Dp, val end: Dp, val top: Dp)

private fun Plot.cropKey(): String? {
    val c = crop.lowercase()
    return when {
        "coffee" in c || "café" in c || "cafe" in c -> "coffee"
        "corn" in c || "maíz" in c || "maiz" in c -> "corn"
        "quinoa" in c || "quinua" in c -> "quinoa"
        else -> null
    }
}

private fun Plot.cropArt(): CropArt? = when (cropKey()) {
    "coffee" -> CropArt("home/crop_coffee.png", 39.dp, 43.dp, 0.dp, 100.dp)
    "corn" -> CropArt("home/crop_corn.png", 60.dp, 74.dp, 5.dp, 85.dp)
    "quinoa" -> CropArt("home/crop_quinoa.png", 81.dp, 118.dp, 8.dp, 81.dp)
    else -> null
}

private fun Plot.photoUri(): String? =
    imageUri?.takeIf { it.isNotBlank() }
        ?: cropKey()?.let { "file:///android_asset/home/photo_$it.jpg" }

@Composable
fun FarmerHomeScreen(
    onWeather: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    onPlotSelected: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    val farmerName = container.fakeStore.currentUser?.displayName.orEmpty()
    var currentTemperature by remember { mutableStateOf<Double?>(null) }
    var plots by remember { mutableStateOf<List<Plot>>(emptyList()) }
    var isLoadingPlots by remember { mutableStateOf(true) }
    var plotsError by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(HomeFilter.All) }
    val languages = container.languages
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()

    LaunchedEffect(Unit) {
        currentTemperature = withContext(Dispatchers.IO) {
            val location = context.resolveWeatherLocation(container.farmerPlotForWeather())
            location?.let {
                OpenMeteoWeatherClient.fetch(
                    latitude = it.latitude,
                    longitude = it.longitude,
                )?.current?.temperature
            }
        }
    }

    LaunchedEffect(lifecycleState.value) {
        if (!lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) return@LaunchedEffect
        isLoadingPlots = true
        plotsError = null
        val email = container.fakeStore.currentUser?.email.orEmpty()
        container.getPlotsUseCase()
            .onSuccess { all ->
                plots = all
                    .filter { it.ownerEmail.equals(email, ignoreCase = true) }
                    .map { plot ->
                        container.fakeStore.ensureMilestones(plot.id)?.let { container.fakeStore.toDomain(it) }
                            ?: plot
                    }
            }
            .onFailure { error -> plotsError = error.message.orEmpty() }
        isLoadingPlots = false
    }

    val filteredPlots = remember(plots, selectedFilter, searchQuery) {
        plots.filter { it.matchesHomeFilter(selectedFilter) && it.matchesSearch(searchQuery) }
    }
    val listScroll = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuLocateBg),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            FarmerHeader(
                farmerName = farmerName,
                temperature = currentTemperature,
                language = languages.farmer,
                onLanguageToggle = { languages.toggle(isMerchant = false) },
                onWeather = onWeather,
                query = searchQuery,
                onQueryChange = { searchQuery = it },
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilterChips(
                selected = selectedFilter,
                onSelected = { selectedFilter = it },
            )
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(listScroll)
                        .padding(start = ScreenPadding, end = 30.dp, bottom = MuyuScrollBottomInset),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    when {
                        isLoadingPlots && plots.isEmpty() -> Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(32.dp),
                                color = MuyuAction,
                            )
                        }
                        plotsError != null -> StateMessage(stringResource(R.string.farmer_plots_loading_error), Color(0xFF9E2F2F))
                        plots.isEmpty() -> EmptyPlots(onGoToPlots = { onTabSelected(MuyuTab.Plot) })
                        filteredPlots.isEmpty() -> StateMessage(stringResource(R.string.farmer_no_plots_match), Muted)
                        else -> filteredPlots.forEach { plot ->
                            HomePlotCard(
                                plot = plot,
                                onClick = {
                                    container.selectedPlot = plot
                                    onPlotSelected()
                                },
                            )
                        }
                    }
                }
                HomeScrollbar(
                    state = listScroll,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 5.dp)
                        .navigationBarsPadding()
                        .padding(bottom = BottomBarHeight),
                )
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
private fun FarmerHeader(
    farmerName: String,
    temperature: Double?,
    language: AppLanguage,
    onLanguageToggle: () -> Unit,
    onWeather: () -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MuyuNavBar)
            .statusBarsPadding()
            .padding(start = ScreenPadding, end = 30.dp)
            .padding(top = 22.dp, bottom = 22.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.farmer_welcome) + ",",
                    style = TextStyle(
                        fontFamily = UrbanistLight,
                        fontWeight = FontWeight.Light,
                        fontSize = 20.sp,
                        color = MuyuField,
                    ),
                )
                Text(
                    text = farmerName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = UrbanistBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = MuyuField,
                    ),
                )
            }
            LanguageToggleButton(
                language = language,
                onToggle = onLanguageToggle,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                modifier = Modifier
                    .clip(PillShape)
                    .clickable(onClick = onWeather),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MuyuAsset(
                    fileName = "home/weather_sun_cloud.png",
                    contentDescription = stringResource(R.string.farmer_weather),
                    width = 40.dp,
                    height = 36.dp,
                )
                Text(
                    text = temperature?.let { "${it.toInt()}°C" } ?: "—°C",
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MuyuField,
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            MuyuAsset(
                fileName = "home/flag_peru.svg",
                contentDescription = stringResource(R.string.farmer_flag_peru),
                width = 28.dp,
                height = 16.dp,
            )
            Text(
                text = stringResource(R.string.farmer_country_peru),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 15.sp,
                    color = MuyuField,
                ),
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        SearchBar(query = query, onQueryChange = onQueryChange)
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(PillShape)
            .background(Color.White)
            .padding(start = 17.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MuyuAsset(
            fileName = "farmer/search.svg",
            contentDescription = stringResource(R.string.farmer_search_icon),
            width = 20.dp,
            height = 20.dp,
        )
        Spacer(modifier = Modifier.width(16.dp))
        val textStyle = TextStyle(
            fontFamily = UrbanistBold,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.farmer_search),
                        style = textStyle.copy(color = SearchHint),
                    )
                }
                inner()
            },
        )
    }
}

@Composable
private fun FilterChips(
    selected: HomeFilter,
    onSelected: (HomeFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HomeFilter.entries.forEach { filter ->
            val isSelected = filter == selected
            Box(
                modifier = Modifier
                    .widthIn(min = filter.minWidth)
                    .height(24.dp)
                    .clip(PillShape)
                    .background(if (isSelected) MuyuForest else MuyuSkip)
                    .clickable { onSelected(filter) }
                    .padding(horizontal = 11.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(filter.label),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isSelected) MuyuField else MuyuNearText,
                    ),
                )
            }
        }
    }
}

@Composable
private fun HomePlotCard(
    plot: Plot,
    onClick: () -> Unit,
) {
    val style = plot.cardStyle()
    val progress = plot.progress.coerceIn(0, 100)
    val stageLabel = plot.stageTitleRes()?.let { stringResource(it) }
        ?: plot.stage.ifBlank { "—" }
    val bodyStyle = TextStyle(
        fontFamily = UrbanistRegular,
        fontSize = 16.sp,
        color = MuyuField,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(CardHeight)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
    ) {
        PlotPhoto(
            imageUri = plot.photoUri(),
            description = plot.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(CardPhotoHeight)
                .clip(RoundedCornerShape(20.dp)),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(CardHeight - CardPanelTop)
                .offset(y = CardPanelTop)
                .clip(CardPanelShape)
                .background(style.panel)
                .padding(start = 20.dp, end = 19.dp, top = 12.dp),
        ) {
            Text(
                text = plot.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = bodyStyle.copy(fontFamily = UrbanistSemiBold, fontWeight = FontWeight.SemiBold),
            )
            Text(text = "${plot.crop}  ·  ${formatPlotArea(plot.area)}", style = bodyStyle)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "${stringResource(R.string.farmer_stage_label)}  $stageLabel", style = bodyStyle)
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(PillShape)
                    .background(style.track),
            ) {
                if (progress > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress / 100f)
                            .fillMaxHeight()
                            .clip(PillShape)
                            .background(Color.White),
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = stringResource(R.string.farmer_progress_complete, progress), style = bodyStyle)
        }
        plot.cropArt()?.let { art ->
            MuyuAsset(
                fileName = art.fileName,
                contentDescription = null,
                width = art.width,
                height = art.height,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = art.end, top = art.top),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 18.dp)
                .widthIn(min = 103.dp)
                .height(23.dp)
                .clip(PillShape)
                .background(style.chipBg)
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(style.label),
                style = TextStyle(
                    fontFamily = style.chipFont,
                    fontSize = 16.sp,
                    color = style.chipText,
                ),
            )
        }
    }
}

@Composable
private fun HomeScrollbar(
    state: ScrollState,
    modifier: Modifier = Modifier,
) {
    if (state.maxValue == 0 || state.maxValue == Int.MAX_VALUE) return
    BoxWithConstraints(
        modifier = modifier
            .width(17.dp)
            .fillMaxHeight()
            .clip(PillShape)
            .background(Color.White)
            .padding(3.dp),
    ) {
        val density = LocalDensity.current
        val trackPx = with(density) { maxHeight.toPx() }
        val viewportPx = state.viewportSize.toFloat()
        val contentPx = viewportPx + state.maxValue
        val thumbPx = (trackPx * viewportPx / contentPx).coerceAtLeast(with(density) { 32.dp.toPx() })
        val offsetPx = (trackPx - thumbPx) * state.value / state.maxValue
        Box(
            modifier = Modifier
                .offset(y = with(density) { offsetPx.toDp() })
                .width(11.dp)
                .height(with(density) { thumbPx.toDp() })
                .clip(PillShape)
                .background(MuyuScrollThumb),
        )
    }
}

@Composable
private fun StateMessage(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontFamily = UrbanistSemiBold,
        fontSize = 16.sp,
        modifier = Modifier.padding(vertical = 24.dp),
    )
}

@Composable
private fun EmptyPlots(onGoToPlots: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Eco,
            contentDescription = null,
            tint = MuyuAction,
            modifier = Modifier.size(56.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.farmer_no_plots_title),
            fontFamily = UrbanistBold,
            fontSize = 22.sp,
            color = MuyuForest,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.farmer_no_plots_body),
            fontFamily = UrbanistRegular,
            fontSize = 14.sp,
            color = Muted,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onGoToPlots,
            colors = ButtonDefaults.buttonColors(containerColor = MuyuAction),
        ) {
            Text(
                text = stringResource(R.string.farmer_go_to_my_plots),
                color = Color.White,
                fontFamily = UrbanistSemiBold,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun FarmerHomeScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            FarmerHomeScreen(onWeather = {}, onTabSelected = {})
        }
    }
}