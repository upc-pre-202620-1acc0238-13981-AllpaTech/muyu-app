package com.example.muyu_app.features.plot_financing.presentation.catalog

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.LanguageToggleButton
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.i18n.AppLanguage
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.presentation.myplots.formatPlotArea
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuNearText
import com.example.muyu_app.ui.theme.MuyuSkip
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBlack
import com.example.muyu_app.ui.theme.UrbanistExtraBold
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(100.dp)
private val SearchShape = RoundedCornerShape(100.dp)
private val MutedFooter = Color.Black.copy(alpha = 0.4f)

private enum class CatalogFilter { All, Near, Recommended }

// Loads available plots and applies the merchant search and catalog filters locally.

@Composable
fun MerchantCatalogScreen(
    onPlotClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    var plots by remember { mutableStateOf<List<Plot>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(CatalogFilter.All) }
    val languages = container.languages

    LaunchedEffect(Unit) {
        container.getPlotsUseCase().onSuccess { all ->
            plots = all.filter { it.status == "available" }
        }
    }

    val filteredPlots = remember(searchQuery, selectedFilter, plots) {
        plots.filter { plot ->
            val matchesQuery = searchQuery.isBlank() ||
                    plot.name.contains(searchQuery, ignoreCase = true) ||
                    plot.crop.contains(searchQuery, ignoreCase = true) ||
                    plot.place.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                CatalogFilter.Near -> plot.place.contains("Cusco", ignoreCase = true) ||
                    plot.place.contains("Cuzco", ignoreCase = true) ||
                    plot.place.contains("Lima", ignoreCase = true)
                CatalogFilter.Recommended -> plot.amount.contains("8") ||
                    plot.crop.contains("Papa", ignoreCase = true) ||
                    plot.crop.contains("Potato", ignoreCase = true)
                CatalogFilter.All -> true
            }

            matchesQuery && matchesFilter
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CatalogHeader(
                companyName = stringResource(R.string.merchant_company_name),
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                language = languages.merchant,
                onLanguageToggle = { languages.toggle(isMerchant = true) },
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        text = stringResource(R.string.merchant_filter_all),
                        selected = selectedFilter == CatalogFilter.All,
                        onClick = { selectedFilter = CatalogFilter.All },
                    )
                    FilterChip(
                        text = stringResource(R.string.merchant_filter_near),
                        selected = selectedFilter == CatalogFilter.Near,
                        onClick = { selectedFilter = CatalogFilter.Near },
                    )
                    FilterChip(
                        text = stringResource(R.string.merchant_filter_recommended),
                        selected = selectedFilter == CatalogFilter.Recommended,
                        onClick = { selectedFilter = CatalogFilter.Recommended },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (filteredPlots.isEmpty()) {
                    Text(
                        text = stringResource(R.string.merchant_no_plots),
                        style = TextStyle(
                            fontFamily = UrbanistSemiBold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.5f),
                        ),
                    )
                }
                filteredPlots.forEach { plot ->
                    PlotOfferCard(
                        photo = catalogPhoto(plot),
                        imageUri = plot.imageUri,
                        name = plot.name,
                        price = plot.amount,
                        farmerPhoto = farmerPhoto(plot),
                        farmer = plot.ownerName,
                        crop = plot.crop,
                        area = formatPlotArea(plot.area),
                        place = plot.place,
                        onClick = {
                            container.selectedPlot = plot
                            onPlotClick()
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        MerchantBottomBar(
            selectedTab = MerchantTab.Catalog,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

internal fun catalogPhoto(plot: Plot): Int {
    return if (plot.name.contains("Álamos", ignoreCase = true) ||
        plot.name.contains("Alamos", ignoreCase = true)
    ) {
        R.drawable.merchant_alamos
    } else {
        R.drawable.merchant_esperanza
    }
}

internal fun farmerPhoto(plot: Plot): Int {
    return if (plot.ownerName.contains("Carlos", ignoreCase = true)) {
        R.drawable.merchant_carlos_sm
    } else {
        R.drawable.merchant_maria_sm
    }
}

@Composable
private fun CatalogHeader(
    companyName: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    language: AppLanguage,
    onLanguageToggle: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MuyuNavBar)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.merchant_welcome),
                    style = TextStyle(
                        fontFamily = UrbanistLight,
                        fontWeight = FontWeight.Light,
                        fontSize = 20.sp,
                        color = Color.White,
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = companyName,
                    style = TextStyle(
                        fontFamily = UrbanistBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color.White,
                        lineHeight = 28.sp,
                    ),
                )
            }
            LanguageToggleButton(
                language = language,
                onToggle = onLanguageToggle,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                    fontSize = 14.sp,
                    color = Color.White,
                ),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(SearchShape)
                .background(Color.White)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MuyuAsset(
                fileName = "farmer/search.svg",
                contentDescription = stringResource(R.string.farmer_search_icon),
                width = 20.dp,
                height = 20.dp,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = stringResource(R.string.merchant_search),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.45f),
                        ),
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 16.sp,
                        color = Color.Black,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(ChipShape)
            .background(if (selected) MuyuNavBar else MuyuSkip)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (selected) Color.White else MuyuNearText,
            ),
        )
    }
}

@Composable
internal fun PlotOfferCard(
    @DrawableRes photo: Int,
    imageUri: String? = null,
    name: String,
    price: String,
    @DrawableRes farmerPhoto: Int,
    farmer: String,
    crop: String,
    area: String,
    place: String,
    onClick: (() -> Unit)?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        if (!imageUri.isNullOrBlank()) {
            AsyncImage(
                model = imageUri,
                contentDescription = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp),
                contentScale = ContentScale.Crop,
            )
        } else {
            Image(
                painter = painterResource(photo),
                contentDescription = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp),
                contentScale = ContentScale.Crop,
            )
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = name,
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                    ),
                )
                Text(
                    text = price,
                    style = TextStyle(
                        fontFamily = UrbanistExtraBold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = MuyuForest,
                    ),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(farmerPhoto),
                    contentDescription = farmer,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = farmer,
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black,
                    ),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MuyuApprovedBg)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = listOf(crop, area, place).filter { it.isNotBlank() }.joinToString(" · "),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MuyuForest,
                    ),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.merchant_verified),
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 13.sp,
                        color = MutedFooter,
                    ),
                )
                Text(
                    text = stringResource(R.string.merchant_milestones),
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 13.sp,
                        color = MutedFooter,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun MerchantCatalogScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            MerchantCatalogScreen(onPlotClick = {}, onAlertsClick = {}, onTabSelected = {})
        }
    }
}
