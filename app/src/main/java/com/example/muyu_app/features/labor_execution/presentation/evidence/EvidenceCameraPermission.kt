package com.example.muyu_app.features.labor_execution.presentation.evidence

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NoPhotography
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuFinancedBg
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuLocateBg
import com.example.muyu_app.ui.theme.MuyuMutedChip
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

// Medidas y colores del frame "A2 - Permiso de cámara" (Mockups - Mobile).
private val CardShape = RoundedCornerShape(20.dp)
private val ActionShape = RoundedCornerShape(16.dp)
private val ChipShape = RoundedCornerShape(24.dp)
private val CardBorder = Color(0xFFD6E2CF)
private val Muted = Color(0xFF4F5A50)
private val FinancedText = Color(0xFF244398)
private val WarningContainer = Color(0xFFFFF3E0)
private val WarningText = Color(0xFF774308)

/** Resumen de la ubicación capturada para la evidencia, ya traducido. */
data class EvidenceGpsSummary(
    val title: String,
    val detail: String,
    val valid: Boolean,
)

fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null),
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { startActivity(intent) }
}

/**
 * Se muestra cuando el usuario niega el permiso de cámara. El borrador
 * (insumo, cantidad, notas y GPS) sigue en el estado de RecordEvidenceScreen.
 */
@Composable
fun CameraAccessDeniedContent(
    plot: Plot?,
    online: Boolean,
    milestoneTitle: String,
    inputUsed: String,
    amount: String,
    hasPhoto: Boolean,
    gps: EvidenceGpsSummary,
    onRetry: () -> Unit,
    onOpenSettings: () -> Unit,
    onUploadFromGallery: () -> Unit,
    onBackToDraft: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasDraft = inputUsed.isNotBlank() || amount.isNotBlank() || hasPhoto
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        GreenTitleHeader(
            title = stringResource(R.string.evidence_capture_title),
            onBack = onBackToDraft,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            StatusChip(
                text = stringResource(if (online) R.string.evidence_online else R.string.sync_offline),
                icon = Icons.Filled.Check,
                background = if (online) MuyuApprovedBg else MuyuMutedChip,
                contentColor = if (online) MuyuForest else Muted,
            )

            if (plot != null) {
                Column(
                    modifier = Modifier.cardModifier(Color.White),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(text = plot.name, style = textStyle(UrbanistBold, 16, Color.Black))
                    val funded = plot.status.equals("funded", ignoreCase = true)
                    StatusChip(
                        text = stringResource(if (funded) R.string.farmer_status_funded else R.string.farmer_status_pending),
                        icon = Icons.Outlined.VerifiedUser,
                        background = if (funded) MuyuFinancedBg else MuyuPendingBg,
                        contentColor = if (funded) FinancedText else MuyuPendingText,
                    )
                    Text(
                        text = listOf(plot.crop, plot.area, plot.place)
                            .filter { it.isNotBlank() }
                            .joinToString(" · "),
                        style = textStyle(UrbanistRegular, 14, Muted),
                    )
                }
            }

            Column(
                modifier = Modifier.cardModifier(MuyuLocateBg),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.NoPhotography,
                    contentDescription = null,
                    tint = MuyuForest,
                    modifier = Modifier.size(48.dp),
                )
                Text(
                    text = stringResource(R.string.evidence_camera_denied_title),
                    style = textStyle(UrbanistBold, 16, Color.Black),
                )
                Text(
                    text = stringResource(R.string.evidence_camera_denied_body),
                    style = textStyle(UrbanistRegular, 16, Muted),
                )
                Text(
                    text = stringResource(R.string.evidence_camera_retry),
                    style = textStyle(UrbanistSemiBold, 16, MuyuForest).copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ActionShape)
                        .clickable(onClick = onRetry)
                        .padding(vertical = 6.dp),
                )
            }

            if (hasDraft) {
                StatusChip(
                    text = stringResource(R.string.evidence_draft_saved),
                    icon = Icons.Outlined.EditNote,
                    background = MuyuMutedChip,
                    contentColor = Muted,
                )
                val parts = listOf(milestoneTitle, inputUsed, amount).filter { it.isNotBlank() }
                val summary = parts.joinToString(" · ") +
                    if (hasPhoto) "." else ". " + stringResource(R.string.evidence_draft_no_photo)
                Text(text = summary, style = textStyle(UrbanistRegular, 14, Muted))
            }

            Column(
                modifier = Modifier.cardModifier(Color.White),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = if (gps.valid) MuyuForest else MuyuPendingText,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = gps.title,
                        style = textStyle(UrbanistBold, 14, if (gps.valid) MuyuForest else MuyuPendingText),
                    )
                }
                if (gps.detail.isNotBlank()) {
                    Text(text = gps.detail, style = textStyle(UrbanistRegular, 14, Muted))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ActionShape)
                    .background(WarningContainer)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = WarningText,
                    modifier = Modifier.size(22.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.evidence_gallery_hint_title),
                        style = textStyle(UrbanistBold, 16, WarningText),
                    )
                    Text(
                        text = stringResource(R.string.evidence_gallery_hint_body),
                        style = textStyle(UrbanistRegular, 14, WarningText),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MuyuPageGray)
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ActionButton(
                text = stringResource(R.string.evidence_open_settings_camera),
                filled = true,
                onClick = onOpenSettings,
            )
            ActionButton(
                text = stringResource(R.string.evidence_upload_gallery),
                filled = false,
                onClick = onUploadFromGallery,
            )
            Text(
                text = stringResource(R.string.evidence_back_to_draft),
                style = textStyle(UrbanistSemiBold, 16, MuyuForest).copy(textAlign = TextAlign.Center),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ActionShape)
                    .clickable(onClick = onBackToDraft)
                    .padding(vertical = 6.dp),
            )
        }
    }
}

private fun Modifier.cardModifier(background: Color): Modifier = this
    .fillMaxWidth()
    .clip(CardShape)
    .background(background)
    .border(1.dp, CardBorder, CardShape)
    .padding(16.dp)

private fun textStyle(family: FontFamily, size: Int, color: Color) = TextStyle(
    fontFamily = family,
    fontSize = size.sp,
    color = color,
)

@Composable
private fun StatusChip(
    text: String,
    icon: ImageVector,
    background: Color,
    contentColor: Color,
) {
    Row(
        modifier = Modifier
            .clip(ChipShape)
            .background(background)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(14.dp),
        )
        Text(text = text, style = textStyle(UrbanistSemiBold, 12, contentColor))
    }
}

@Composable
private fun ActionButton(text: String, filled: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        style = textStyle(UrbanistBold, 16, if (filled) Color.White else MuyuForest)
            .copy(textAlign = TextAlign.Center),
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .clip(ActionShape)
            .background(if (filled) MuyuAction else Color.White)
            .then(if (filled) Modifier else Modifier.border(1.dp, MuyuForest, ActionShape))
            .clickable(onClick = onClick)
            .padding(vertical = 17.dp),
    )
}
