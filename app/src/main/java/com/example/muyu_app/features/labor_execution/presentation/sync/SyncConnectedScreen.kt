package com.example.muyu_app.features.labor_execution.presentation.sync

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(12.dp)

private val LightGreenBg = Color(0xFFDCF0DD)
private val DarkGreenText = Color(0xFF2E6930)
private val PurpleChipBg = Color(0xFFE8EAF6)
private val PurpleChipText = Color(0xFF283593)
private val BlueProgressBar = Color(0xFF3F51B5)
private val PinkChipBg = Color(0xFFFFEBEE)
private val RedChipText = Color(0xFFD32F2F)
private val GrayChipBg = Color(0xFFE0E0E0)

@Composable
fun SyncConnectedScreen(
    onBack: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    onToggleOffline: () -> Unit = {},
    onSyncNow: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.sync_title),
                onBack = onBack,
                trailing = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(LightGreenBg)
                            .clickable { onToggleOffline() }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.sync_connected),
                            color = DarkGreenText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                Text(
                    text = "El Rosal Plot - Potato   .   2026 cycle",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card explicativa
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(LightGreenBg)
                        .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.sync_pending_proofs),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreenText,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.sync_pending_body),
                            fontSize = 13.sp,
                            color = DarkGreenText.copy(alpha = 0.8f),
                            lineHeight = 18.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Item 1: Esperanza Plot (usa synchronizationImg01)
                SyncUploadingItemCard(
                    plotName = "Esperanza Plot",
                    activity = stringResource(R.string.plot_fertilization),
                    imageRes = R.drawable.synchronizationimg01, // <-- SOLO LA PRIMERA
                    statusText = stringResource(R.string.sync_uploading),
                    statusBg = PurpleChipBg,
                    statusColor = PurpleChipText,
                    locationText = stringResource(R.string.sync_location_recorded),
                    dateText = "30 Sep, 2:15 PM",
                    progress = 0.6f,
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Item 2: Huayco Plot (pon aquí tu segunda imagen cuando la tengas)
                SyncUploadingItemCard(
                    plotName = "Huayco Plot",
                    activity = stringResource(R.string.publish_plan_planting),
                    imageRes = R.drawable.synchronizationimg02, // <-- Cambia por R.drawable.tu_otra_imagen
                    statusText = stringResource(R.string.sync_uploading),
                    statusBg = PurpleChipBg,
                    statusColor = PurpleChipText,
                    locationText = stringResource(R.string.sync_location_recorded),
                    dateText = "29 Sep, 8:12 PM",
                    progress = 0.4f,
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Item 3: Molinos Farm (pon aquí tu tercera imagen)
                SyncUploadingItemCard(
                    plotName = "Molinos Farm",
                    activity = stringResource(R.string.publish_plan_planting),
                    imageRes = R.drawable.synchronizationimg03, // <-- Cambia por R.drawable.tu_tercera_imagen
                    statusText = stringResource(R.string.sync_error_retry),
                    statusBg = PinkChipBg,
                    statusColor = RedChipText,
                    locationText = stringResource(R.string.sync_location_recorded),
                    dateText = "23 Sep, 4:40 PM",
                    progress = 0.0f,
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Item 4: El Rosal Plot
                SyncUploadingItemCard(
                    plotName = "El Rosal Plot",
                    activity = stringResource(R.string.plot_irrigation),
                    imageRes = R.drawable.synchronizationimg04, // <-- Cambia por R.drawable.tu_cuarta_imagen
                    statusText = stringResource(R.string.sync_sent),
                    statusBg = LightGreenBg,
                    statusColor = DarkGreenText,
                    locationText = stringResource(R.string.sync_location_recorded),
                    dateText = "23 Sep, 4:40 PM",
                    progress = 1.0f,
                    isSent = true,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Botón verde interactivo "Sync now"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(CardShape)
                        .background(MuyuForest)
                        .clickable { onSyncNow() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.sync_now),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sección History
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, Color.Gray.copy(alpha = 0.4f), CardShape)
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.sync_history),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.sync_proofs_synced),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                            )
                        }
                        MuyuAsset(
                            fileName = "publish/chevron.svg",
                            contentDescription = stringResource(R.string.sync_expand_history),
                            width = 16.dp,
                            height = 10.dp,
                        )
                    }
                }
            }
        }

        MuyuBottomBar(
            selectedTab = MuyuTab.Sync,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun SyncUploadingItemCard(
    plotName: String,
    activity: String,
    @DrawableRes imageRes: Int?,
    statusText: String,
    statusBg: Color,
    statusColor: Color,
    locationText: String,
    dateText: String,
    progress: Float,
    isSent: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest.copy(alpha = 0.5f), CardShape)
            .padding(14.dp),
    ) {
        Column {
            Text(
                text = "$plotName  ·  $activity",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (imageRes != null) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "$plotName evidence",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.LightGray),
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    StatusChip(text = statusText, bg = statusBg, textColor = statusColor)
                    StatusChip(text = locationText, bg = LightGreenBg, textColor = DarkGreenText)
                    StatusChip(text = dateText, bg = LightGreenBg, textColor = DarkGreenText)
                }
            }

            if (progress > 0f) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isSent) DarkGreenText else BlueProgressBar,
                    trackColor = GrayChipBg,
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    bg: Color,
    textColor: Color,
) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun SyncConnectedScreenPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme {
        SyncConnectedScreen(onBack = {}, onTabSelected = {})
    }
}