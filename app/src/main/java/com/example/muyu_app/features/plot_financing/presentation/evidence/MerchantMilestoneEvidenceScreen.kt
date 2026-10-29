package com.example.muyu_app.features.plot_financing.presentation.evidence

import com.example.muyu_app.core.contact.openFarmerContact
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.core.location.PlotLocationMap
import com.example.muyu_app.core.plot.rememberLiveFundedPlot
import com.example.muyu_app.features.labor_execution.presentation.evidence.PLOT_MATCH_METERS
import com.example.muyu_app.features.labor_execution.presentation.evidence.distanceToPlotMeters
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.features.plot_approval.domain.milestoneForEvidence
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuCoverText
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistExtraBold
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistMedium
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val PhotoShape = RoundedCornerShape(20.dp)
private val MapShape = RoundedCornerShape(16.dp)
private val ChipShape = RoundedCornerShape(100.dp)
private val ButtonBorder = Color(0xFFD1D1D1)
private val CardOutline = Color(0xFFCAC4D0)
private val OnSurface = Color(0xFF1D1B20)
private val OnSurfaceVariant = Color(0xFF49454F)
private val WarningText = Color(0xFF774308)
private val PhotoScrim = Color.Black.copy(alpha = 0.25f)

@Composable
fun MerchantMilestoneEvidenceScreen(
    onBack: () -> Unit,
    onReview: () -> Unit,
    onTabSelected: (MerchantTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    val plot = rememberLiveFundedPlot()
    val milestoneKey = container.selectedMilestoneKey
    val definition = MilestoneCatalog.definition(milestoneKey)
    val milestone = plot?.milestoneForEvidence(milestoneKey)
    val farmerName = plot?.ownerName?.ifBlank { null }.orEmpty()
    val farmerFirst = farmerName.substringBefore(" ").ifBlank { farmerName }
    val merchantEmail = container.fakeStore.currentUser?.email.orEmpty()
    val sentAt = resolveMilestoneSubmittedAt(milestone, plot?.id, container.fakeStore, merchantEmail)
    val locale = context.resources.configuration.locales[0]
    val sentSubtitle = when {
        sentAt <= 0L -> stringResource(R.string.merchant_sent_at_unknown, plot?.name.orEmpty())
        isSameCalendarDay(sentAt) -> stringResource(
            R.string.merchant_sent_today_at,
            plot?.name.orEmpty(),
            formatEvidenceTime(sentAt),
        )
        else -> stringResource(
            R.string.merchant_sent_date_at,
            plot?.name.orEmpty(),
            formatEvidenceShortDate(sentAt, locale),
            formatEvidenceTime(sentAt),
        )
    }
    val lat = milestone?.latitude
    val lng = milestone?.longitude
    val photoDateLabel = formatEvidenceShortDate(sentAt, locale)
    val gpsVerifiedShort = stringResource(R.string.merchant_gps_verified_short)
    val gpsNotRecorded = stringResource(R.string.merchant_gps_not_recorded)
    val photoTimeGps = "${formatEvidenceTime(sentAt)} · ${if (lat != null && lng != null) gpsVerifiedShort else gpsNotRecorded}"
    val mapLat = lat ?: plot?.latitude
    val mapLng = lng ?: plot?.longitude
    val distance = if (plot != null && lat != null && lng != null) {
        distanceToPlotMeters(lat, lng, plot)
    } else {
        null
    }
    val locationOk = distance == null || distance <= PLOT_MATCH_METERS
    val canReview = milestone?.status == MilestoneState.STATUS_SUBMITTED
    val photoUri = milestone?.photoUri?.takeIf { it.isNotBlank() }
    val hasVerifiedLocation = lat != null && lng != null
    val inputUsed = milestone?.inputUsed?.ifBlank { null }
    val amountApplied = milestone?.amountApplied?.ifBlank { null }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.merchant_milestone_evidence_title),
                onBack = onBack,
                backgroundColor = MuyuNavBar,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 21.dp)
                    .padding(top = 24.dp, bottom = MuyuScrollBottomInset),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = stringResource(definition.titleRes),
                            style = TextStyle(
                                fontFamily = UrbanistMedium,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = OnSurface,
                            ),
                        )
                        Text(
                            text = sentSubtitle,
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = OnSurfaceVariant,
                            ),
                        )
                    }
                    StatusChip(
                        label = when (milestone?.status) {
                            MilestoneState.STATUS_APPROVED -> stringResource(R.string.merchant_status_approved)
                            MilestoneState.STATUS_NEEDS_CORRECTION -> stringResource(R.string.plot_needs_correction)
                            else -> stringResource(R.string.merchant_to_review)
                        },
                        approved = milestone?.status == MilestoneState.STATUS_APPROVED,
                    )
                }

                if (photoUri != null) {
                    EvidencePhotoTile(
                        photoUri = photoUri,
                        title = "${stringResource(definition.titleRes)} · $photoDateLabel",
                        subTitle = photoTimeGps,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                EvidenceCard {
                    Text(
                        text = stringResource(R.string.merchant_location_verification).uppercase(),
                        style = sectionTitleStyle(),
                    )
                    if (mapLat != null && mapLng != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(MapShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            PlotLocationMap(
                                latitude = mapLat,
                                longitude = mapLng,
                                modifier = Modifier.fillMaxSize(),
                            )
                            MuyuAsset(
                                fileName = "publish/pin.svg",
                                contentDescription = null,
                                width = 20.dp,
                                height = 20.dp,
                            )
                        }
                        Text(
                            text = when {
                                !locationOk -> stringResource(R.string.evidence_location_wrong_body)
                                distance != null -> stringResource(
                                    R.string.merchant_location_within_plot,
                                    distance,
                                )
                                else -> stringResource(R.string.merchant_location_verified_generic)
                            },
                            style = bodyVariantStyle(),
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.merchant_gps_not_recorded),
                            style = bodyVariantStyle(),
                        )
                    }
                }

                EvidenceCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.merchant_verified_data).uppercase(),
                            style = sectionTitleStyle(),
                        )
                        if (hasVerifiedLocation) {
                            VerifiedChip()
                        }
                    }
                    LabeledValue(
                        label = stringResource(R.string.merchant_verified_time),
                        value = formatEvidenceTimeCommaDate(sentAt, locale),
                    )
                    LabeledValue(
                        label = stringResource(R.string.merchant_verified_weather),
                        value = stringResource(R.string.merchant_weather_not_recorded),
                    )
                    if (hasVerifiedLocation) {
                        LabeledValue(
                            label = stringResource(R.string.merchant_verified_coords),
                            value = PlotFinance.formatCoordinates(lat, lng),
                        )
                    }
                }

                EvidenceCard {
                    Text(
                        text = stringResource(R.string.merchant_farmers_note).uppercase(),
                        style = sectionTitleStyle(),
                    )
                    Text(
                        text = milestone?.notes?.takeIf { it.isNotBlank() }?.let { "\"$it\"" } ?: "—",
                        style = TextStyle(
                            fontFamily = UrbanistLight,
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.5f),
                        ),
                    )
                }

                EvidenceCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.merchant_input_used).uppercase(),
                            style = sectionTitleStyle(),
                        )
                        if (!inputUsed.isNullOrBlank()) {
                            VerifiedChip()
                        }
                    }
                    Text(
                        text = inputUsed ?: "—",
                        style = TextStyle(
                            fontFamily = UrbanistMedium,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = OnSurface,
                        ),
                    )
                    Text(
                        text = amountApplied?.let {
                            stringResource(R.string.merchant_input_applied_line, it)
                        } ?: "—",
                        style = bodyVariantStyle(),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (farmerFirst.isNotBlank()) {
                        PrimaryButton(
                            text = stringResource(R.string.merchant_contact_farmer, farmerFirst),
                            enabled = true,
                            extraBold = false,
                            onClick = { openFarmerContact(context, plot?.ownerEmail.orEmpty()) },
                        )
                    }
                    if (canReview) {
                        PrimaryButton(
                            text = stringResource(R.string.merchant_review_evidence),
                            enabled = true,
                            extraBold = true,
                            onClick = onReview,
                        )
                    } else {
                        PrimaryButton(
                            text = when (milestone?.status) {
                                MilestoneState.STATUS_APPROVED -> stringResource(R.string.merchant_evidence_already_approved)
                                else -> stringResource(R.string.merchant_evidence_awaiting_resubmit)
                            },
                            enabled = false,
                            extraBold = true,
                            onClick = {},
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        MerchantBottomBar(
            selectedTab = container.merchantEvidenceBottomTab(),
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun EvidencePhotoTile(
    photoUri: String,
    title: String,
    subTitle: String,
    modifier: Modifier = Modifier,
) {
    // Permite revisar la fotografía completa sin recortarla en la miniatura.
    var expanded by rememberSaveable(photoUri) { mutableStateOf(false) }
    if (expanded) {
        Dialog(onDismissRequest = { expanded = false }) {
            Column(
                modifier = Modifier.fillMaxWidth().background(Color.Black).padding(16.dp),
            ) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = title,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp, max = 480.dp),
                    contentScale = ContentScale.Fit,
                )
                TextButton(onClick = { expanded = false }) {
                    Text(stringResource(R.string.muyu_close_photo), color = Color.White)
                }
            }
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .clip(PhotoShape)
                .clickable { expanded = true },
        ) {
            AsyncImage(
                model = photoUri,
                // Describe el hito y los datos de la fotografía para el lector de pantalla.
                contentDescription = "$title · $subTitle",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PhotoScrim)
                    .padding(12.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = UrbanistMedium,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.White,
                    ),
                )
            }
        }
        Text(
            text = subTitle,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = OnSurfaceVariant,
            ),
        )
    }
}

@Composable
private fun EvidenceCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, CardOutline, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = { content() },
    )
}

@Composable
private fun StatusChip(label: String, approved: Boolean) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(if (approved) MuyuApprovedBg else MuyuPendingBg)
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = if (approved) MuyuCoverText else WarningText,
            ),
        )
    }
}

@Composable
private fun VerifiedChip() {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(MuyuApprovedBg)
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = stringResource(R.string.merchant_location_verified_short),
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = MuyuCoverText,
            ),
        )
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = OnSurfaceVariant,
            ),
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = OnSurface,
            ),
        )
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    enabled: Boolean,
    extraBold: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .clip(CardShape)
            .background(if (enabled) MuyuAction else MuyuAction.copy(alpha = 0.45f))
            .border(1.dp, ButtonBorder, CardShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = if (extraBold) UrbanistExtraBold else UrbanistBold,
                fontWeight = if (extraBold) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFFEFEFE),
            ),
        )
    }
}

private fun sectionTitleStyle() = TextStyle(
    fontFamily = UrbanistSemiBold,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    color = Color.Black,
)

private fun bodyVariantStyle() = TextStyle(
    fontFamily = UrbanistRegular,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    color = OnSurfaceVariant,
)
