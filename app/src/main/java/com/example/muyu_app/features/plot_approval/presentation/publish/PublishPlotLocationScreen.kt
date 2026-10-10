package com.example.muyu_app.features.plot_approval.presentation.publish

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.muyu_app.core.location.PlotLocationMap
import com.example.muyu_app.core.location.hasLocationPermission
import com.example.muyu_app.core.location.requestCurrentLocation
import com.example.muyu_app.core.location.reverseGeocode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.designsystem.components.PublishStepper
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuLocateBg
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

private val CardShape = RoundedCornerShape(20.dp)
private val FieldShape = RoundedCornerShape(16.dp)

@Composable
fun PublishPlotLocationScreen(
    onNextFinancing: () -> Unit,
    onBack: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val draft = LocalAppContainer.current.plotDraft
    val scope = rememberCoroutineScope()
    val sessionKey = draft.editingPlotId ?: 0
    var region by remember(sessionKey) {
        mutableStateOf(
            draft.regionName.ifBlank { draft.place.substringAfterLast(", ").ifBlank { "Amazonas" } },
        )
    }
    var province by remember(sessionKey) {
        mutableStateOf(draft.provinceName.ifBlank { "Chachapoyas" })
    }
    var district by remember(sessionKey) {
        mutableStateOf(draft.districtName.ifBlank { "Chachapoyas" })
    }
    var address by remember(sessionKey) { mutableStateOf(draft.addressLine) }
    var provinceMenu by remember { mutableStateOf(false) }
    var districtMenu by remember { mutableStateOf(false) }
    var latitude by remember(sessionKey) { mutableStateOf(draft.latitude) }
    var longitude by remember(sessionKey) { mutableStateOf(draft.longitude) }
    var accuracy by remember(sessionKey) { mutableStateOf(draft.accuracyMeters) }
    var locating by remember { mutableStateOf(false) }
    val provinces = listOf("Chachapoyas", "Bagua", "Utcubamba", "Cuzco", "Cusco", "Quispicanchi")

    fun applyLocation(lat: Double, lng: Double, meters: Int?) {
        latitude = lat
        longitude = lng
        accuracy = meters
        draft.latitude = lat
        draft.longitude = lng
        draft.accuracyMeters = meters
        scope.launch {
            val place = withContext(Dispatchers.IO) { reverseGeocode(context, lat, lng) }
            if (place != null) {
                if (place.region.isNotBlank()) region = place.region
                if (place.province.isNotBlank()) province = place.province
                if (place.district.isNotBlank()) district = place.district
                if (place.address.isNotBlank()) address = place.address
            }
            locating = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { granted ->
        if (granted.values.any { it }) {
            requestCurrentLocation(
                context = context,
                onResult = { location ->
                    applyLocation(
                        location.latitude,
                        location.longitude,
                        location.accuracy.roundToInt(),
                    )
                },
                onError = { locating = false },
            )
        } else {
            locating = false
        }
    }

    fun locate() {
        locating = true
        scope.launch {
            kotlinx.coroutines.delay(12_000)
            if (locating) locating = false
        }
        if (!hasLocationPermission(context)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
            return
        }
        requestCurrentLocation(
            context = context,
            onResult = { location ->
                applyLocation(
                    location.latitude,
                    location.longitude,
                    location.accuracy.roundToInt(),
                )
            },
            onError = { locating = false },
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = publishFlowTitle(draft),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 12.dp, bottom = MuyuScrollBottomInset),
            ) {
                PublishStepper(
                    currentStep = 2,
                    section = stringResource(R.string.publish_step_location),
                    stepLabel = stringResource(R.string.publish_step_2),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(CardShape)
                        .background(MuyuLocateBg)
                        .border(1.dp, MuyuOlive, CardShape)
                        .clickable(onClick = { locate() })
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    MuyuAsset(
                        fileName = "publish/locate.svg",
                        contentDescription = null,
                        width = 23.dp,
                        height = 23.dp,
                    )
                    Spacer(modifier = Modifier.padding(start = 10.dp))
                    Text(
                        text = stringResource(
                            if (locating) R.string.publish_locating else R.string.publish_use_location,
                        ),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MuyuNavBar,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                PlotLocationMap(
                    latitude = latitude,
                    longitude = longitude,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(186.dp),
                    overlay = {
                        val lat = latitude
                        val lng = longitude
                        if (lat != null && lng != null) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                MuyuAsset(
                                    fileName = "publish/pin.svg",
                                    contentDescription = null,
                                    width = 14.dp,
                                    height = 17.dp,
                                )
                                Spacer(modifier = Modifier.padding(start = 6.dp))
                                Text(
                                    text = PlotFinance.formatCoordinates(lat, lng),
                                    style = TextStyle(
                                        fontFamily = UrbanistSemiBold,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = Color.Black,
                                    ),
                                )
                            }
                        }
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                FieldLabelText(stringResource(R.string.publish_region))
                LocationCustomField(
                    value = region,
                    onValueChange = { region = it },
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabelText(stringResource(R.string.publish_province))
                        Box {
                            LocationCustomField(
                                displayText = province,
                                onClick = { provinceMenu = true },
                            )
                            DropdownMenu(
                                expanded = provinceMenu,
                                onDismissRequest = { provinceMenu = false },
                                modifier = Modifier
                                    .background(Color.White)
                                    .border(1.dp, MuyuOlive.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            ) {
                                provinces.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option,
                                                style = TextStyle(
                                                    fontFamily = UrbanistRegular,
                                                    fontSize = 15.sp,
                                                    color = Color.Black,
                                                ),
                                            )
                                        },
                                        onClick = {
                                            province = option
                                            provinceMenu = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabelText(stringResource(R.string.publish_district))
                        Box {
                            LocationCustomField(
                                displayText = district,
                                onClick = { districtMenu = true },
                            )
                            DropdownMenu(
                                expanded = districtMenu,
                                onDismissRequest = { districtMenu = false },
                                modifier = Modifier
                                    .background(Color.White)
                                    .border(1.dp, MuyuOlive.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            ) {
                                provinces.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option,
                                                style = TextStyle(
                                                    fontFamily = UrbanistRegular,
                                                    fontSize = 15.sp,
                                                    color = Color.Black,
                                                ),
                                            )
                                        },
                                        onClick = {
                                            district = option
                                            districtMenu = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                FieldLabelText(stringResource(R.string.publish_address))
                LocationCustomField(
                    placeholderText = stringResource(R.string.publish_address_hint),
                    value = address,
                    onValueChange = { address = it },
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Botón de Siguiente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(CardShape)
                        .background(MuyuForest)
                        .clickable {
                            draft.place = listOf(address, district, province, region)
                                .filter { it.isNotBlank() }
                                .joinToString(", ")
                            onNextFinancing()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.publish_next_financing),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White,
                        ),
                    )
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
private fun FieldLabelText(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = UrbanistSemiBold,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = Color.Black,
        ),
    )
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun LocationCustomField(
    displayText: String = "",
    value: String? = null,
    placeholderText: String = "",
    onValueChange: ((String) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(FieldShape)
            .background(Color.White)
            .border(1.dp, MuyuOlive, FieldShape) // Borde verde oliva exacto
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (onValueChange != null && value != null) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 15.sp,
                    color = Color.Black,
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty() && placeholderText.isNotEmpty()) {
                        Text(
                            text = placeholderText,
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontSize = 15.sp,
                                color = Color.Gray,
                            ),
                        )
                    }
                    innerTextField()
                },
            )
        } else {
            Text(
                text = displayText,
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 15.sp,
                    color = Color.Black,
                ),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun PublishPlotLocationScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            PublishPlotLocationScreen(onNextFinancing = {}, onBack = {}, onTabSelected = {})
        }
    }
}