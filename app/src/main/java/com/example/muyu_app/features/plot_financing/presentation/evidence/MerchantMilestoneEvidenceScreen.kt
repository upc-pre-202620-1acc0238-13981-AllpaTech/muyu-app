package com.example.muyu_app.features.plot_financing.presentation.evidence

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextDecoration
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
import com.example.muyu_app.features.plot_approval.domain.milestoneForEvidence
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuLocateBg
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val PhotoCardShape = RoundedCornerShape(18.dp)
private val ChipShape = RoundedCornerShape(12.dp)
private val SectionMuted = Color.Black.copy(alpha = 0.55f)

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
            formatEvidenceShortDate(sentAt, context.resources.configuration.locales[0]),
            formatEvidenceTime(sentAt),
        )
    }
    val releaseAmount = plot?.id?.let { container.fakeStore.milestoneReleaseAmountFor(it, milestoneKey) }.orEmpty()
    val lat = milestone?.latitude
    val lng = milestone?.longitude
    val photoDateLabel = formatEvidenceShortDate(sentAt, context.resources.configuration.locales[0])
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.merchant_milestone_evidence_title),
                onBack = onBack,
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
                        text = stringResource(definition.titleRes),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.Black,
                        ),
                    )
                    StatusChip(
                        label = when (milestone?.status) {
                            MilestoneState.STATUS_APPROVED -> stringResource(R.string.merchant_status_approved)
                            MilestoneState.STATUS_NEEDS_CORRECTION -> stringResource(R.string.plot_needs_correction)
                            else -> stringResource(R.string.merchant_to_review)
                        },
                        approved = milestone?.status == MilestoneState.STATUS_APPROVED,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sentSubtitle,
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 15.sp,
                        color = SectionMuted,
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                milestone?.photoUri?.takeIf { it.isNotBlank() }?.let { photoUri ->
                    EvidencePhotoTile(
                        photoUri = photoUri,
                        title = "${stringResource(definition.titleRes)} · $photoDateLabel",
                        subTitle = photoTimeGps,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }

                SectionHeading(stringResource(R.string.merchant_location_verification))
                Spacer(modifier = Modifier.height(10.dp))
                if (mapLat != null && mapLng != null) {
                    PlotLocationMap(
                        latitude = mapLat,
                        longitude = mapLng,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(186.dp),
                        overlay = {
                            LocationBadge(
                                ok = locationOk,
                                distanceMeters = distance,
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoBanner(
                        title = if (locationOk) {
                            stringResource(R.string.evidence_location_ok)
                        } else {
                            stringResource(R.string.evidence_location_wrong)
                        },
                        body = when {
                            !locationOk -> stringResource(R.string.evidence_location_wrong_body)
                            distance != null -> stringResource(
                                R.string.merchant_location_within_plot,
                                distance,
                            )
                            else -> stringResource(R.string.merchant_location_verified_generic)
                        },
                        ok = locationOk,
                    )
                } else {
                    TextCard(text = stringResource(R.string.merchant_gps_not_recorded))
                }

                Spacer(modifier = Modifier.height(18.dp))

                SectionHeading(stringResource(R.string.merchant_verified_data))
                Spacer(modifier = Modifier.height(10.dp))
                DataCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = formatEvidenceDateTimeLine(
                                sentAt,
                                context.resources.configuration.locales[0],
                            ),
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontSize = 16.sp,
                                color = Color.Black.copy(alpha = 0.7f),
                            ),
                        )
                        if (lat != null && lng != null) {
                            Box(
                                modifier = Modifier
                                    .clip(ChipShape)
                                    .background(Color(0xFFC7E8C8))
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.merchant_location_verified_short),
                                    style = TextStyle(
                                        fontFamily = UrbanistBold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MuyuForest,
                                    ),
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.merchant_weather_not_recorded),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.7f),
                        ),
                    )
                    if (lat != null && lng != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = PlotFinance.formatCoordinates(lat, lng),
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontSize = 16.sp,
                                color = Color.Black.copy(alpha = 0.7f),
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                SectionHeading(stringResource(R.string.merchant_farmers_note))
                Spacer(modifier = Modifier.height(8.dp))
                TextCard(
                    text = milestone?.notes?.takeIf { it.isNotBlank() }?.let { "\"$it\"" } ?: "—",
                )

                Spacer(modifier = Modifier.height(18.dp))

                SectionHeading(stringResource(R.string.merchant_input_used))
                Spacer(modifier = Modifier.height(8.dp))
                DataCard {
                    DataRow(
                        label = stringResource(R.string.merchant_input_label),
                        value = milestone?.inputUsed?.ifBlank { "—" } ?: "—",
                    )
                    DataRow(
                        label = stringResource(R.string.evidence_amount_applied),
                        value = milestone?.amountApplied?.ifBlank { "—" } ?: "—",
                    )
                    if (releaseAmount.isNotBlank()) {
                        DataRow(
                            label = stringResource(R.string.merchant_milestone_pay),
                            value = releaseAmount,
                        )
                    }
                }

                if (farmerFirst.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(CardShape)
                            .background(MuyuAction)
                            .border(1.dp, MuyuForest, CardShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.merchant_whatsapp_contact, farmerFirst),
                            style = TextStyle(
                                fontFamily = UrbanistBold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFFF2F2F2),
                                textDecoration = TextDecoration.Underline,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (canReview) {
                    PrimaryButton(
                        text = stringResource(R.string.merchant_review_approve_reject),
                        enabled = true,
                        onClick = onReview,
                    )
                } else {
                    PrimaryButton(
                        text = when (milestone?.status) {
                            MilestoneState.STATUS_APPROVED -> stringResource(R.string.merchant_evidence_already_approved)
                            else -> stringResource(R.string.merchant_evidence_awaiting_resubmit)
                        },
                        enabled = false,
                        onClick = {},
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
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
    Box(
        modifier = modifier
            .height(200.dp)
            .clip(PhotoCardShape)
            .border(1.dp, MuyuForest, PhotoCardShape)
            .background(Color(0xFF1A1A1A)),
    ) {
        AsyncImage(
            model = photoUri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
        ) {
            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White,
                    ),
                )
                Text(
                    text = subTitle,
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                    ),
                )
            }
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text = text.uppercase(),
        style = TextStyle(
            fontFamily = UrbanistBold,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f),
            letterSpacing = 0.8.sp,
        ),
    )
}

@Composable
private fun StatusChip(label: String, approved: Boolean) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(if (approved) MuyuApprovedBg else Color(0xFFFDE8D7))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (approved) MuyuForest else Color(0xFFC85A17),
            ),
        )
    }
}

@Composable
private fun LocationBadge(ok: Boolean, distanceMeters: Float?) {
    Row(
        modifier = Modifier
            .clip(ChipShape)
            .background(if (ok) MuyuLocateBg else MuyuPendingBg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (ok) {
            MuyuAsset(
                fileName = "publish/check.svg",
                contentDescription = null,
                width = 14.dp,
                height = 14.dp,
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = when {
                !ok -> stringResource(R.string.merchant_location_flagged)
                distanceMeters != null -> stringResource(R.string.merchant_location_within_plot, distanceMeters)
                else -> stringResource(R.string.merchant_location_verified_short)
            },
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontSize = 12.sp,
                color = if (ok) MuyuForest else MuyuPendingText,
            ),
        )
    }
}

@Composable
private fun InfoBanner(title: String, body: String, ok: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(if (ok) MuyuLocateBg else MuyuPendingBg)
            .padding(14.dp),
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (ok) MuyuForest else MuyuPendingText,
            ),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 14.sp,
                color = if (ok) MuyuForest else MuyuPendingText,
            ),
        )
    }
}

@Composable
private fun DataCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        content()
    }
}

@Composable
private fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 15.sp,
                color = SectionMuted,
            ),
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.Black,
            ),
        )
    }
}

@Composable
private fun TextCard(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(16.dp),
        style = TextStyle(
            fontFamily = UrbanistRegular,
            fontSize = 15.sp,
            color = Color.Black.copy(alpha = 0.75f),
            lineHeight = 21.sp,
        ),
    )
}

@Composable
private fun PrimaryButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(CardShape)
            .background(if (enabled) MuyuAction else MuyuAction.copy(alpha = 0.45f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
            ),
        )
    }
}