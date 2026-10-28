package com.example.muyu_app.features.labor_execution.presentation.evidence

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.core.image.imageUriToPortableDataUrl
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.core.location.bestLastKnownLocation
import com.example.muyu_app.core.location.hasLocationPermission
import com.example.muyu_app.core.location.requestCurrentLocation
import com.example.muyu_app.core.location.requestCurrentLocationQuick
import com.example.muyu_app.core.plot.liveFundedPlot
import com.example.muyu_app.core.plot.refreshSelectedPlot
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuAddPhoto
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuLocateBg
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import kotlinx.coroutines.launch

private val CardShape = RoundedCornerShape(20.dp)
private val FieldShape = RoundedCornerShape(12.dp)
private val ButtonShape = RoundedCornerShape(24.dp)
private val LightGreenField = Color(0xFFEDF8EE)

private enum class LocationCheck { Idle, Matching, Mismatch, Recorded }

@Composable
fun RecordEvidenceScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    var plot by remember { mutableStateOf(container.liveFundedPlot()) }
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    LaunchedEffect(lifecycleState.value, container.selectedPlot?.id) {
        if (lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) {
            plot = container.liveFundedPlot() ?: plot
        }
    }
    val definition = MilestoneCatalog.definition(container.selectedMilestoneKey)
    val existing = plot?.milestoneStates?.find { it.key == definition.key }
    val online by rememberNetworkOnline(context)
    var photoUri by remember { mutableStateOf(existing?.photoUri.orEmpty()) }
    var inputUsed by remember { mutableStateOf(existing?.inputUsed.orEmpty()) }
    var amount by remember { mutableStateOf(existing?.amountApplied.orEmpty()) }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }
    var latitude by remember { mutableStateOf(existing?.latitude) }
    var longitude by remember { mutableStateOf(existing?.longitude) }
    var accuracy by remember { mutableStateOf(existing?.accuracyMeters) }
    var locationCheck by remember { mutableStateOf(LocationCheck.Idle) }
    var locating by remember { mutableStateOf(false) }
    var queued by remember { mutableStateOf(false) }

    fun applyLocation(lat: Double, lng: Double, acc: Int?) {
        latitude = lat
        longitude = lng
        accuracy = acc
        val currentPlot = plot ?: container.liveFundedPlot()
        locationCheck = when {
            currentPlot == null -> LocationCheck.Recorded
            !online -> LocationCheck.Recorded
            isEvidenceLocationValid(lat, lng, currentPlot, acc) -> LocationCheck.Matching
            else -> LocationCheck.Mismatch
        }
    }

    LaunchedEffect(plot?.id, definition.key, latitude, longitude) {
        if (latitude != null && longitude != null && locationCheck == LocationCheck.Idle) {
            applyLocation(latitude!!, longitude!!, accuracy)
        }
    }

    fun captureGps(quick: Boolean = false) {
        locating = true
        val onDone = { locating = false }
        val onResult: (android.location.Location) -> Unit = { location ->
            applyLocation(
                location.latitude,
                location.longitude,
                location.accuracy.toInt(),
            )
            onDone()
        }
        val onError = {
            val refreshed = container.liveFundedPlot() ?: plot
            plot = refreshed
            val fallback = bestLastKnownLocation(context)
            if (fallback != null) {
                applyLocation(
                    fallback.latitude,
                    fallback.longitude,
                    fallback.accuracy.toInt(),
                )
            } else {
                locationCheck = when {
                    !online -> LocationCheck.Recorded
                    refreshed != null && !plotHasReferenceGps(refreshed) -> LocationCheck.Matching
                    else -> LocationCheck.Mismatch
                }
            }
            onDone()
        }
        if (quick) {
            requestCurrentLocationQuick(context, onResult = onResult, onError = onError)
        } else {
            requestCurrentLocation(context, onResult = onResult, onError = onError)
        }
    }

    val locationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        if (grants.values.any { it }) captureGps()
        else locating = false
    }

    fun afterPhotoCaptured() {
        locating = true
        if (hasLocationPermission(context)) {
            captureGps(quick = false)
        } else {
            locationPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview(),
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            photoUri = saveEvidenceBitmap(context, bitmap)
            afterPhotoCaptured()
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            photoUri = uri.toString()
            afterPhotoCaptured()
        }
    }
    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    val canSave = photoUri.isNotBlank() && inputUsed.isNotBlank() && amount.isNotBlank()
    val hasGpsFix = latitude != null && longitude != null
    val locationReady = !locating && (
        !online ||
            locationCheck == LocationCheck.Matching ||
            locationCheck == LocationCheck.Recorded ||
            hasGpsFix
        )
    val continueEnabled = canSave && locationReady

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.evidence_record_title),
                onBack = onBack,
                trailing = { ConnectionChip(online = online) },
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = MuyuScrollBottomInset),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${plot?.name.orEmpty()}\n${plot?.crop.orEmpty()}",
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 15.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                        ),
                    )
                    Text(
                        text = stringResource(
                            R.string.evidence_season,
                            PlotFinance.seasonYear(plot?.harvestDate.orEmpty()),
                        ),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 15.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuForest, CardShape)
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(definition.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(definition.titleRes),
                                    style = TextStyle(
                                        fontFamily = UrbanistBold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.Black,
                                    ),
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(definition.hintRes),
                                    style = TextStyle(
                                        fontFamily = UrbanistRegular,
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                    ),
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFDE8D7))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "Pending",
                                style = TextStyle(
                                    fontFamily = UrbanistBold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFC85A17),
                                ),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                PhotoBox(
                    photoUri = photoUri,
                    onTakePhoto = {
                        cameraPermission.launch(Manifest.permission.CAMERA)
                    },
                    onOpenGallery = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color(0xFFDCEBFA))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MuyuAsset(
                            fileName = "publish/location.svg",
                            contentDescription = null,
                            width = 20.dp,
                            height = 20.dp,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.evidence_photo_gps),
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontSize = 13.sp,
                                color = Color(0xFF2B5876),
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuForest.copy(alpha = 0.8f), CardShape)
                        .padding(16.dp),
                ) {
                    Column {
                        FieldLabel(stringResource(R.string.evidence_input_used))
                        EvidenceField(
                            value = inputUsed,
                            onValueChange = { inputUsed = it },
                            hint = stringResource(R.string.evidence_input_placeholder),
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        FieldLabel(stringResource(R.string.evidence_amount_applied))
                        EvidenceField(
                            value = amount,
                            onValueChange = { amount = it },
                            hint = stringResource(R.string.evidence_amount_hint),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                FieldLabel(stringResource(R.string.evidence_notes))
                EvidenceField(
                    value = notes,
                    onValueChange = { notes = it },
                    hint = stringResource(R.string.evidence_notes_hint),
                    tall = true,
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (locating) {
                    LocationCard(
                        title = stringResource(R.string.evidence_recheck_loading),
                        body = stringResource(R.string.evidence_location_loading_body),
                        background = MuyuLocateBg,
                        contentColor = MuyuForest,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                when (locationCheck) {
                    LocationCheck.Matching -> LocationCard(
                        title = stringResource(R.string.evidence_location_ok),
                        body = stringResource(
                            R.string.evidence_location_ok_body,
                            accuracy ?: plot?.accuracyMeters ?: 8,
                        ),
                        background = MuyuLocateBg,
                        contentColor = MuyuForest,
                    )
                    LocationCheck.Mismatch -> {
                        LocationCard(
                            title = stringResource(R.string.evidence_location_wrong),
                            body = stringResource(R.string.evidence_location_wrong_body),
                            background = MuyuPendingBg,
                            contentColor = MuyuPendingText,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlineAction(
                            text = if (locating) {
                                stringResource(R.string.evidence_recheck_loading)
                            } else {
                                stringResource(R.string.evidence_recheck)
                            },
                            enabled = !locating,
                        ) {
                            if (hasLocationPermission(context)) {
                                captureGps(quick = false)
                            } else {
                                locationPermission.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                    ),
                                )
                            }
                        }
                    }
                    LocationCheck.Recorded -> LocationCard(
                        title = stringResource(R.string.evidence_location_recorded),
                        body = stringResource(R.string.evidence_offline_banner),
                        background = Color(0xFFFFF4D6),
                        contentColor = Color(0xFF8A6A12),
                    )
                    LocationCheck.Idle -> Unit
                }

                if (queued) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LocationCard(
                        title = stringResource(R.string.evidence_save_queue),
                        body = stringResource(R.string.evidence_offline_banner),
                        background = Color(0xFFFFF4D6),
                        contentColor = Color(0xFF8A6A12),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(CardShape)
                        .background(if (continueEnabled) MuyuAction else MuyuAction.copy(alpha = 0.45f))
                        .clickable(enabled = continueEnabled) {
                            val currentPlot = plot ?: container.liveFundedPlot() ?: return@clickable
                            scope.launch {
                                persistEvidence(
                                    context = context,
                                    container = container,
                                    plot = currentPlot,
                                    key = definition.key,
                                    photoUri = photoUri,
                                    inputUsed = inputUsed,
                                    amount = amount,
                                    notes = notes,
                                    latitude = latitude,
                                    longitude = longitude,
                                    accuracy = accuracy,
                                    online = online,
                                )
                                queued = !online
                                onSaved()
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(
                            if (online) R.string.evidence_continue else R.string.evidence_save_queue,
                        ),
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

private suspend fun persistEvidence(
    context: android.content.Context,
    container: com.example.muyu_app.core.di.AppContainer,
    plot: Plot,
    key: String,
    photoUri: String,
    inputUsed: String,
    amount: String,
    notes: String,
    latitude: Double?,
    longitude: Double?,
    accuracy: Int?,
    online: Boolean,
) {
    val previous = plot.milestoneStates.find { it.key == key } ?: MilestoneState(key = key)
    val submittedAt = System.currentTimeMillis()
    val status = if (online) MilestoneState.STATUS_SUBMITTED else MilestoneState.STATUS_QUEUED
    container.fakeStore.updateMilestone(
        plot.id,
        previous.copy(
            status = status,
            photoUri = imageUriToPortableDataUrl(context, photoUri).orEmpty(),
            inputUsed = inputUsed,
            amountApplied = amount,
            notes = notes,
            recordedAt = PlotFinance.publishedToday(),
            submittedAtMillis = submittedAt,
            latitude = latitude,
            longitude = longitude,
            accuracyMeters = accuracy,
        ),
    )
    container.refreshSelectedPlot()
    if (online) {
        container.selectedPlot?.let { updated ->
            container.syncPlot(updated).onSuccess { remote ->
                container.selectedPlot = remote
            }
        }
    }
}

@Composable
private fun PhotoBox(
    photoUri: String,
    onTakePhoto: () -> Unit,
    onOpenGallery: () -> Unit,
) {
    val context = LocalContext.current
    val dash = Modifier.drawBehind {
        drawRoundRect(
            color = Color(0xFF8FBF7A),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f)),
            ),
            cornerRadius = CornerRadius(20.dp.toPx()),
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(MuyuAddPhoto)
            .then(dash)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (photoUri.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photoUri)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.height(12.dp))
        } else {
            MuyuAsset(
                fileName = "publish/camera.svg",
                contentDescription = null,
                width = 48.dp,
                height = 48.dp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.evidence_add_photo),
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.Black,
                ),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "JPG / PNG   max. 10 MB",
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 13.sp,
                    color = Color.Gray,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(ButtonShape)
                .background(MuyuForest)
                .clickable(onClick = onTakePhoto),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                    text = stringResource(R.string.evidence_take_photo),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White,
                    ),
                )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(ButtonShape)
                .background(Color.White)
                .border(1.dp, MuyuForest, ButtonShape)
                .clickable(onClick = onOpenGallery),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                    text = stringResource(R.string.evidence_open_gallery),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MuyuForest,
                    ),
                )
        }
    }
}

@Composable
private fun EvidenceField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    tall: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (tall) 100.dp else 48.dp)
            .clip(if (tall) CardShape else FieldShape)
            .background(LightGreenField)
            .border(1.dp, MuyuForest.copy(alpha = 0.4f), if (tall) CardShape else FieldShape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 15.sp,
                color = Color.Black,
            ),
            modifier = Modifier.fillMaxSize(),
            decorationBox = { inner ->
                if (value.isBlank()) {
                    Text(
                        text = hint,
                        color = Color.Black.copy(alpha = 0.4f),
                        fontFamily = UrbanistRegular,
                        fontSize = 15.sp,
                    )
                }
                inner()
            },
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 6.dp),
        style = TextStyle(
            fontFamily = UrbanistBold,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.Black,
        ),
    )
}

@Composable
private fun LocationCard(
    title: String,
    body: String,
    background: Color,
    contentColor: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(background)
            .padding(16.dp),
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = contentColor,
            ),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 14.sp,
                color = contentColor.copy(alpha = 0.85f),
            ),
        )
    }
}

@Composable
private fun OutlineAction(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontSize = 16.sp,
                color = MuyuForest,
            ),
        )
    }
}
