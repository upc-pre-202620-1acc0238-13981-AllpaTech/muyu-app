package com.example.muyu_app.features.plot_approval.presentation.home

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.environment.Environment
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.presentation.myplots.approvedMilestoneCount
import com.example.muyu_app.features.plot_approval.presentation.myplots.formatPlotArea
import com.example.muyu_app.features.plot_approval.presentation.myplots.isFundedPlot
import com.example.muyu_app.features.plot_approval.presentation.myplots.matchesSearch
import com.example.muyu_app.features.plot_approval.presentation.myplots.stageTitleRes
import com.example.muyu_app.ui.theme.MuyuFinancedBg
import com.example.muyu_app.ui.theme.MuyuFinancedText
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuHeader
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuStatCard
import com.example.muyu_app.ui.theme.MuyuWeatherChip
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBlack
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(20.dp)
private val ProgressTrack = Color(0xFFCBF5B8)
private val AvailableChipBg = Color(0xFFFED2BD)
private val AvailableChipText = Color(0xFF774308)
private val Muted = Color(0xFF4F5A50)

@Composable
fun FarmerHomeScreen(
    onWeather: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    onPlotSelected: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val farmerName = container.fakeStore.currentUser?.displayName
        ?: stringResource(R.string.farmer_name)
    var plots by remember { mutableStateOf<List<Plot>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val languages = container.languages
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

    val filteredPlots = remember(searchQuery, plots) {
        plots.filter { it.matchesSearch(searchQuery) }
    }

    val plotCount = plots.size
    val milestoneCount = plots.sumOf { it.approvedMilestoneCount() }
    val cropCount = plots
        .filter { it.status != "pending" }
        .map { it.crop }
        .distinct()
        .size

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            FarmerHeader(
                farmerName = farmerName,
                plotCount = plotCount,
                milestoneCount = milestoneCount,
                cropCount = cropCount,
                language = languages.farmer,
                onLanguageToggle = { languages.toggle(isMerchant = false) },
                onWeather = onWeather,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 30.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                Text(
                    text = stringResource(R.string.farmer_my_plots),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp,
                        color = Color.Black,
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                )

                Spacer(modifier = Modifier.height(16.dp))

                filteredPlots.forEach { plot ->
                    HomePlotCard(
                        plot = plot,
                        onClick = {
                            container.selectedPlot = plot
                            onPlotSelected()
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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
private fun FarmerHeader(
    farmerName: String,
    plotCount: Int,
    milestoneCount: Int,
    cropCount: Int,
    language: com.example.muyu_app.core.i18n.AppLanguage,
    onLanguageToggle: () -> Unit,
    onWeather: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MuyuHeader)
            .statusBarsPadding()
            .padding(horizontal = 30.dp)
            .padding(top = 12.dp, bottom = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.farmer_welcome),
            style = TextStyle(
                fontFamily = UrbanistLight,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = Color.White,
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = farmerName,
                style = TextStyle(
                    fontFamily = UrbanistBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = Color.White,
                ),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                com.example.muyu_app.core.designsystem.components.LanguageToggleButton(
                    language = language,
                    onToggle = onLanguageToggle,
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MuyuWeatherChip)
                        .clickable(onClick = onWeather),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.weather_nublado),
                        contentDescription = stringResource(R.string.farmer_weather),
                        modifier = Modifier
                            .width(28.dp)
                            .height(23.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StatCard(
                value = plotCount.toString(),
                label = stringResource(R.string.farmer_stat_plots),
            )
            StatCard(
                value = milestoneCount.toString(),
                label = stringResource(R.string.farmer_stat_milestones),
            )
            StatCard(
                value = cropCount.toString(),
                label = stringResource(R.string.farmer_stat_crop),
            )
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
) {
    Column(
        modifier = Modifier
            .width(104.dp)
            .height(110.dp)
            .clip(CardShape)
            .background(MuyuStatCard)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = value,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.5f),
            ),
        )
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
            .clip(CardShape)
            .background(Color.White)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MuyuAsset(
            fileName = "farmer/search.svg",
            contentDescription = stringResource(R.string.farmer_search_icon),
            width = 20.dp,
            height = 20.dp,
        )
        Spacer(modifier = Modifier.width(10.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.farmer_search_plots_gps),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Muted,
                        ),
                    )
                }
                inner()
            },
        )
        MuyuAsset(
            fileName = "farmer/filter.svg",
            contentDescription = stringResource(R.string.farmer_filter_icon),
            width = 18.dp,
            height = 15.dp,
        )
    }
}

@Composable
private fun HomePlotCard(
    plot: Plot,
    onClick: () -> Unit,
) {
    val funded = plot.isFundedPlot()
    val approved = plot.approvedMilestoneCount()
    val total = 4
    val progress = if (funded) plot.progress.coerceIn(0, 100) else 0
    val (statusLabel, statusBg, statusColor) = when {
        funded -> Triple(
            stringResource(R.string.farmer_status_funded),
            MuyuFinancedBg,
            MuyuFinancedText,
        )
        plot.status == "available" -> Triple(
            stringResource(R.string.merchant_available),
            AvailableChipBg,
            AvailableChipText,
        )
        else -> Triple(
            stringResource(R.string.farmer_status_listed),
            AvailableChipBg,
            AvailableChipText,
        )
    }
    val progressLabel = if (funded) {
        stringResource(R.string.farmer_milestones_progress, progress, approved, total)
    } else {
        stringResource(R.string.farmer_unfunded_milestones, total)
    }
    val stageRes = plot.stageTitleRes()
    val stageLabel = when {
        !funded -> stringResource(R.string.farmer_status_listed)
        stageRes != null -> stringResource(stageRes)
        else -> plot.stage.ifBlank { "—" }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
            Box(
                modifier = Modifier
                    .clip(ChipShape)
                    .background(statusBg)
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = statusLabel,
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = statusColor,
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${plot.crop} · ${formatPlotArea(plot.area)}",
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Muted,
            ),
        )
        Spacer(modifier = Modifier.height(4.dp))
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(ChipShape)
                .background(if (funded) ProgressTrack else Color(0xFFF1F1F1)),
        ) {
            if (funded && progress > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress / 100f)
                        .height(16.dp)
                        .clip(ChipShape)
                        .background(MuyuHeader),
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = progressLabel,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MuyuForest,
            ),
        )
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
