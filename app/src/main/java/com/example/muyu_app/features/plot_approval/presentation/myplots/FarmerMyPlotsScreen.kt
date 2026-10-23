package com.example.muyu_app.features.plot_approval.presentation.myplots

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.designsystem.components.PlotPhoto
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.environment.Environment
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuFinancedBg
import com.example.muyu_app.ui.theme.MuyuFinancedText
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuHeader
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(20.dp)
private val ButtonShape = RoundedCornerShape(10.dp)
private val SectionBg = Color(0xFFF1F1F1)
private val Muted = Color(0xFF4F5A50)
private val AvailableBg = Color(0xFFFFD9D9)
private val AvailableText = Color(0xFF9E2F2F)
private val RainBannerBg = Color(0xFFFED2BD)
private val RainText = Color(0xFF9E2F2F)
private val OkAlertBg = Color(0xFFCBF5B8)
private val OkAlertText = Color(0xFF0F3F0D)

@Composable
fun FarmerMyPlotsScreen(
    onPublish: () -> Unit,
    onViewPlot: (Plot) -> Unit,
    onWeatherAlert: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    var plots by remember { mutableStateOf<List<Plot>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(FarmerPlotFilter.All) }
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()

    LaunchedEffect(lifecycleState.value) {
        if (!lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) return@LaunchedEffect
        val email = container.fakeStore.currentUser?.email
            ?: Environment.DemoCredentials.FARMER_EMAIL
        container.getPlotsUseCase().onSuccess { all ->
            plots = all
                .filter { it.ownerEmail.equals(email, ignoreCase = true) }
                .map { plot ->
                    container.fakeStore.ensureMilestones(plot.id)?.let { container.fakeStore.toDomain(it) }
                        ?: plot
                }
        }
    }

    val visiblePlots = remember(plots, searchQuery, filter) {
        plots
            .filter { it.matchesFarmerFilter(filter) && it.matchesSearch(searchQuery) }
            .sortedBy { it.name.lowercase() }
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
                    .padding(horizontal = 28.dp)
                    .padding(top = 16.dp, bottom = 18.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.farmer_my_plots),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White,
                        ),
                    )
                    Row(
                        modifier = Modifier
                            .clip(ButtonShape)
                            .background(Color(0xFF0F3F0D))
                            .clickable(onClick = onPublish)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MuyuAsset(
                            fileName = "farmer/plus.svg",
                            contentDescription = null,
                            width = 18.dp,
                            height = 18.dp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.farmer_publish),
                            style = TextStyle(
                                fontFamily = UrbanistBold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFFEAFAE9),
                            ),
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SectionBg)
                    .padding(horizontal = 28.dp)
                    .padding(top = 12.dp, bottom = 14.dp),
            ) {
                SearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                )
                Spacer(modifier = Modifier.height(12.dp))
                FilterChips(
                    selected = filter,
                    onSelected = { filter = it },
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                if (visiblePlots.isEmpty()) {
                    Text(
                        text = stringResource(R.string.farmer_no_plots_match),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 16.sp,
                            color = Muted,
                        ),
                    )
                } else {
                    visiblePlots.forEach { plot ->
                        FarmerPlotCard(
                            plot = plot,
                            onViewPlot = { onViewPlot(plot) },
                            onWeatherAlert = onWeatherAlert,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        MuyuBottomBar(
            selectedTab = MuyuTab.Plot,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CardShape)
            .background(Color.White)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MuyuAsset(
            fileName = "farmer/search.svg",
            contentDescription = stringResource(R.string.farmer_search_icon),
            width = 20.dp,
            height = 20.dp,
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 16.sp,
                color = Color.Black,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.farmer_search_plots_gps),
                        color = Muted,
                        fontSize = 16.sp,
                    )
                }
                inner()
            },
        )
        MuyuAsset(
            fileName = "farmer/filter.svg",
            contentDescription = stringResource(R.string.farmer_filter_icon),
            width = 18.dp,
            height = 18.dp,
        )
    }
}

@Composable
private fun FilterChips(selected: FarmerPlotFilter, onSelected: (FarmerPlotFilter) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            label = stringResource(R.string.farmer_filter_all),
            selected = selected == FarmerPlotFilter.All,
            onClick = { onSelected(FarmerPlotFilter.All) },
            selectedBg = MuyuForest,
            selectedFg = Color(0xFFFFFEFE),
        )
        FilterChip(
            label = stringResource(R.string.farmer_status_funded),
            selected = selected == FarmerPlotFilter.Funded,
            onClick = { onSelected(FarmerPlotFilter.Funded) },
            selectedBg = OkAlertBg,
            selectedFg = Color(0xFF579226),
        )
        FilterChip(
            label = stringResource(R.string.merchant_available),
            selected = selected == FarmerPlotFilter.Available,
            onClick = { onSelected(FarmerPlotFilter.Available) },
            selectedBg = OkAlertBg,
            selectedFg = Color(0xFF579226),
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedBg: Color,
    selectedFg: Color,
) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(if (selected) selectedBg else OkAlertBg.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (selected) selectedFg else Color(0xFF579226).copy(alpha = 0.7f),
            ),
        )
    }
}

@Composable
private fun FarmerPlotCard(
    plot: Plot,
    onViewPlot: () -> Unit,
    onWeatherAlert: () -> Unit,
) {
    val funded = plot.isFundedPlot()
    val approved = plot.approvedMilestoneCount()
    val total = 4
    val progress = if (funded) plot.progress.coerceIn(0, 100) else 0
    val stageRes = plot.stageTitleRes()
    val stageLabel = when {
        !funded -> stringResource(R.string.farmer_status_listed)
        stageRes != null -> stringResource(stageRes)
        else -> plot.stage
    }
    val rainRisk = plot.hasHeavyRainRisk()

    val areaText = if (plot.area.endsWith("ha", ignoreCase = true)) plot.area else "${plot.area} ha"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = plot.name,
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
            )
            PlotStatusChip(plot = plot, funded = funded)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            PlotThumbnail(plot = plot)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${plot.crop} · $areaText",
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 16.sp,
                        color = Muted,
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ubication),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = plot.place.ifBlank { "—" },
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 16.sp,
                            color = Muted,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    Text(
                        text = stringResource(R.string.farmer_stage_label),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 16.sp,
                            color = Muted,
                        ),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stageLabel,
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProgressBar(progress = progress, enabled = funded)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (funded) {
                        stringResource(
                            R.string.farmer_milestones_progress,
                            progress,
                            approved,
                            total,
                        )
                    } else {
                        stringResource(R.string.farmer_unfunded_milestones, total)
                    },
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MuyuForest,
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (rainRisk) {
                    WeatherAlertRow(
                        rainRisk = true,
                        onClick = onWeatherAlert,
                    )
                } else if (funded) {
                    WeatherAlertRow(rainRisk = false, onClick = {})
                } else {
                    WeatherAlertRow(rainRisk = false, onClick = {}, neutral = true)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(ButtonShape)
                    .background(MuyuAction)
                    .clickable(onClick = onViewPlot),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.farmer_view_plot),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White,
                    ),
                )
            }
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(32.dp)
                    .clip(ButtonShape)
                    .background(Color(0xFFD9D9D9))
                    .clickable(onClick = onViewPlot),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "⋮", fontSize = 18.sp, color = Color.Black)
            }
        }
    }
}

@Composable
private fun PlotStatusChip(plot: Plot, funded: Boolean) {
    val (bg, fg, label) = when {
        funded -> Triple(MuyuFinancedBg, MuyuFinancedText, stringResource(R.string.farmer_status_funded))
        plot.status == "available" -> Triple(AvailableBg, AvailableText, stringResource(R.string.merchant_available))
        else -> Triple(MuyuFinancedBg, MuyuFinancedText, stringResource(R.string.farmer_status_published))
    }
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = fg,
            ),
        )
    }
}

@Composable
private fun PlotThumbnail(plot: Plot) {
    PlotPhoto(
        imageUri = plot.imageUri,
        description = plot.name,
        modifier = Modifier
            .size(width = 104.dp, height = 140.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MuyuForest, RoundedCornerShape(20.dp)),
    )
}

@Composable
private fun ProgressBar(progress: Int, enabled: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(ChipShape)
            .background(if (enabled) OkAlertBg else SectionBg),
    ) {
        if (enabled && progress > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress / 100f)
                    .height(16.dp)
                    .clip(ChipShape)
                    .background(MuyuHeader),
            )
        }
    }
}

@Composable
private fun WeatherAlertRow(
    rainRisk: Boolean,
    onClick: () -> Unit,
    neutral: Boolean = false,
) {
    val bg = when {
        rainRisk -> RainBannerBg
        neutral -> SectionBg
        else -> OkAlertBg
    }
    val fg = when {
        rainRisk -> RainText
        neutral -> Muted
        else -> OkAlertText
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ButtonShape)
            .background(bg)
            .clickable(enabled = rainRisk, onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (rainRisk) {
            Image(
                painter = painterResource(R.drawable.weather_nublado),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        } else if (!neutral) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MuyuForest),
                contentAlignment = Alignment.Center,
            ) {
                MuyuAsset(
                    fileName = "publish/check.svg",
                    contentDescription = null,
                    width = 14.dp,
                    height = 14.dp,
                )
            }
        }
        Text(
            text = when {
                rainRisk -> stringResource(R.string.farmer_heavy_rain_risk)
                neutral -> stringResource(R.string.farmer_no_active_alerts)
                else -> stringResource(R.string.farmer_no_active_alerts)
            },
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = fg,
            ),
        )
    }
}