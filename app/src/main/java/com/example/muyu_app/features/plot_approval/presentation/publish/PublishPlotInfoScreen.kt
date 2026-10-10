package com.example.muyu_app.features.plot_approval.presentation.publish

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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.muyu_app.ui.theme.MuyuAddPhoto
import com.example.muyu_app.ui.theme.MuyuAddPhotoText
import com.example.muyu_app.ui.theme.MuyuCoverChip
import com.example.muyu_app.ui.theme.MuyuCoverText
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBlack
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private val PhotoShape = RoundedCornerShape(20.dp)
private val ThumbShape = RoundedCornerShape(10.dp)
private val FieldShape = RoundedCornerShape(20.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishPlotInfoScreen(
    onNextLocation: () -> Unit,
    onBack: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val draft = LocalAppContainer.current.plotDraft
    val sessionKey = draft.editingPlotId ?: 0
    var name by remember(sessionKey) { mutableStateOf(draft.name) }
    var crop by remember(sessionKey) { mutableStateOf(draft.crop) }
    var area by remember(sessionKey) { mutableStateOf(draft.area) }
    var harvest by remember(sessionKey) { mutableStateOf(draft.harvestDate) }
    var coverUri by remember(sessionKey) { mutableStateOf(draft.imageUri) }
    var extraUris by remember(sessionKey) { mutableStateOf(listOf<String>()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            if (coverUri == null) {
                coverUri = uri.toString()
            } else {
                extraUris = extraUris + uri.toString()
            }
        }
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
                    .padding(horizontal = 17.dp)
                    .padding(top = 12.dp, bottom = MuyuScrollBottomInset),
            ) {
                PublishStepper(
                    currentStep = 1,
                    section = stringResource(R.string.publish_step_info),
                    stepLabel = stringResource(R.string.publish_step_1),
                )
                Spacer(modifier = Modifier.height(12.dp))
                CoverPhoto(
                    imageUri = coverUri,
                    onPick = {
                        pickImage.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                    onClear = { coverUri = extraUris.firstOrNull().also { extraUris = extraUris.drop(1) } },
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    extraUris.take(2).forEach { uri ->
                        ThumbPhoto(
                            imageUri = uri,
                            onClear = { extraUris = extraUris.filterNot { it == uri } },
                        )
                    }
                    AddPhotoTile(
                        onClick = {
                            pickImage.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                FieldLabel(stringResource(R.string.publish_plot_name))
                PublishField(
                    text = stringResource(R.string.publish_plot_name_hint),
                    value = name,
                    onValueChange = { name = it },
                )
                Spacer(modifier = Modifier.height(10.dp))
                FieldLabel(stringResource(R.string.publish_crop_type))
                PublishField(
                    text = stringResource(R.string.publish_crop_hint),
                    value = crop,
                    onValueChange = { crop = it },
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel(stringResource(R.string.publish_area))
                        PublishField(
                            text = stringResource(R.string.publish_area_value),
                            value = area,
                            onValueChange = { area = it },
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel(stringResource(R.string.publish_harvest))
                        PublishField(
                            text = harvest.ifBlank { stringResource(R.string.publish_harvest_value) },
                            muted = harvest.isBlank(),
                            onClick = { showDatePicker = true },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                PublishNextButton(
                    text = stringResource(R.string.publish_next_location),
                    onClick = {
                        draft.name = name
                        draft.crop = crop
                        draft.area = area
                        draft.harvestDate = harvest
                        draft.imageUri = coverUri
                        onNextLocation()
                    },
                )
            }
        }
        if (showDatePicker) {
            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = harvestDateToPickerMillis(harvest),
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dateState.selectedDateMillis?.let { millis ->
                                harvest = formatHarvestDateFromPicker(millis)
                            }
                            showDatePicker = false
                        },
                    ) { Text(stringResource(R.string.action_ok)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.action_cancel)) }
                },
            ) {
                DatePicker(state = dateState)
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
private fun CoverPhoto(
    imageUri: String?,
    onPick: () -> Unit,
    onClear: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(158.dp)
            .clip(PhotoShape)
            .border(1.dp, MuyuOlive, PhotoShape)
            .clickable(onClick = onPick),
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = stringResource(R.string.publish_cover_image),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Image(
                painter = painterResource(R.drawable.publish_cover),
                contentDescription = stringResource(R.string.publish_cover_image),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 10.dp, top = 11.dp)
                .height(30.dp)
                .clip(ThumbShape)
                .background(MuyuCoverChip)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MuyuAsset(
                fileName = "publish/cover_icon.svg",
                contentDescription = null,
                width = 17.dp,
                height = 16.dp,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.publish_cover),
                style = TextStyle(
                    fontFamily = UrbanistBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = MuyuCoverText,
                ),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clickable(onClick = onClear),
        ) {
            CloseBadge(large = true)
        }
    }
}

@Composable
private fun ThumbPhoto(
    imageUri: String,
    onClear: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(width = 118.dp, height = 94.dp)
            .clip(ThumbShape)
            .border(1.dp, MuyuOlive, ThumbShape),
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = stringResource(R.string.publish_cover_image),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .clickable(onClick = onClear),
        ) {
            CloseBadge(large = false)
        }
    }
}

@Composable
private fun AddPhotoTile(
    onClick: () -> Unit,
) {
    val oliveColor = MuyuOlive
    Column(
        modifier = Modifier
            .size(width = 118.dp, height = 94.dp)
            .clip(ThumbShape)
            .background(MuyuAddPhoto)
            .clickable(onClick = onClick)
            .drawBehind {
                drawRoundRect(
                    color = oliveColor,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
                    ),
                    cornerRadius = CornerRadius(10.dp.toPx()),
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.Center) {
            MuyuAsset(
                fileName = "publish/add_circle.svg",
                contentDescription = null,
                width = 24.dp,
                height = 24.dp,
            )
            MuyuAsset(
                fileName = "publish/add_plus.svg",
                contentDescription = null,
                width = 16.dp,
                height = 16.dp,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.publish_add_photo),
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MuyuAddPhotoText,
            ),
        )
    }
}

@Composable
private fun CloseBadge(
    modifier: Modifier = Modifier,
    large: Boolean,
) {
    Box(
        modifier = modifier.size(if (large) 24.dp else 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        MuyuAsset(
            fileName = if (large) "publish/close_bg.svg" else "publish/close_bg_sm.svg",
            contentDescription = null,
            width = if (large) 24.dp else 12.dp,
            height = if (large) 24.dp else 12.dp,
        )
        MuyuAsset(
            fileName = if (large) "publish/close_x.svg" else "publish/close_x_sm.svg",
            contentDescription = null,
            width = if (large) 14.dp else 8.dp,
            height = if (large) 14.dp else 8.dp,
        )
    }
}

@Composable
internal fun FieldLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        style = TextStyle(
            fontFamily = UrbanistRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Black,
        ),
    )
}

@Composable
internal fun PublishField(
    text: String,
    muted: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    value: String? = null,
    onValueChange: ((String) -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(FieldShape)
            .background(Color.White)
            .border(1.dp, MuyuOlive, FieldShape) // Borde verde oliva exacto
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onValueChange != null) {
            BasicTextField(
                value = value.orEmpty(),
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
                decorationBox = { inner ->
                    if (value.isNullOrBlank()) {
                        Text(
                            text = text,
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = Color.Black.copy(alpha = 0.5f),
                            ),
                        )
                    }
                    inner()
                },
            )
        } else {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = if (muted) Color.Black.copy(alpha = 0.5f) else Color.Black,
                ),
            )
        }
        trailing?.invoke()
    }
}

@Composable
internal fun PublishNextButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .clip(FieldShape)
            .background(MuyuForest)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFFFFEFE),
            ),
        )
    }
}

/** DatePicker returns UTC midnight; format in local calendar to avoid off-by-one day. */
private fun formatHarvestDateFromPicker(selectedUtcMillis: Long): String {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = selectedUtcMillis
    }
    val local = Calendar.getInstance().apply {
        set(Calendar.YEAR, utc.get(Calendar.YEAR))
        set(Calendar.MONTH, utc.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, utc.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(local.time)
}

private fun harvestDateToPickerMillis(harvest: String): Long? {
    if (harvest.isBlank()) return null
    val formats = listOf("d MMM yyyy", "dd MMM yyyy")
    for (pattern in formats) {
        val parsed = runCatching {
            SimpleDateFormat(pattern, Locale.getDefault()).parse(harvest)
        }.getOrNull() ?: continue
        val local = Calendar.getInstance().apply {
            time = parsed
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, local.get(Calendar.YEAR))
            set(Calendar.MONTH, local.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, local.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    return null
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun PublishPlotInfoScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            PublishPlotInfoScreen(onNextLocation = {}, onBack = {}, onTabSelected = {})
        }
    }
}