package com.example.muyu_app.features.plot_financing.presentation.visit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuDatePickerField
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.di.MerchantVisitRequest
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuLocateBg
import com.example.muyu_app.ui.theme.MuyuSkip
import com.example.muyu_app.ui.theme.MuyuTermsBg
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val VisitCardShape = RoundedCornerShape(16.dp)
private val VisitFieldShape = RoundedCornerShape(12.dp)

// Owns the merchant visit proposal and status views without coupling them to financing state.

@Composable
fun MerchantVisitRequestScreen(
    onBack: () -> Unit,
    onRequestSent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val plot = container.selectedPlot
    var date by remember(plot?.id) { mutableStateOf(LocalDate.now().plusDays(7)) }
    var time by rememberSaveable(plot?.id) { mutableStateOf("10:00") }
    var message by rememberSaveable(plot?.id) { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuLocateBg),
    ) {
        GreenTitleHeader(
            title = stringResource(R.string.merchant_visit_request_title),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            VisitCard {
                Text(
                    text = plot?.name.orEmpty(),
                    style = visitTextStyle(18.sp, FontWeight.SemiBold),
                )
                Text(
                    text = listOf(plot?.place.orEmpty(), plot?.ownerName.orEmpty())
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    style = visitTextStyle(13.sp, FontWeight.Normal, Color.Black.copy(alpha = .55f)),
                )
            }
            Spacer(Modifier.height(14.dp))
            VisitCard {
                MuyuDatePickerField(
                    label = stringResource(R.string.merchant_visit_proposed_date),
                    selectedDate = date,
                    onDateSelected = { date = it },
                    minDate = LocalDate.now(),
                    containerColor = MuyuSkip,
                    labelColor = Color.Black,
                )
                Spacer(Modifier.height(16.dp))
                VisitLabel(R.string.merchant_visit_preferred_time)
                VisitTimeSelector(value = time, onValueChange = { time = it })
                Spacer(Modifier.height(16.dp))
                VisitLabel(R.string.merchant_visit_message)
                VisitInput(
                    value = message,
                    onValueChange = { message = it },
                    minHeight = 86.dp,
                    hint = stringResource(R.string.merchant_visit_message_hint),
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.merchant_visit_disclaimer),
                style = visitTextStyle(13.sp, FontWeight.Normal, Color.Black.copy(alpha = .65f)),
            )
            Spacer(Modifier.height(18.dp))
            VisitPrimaryButton(
                label = stringResource(R.string.merchant_visit_send),
                onClick = {
                    if (time.isBlank() || plot == null) return@VisitPrimaryButton
                    container.merchantVisitRequest = MerchantVisitRequest(
                        plotId = plot.id,
                        plotName = plot.name,
                        plotPlace = plot.place,
                        farmerEmail = plot.ownerEmail,
                        farmerName = plot.ownerName,
                        merchantName = container.fakeStore.currentUser?.displayName.orEmpty(),
                        date = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        time = time.trim(),
                        message = message.trim(),
                    )
                    onRequestSent()
                },
            )
        }
    }
}

@Composable
fun MerchantVisitSentScreen(
    onBack: () -> Unit,
    onBackToPlot: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val plot = container.selectedPlot
    val request = container.merchantVisitRequest
    val confirmed = request?.confirmed == true

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuLocateBg),
    ) {
        GreenTitleHeader(
            title = stringResource(R.string.merchant_visit_status_title),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .height(58.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(.14f)
                        .clip(CircleShape)
                        .background(MuyuSkip)
                        .border(1.dp, MuyuAction, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✓", style = visitTextStyle(24.sp, FontWeight.Bold, MuyuForest))
                }
            }
            Text(
                text = stringResource(if (confirmed) R.string.merchant_visit_confirmed else R.string.merchant_visit_sent),
                style = visitTextStyle(25.sp, FontWeight.Bold, MuyuForest),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (confirmed) {
                    stringResource(R.string.merchant_visit_confirmed_body, request?.farmerName.orEmpty())
                } else {
                    stringResource(R.string.merchant_visit_sent_body, request?.farmerName.orEmpty())
                },
                textAlign = TextAlign.Center,
                style = visitTextStyle(14.sp, FontWeight.Normal, Color.Black.copy(alpha = .55f)),
            )
            Spacer(Modifier.height(16.dp))
            VisitCard {
                Text(
                    text = stringResource(R.string.merchant_visit_to_plot, request?.plotName ?: plot?.name.orEmpty()),
                    style = visitTextStyle(17.sp, FontWeight.SemiBold),
                )
                Spacer(Modifier.height(12.dp))
                VisitSummaryRow(stringResource(R.string.merchant_visit_date), request?.date.orEmpty())
                VisitSummaryRow(stringResource(R.string.merchant_visit_time), request?.time.orEmpty())
                VisitSummaryRow(stringResource(R.string.merchant_visit_place), request?.plotPlace ?: plot?.place.orEmpty())
                if (!request?.message.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(request?.message.orEmpty(), style = visitTextStyle(13.sp, FontWeight.Normal))
                }
            }
            if (confirmed) {
                Spacer(Modifier.height(12.dp))
                VisitCard {
                    Text(
                        text = stringResource(R.string.merchant_visit_meeting_point),
                        style = visitTextStyle(16.sp, FontWeight.Bold, MuyuForest),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = request?.plotPlace.orEmpty(),
                        style = visitTextStyle(14.sp, FontWeight.Normal),
                    )
                    Text(
                        text = stringResource(R.string.merchant_visit_meeting_hint),
                        style = visitTextStyle(12.sp, FontWeight.Normal, Color.Gray),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VisitCardShape)
                    .background(MuyuTermsBg)
                    .padding(14.dp),
            ) {
                Text(
                    text = stringResource(R.string.merchant_visit_coordination_only),
                    style = visitTextStyle(13.sp, FontWeight.Normal, Color(0xFF7A4B00)),
                )
            }
            Spacer(Modifier.height(20.dp))
            VisitPrimaryButton(
                label = stringResource(R.string.merchant_visit_back_plot),
                onClick = onBackToPlot,
            )
        }
    }
}

@Composable
private fun VisitCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VisitCardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, VisitCardShape)
            .padding(16.dp),
        content = content,
    )
}

@Composable
private fun VisitLabel(label: Int) {
    Text(stringResource(label), style = visitTextStyle(13.sp, FontWeight.Bold))
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun VisitInput(
    value: String,
    onValueChange: (String) -> Unit,
    minHeight: androidx.compose.ui.unit.Dp = 48.dp,
    hint: String = "",
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = visitTextStyle(15.sp, FontWeight.Normal),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(minHeight)
                    .clip(VisitFieldShape)
                    .background(MuyuSkip)
                    .border(1.dp, MuyuAction, VisitFieldShape)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
            ) {
                if (value.isBlank() && hint.isNotBlank()) {
                    Text(hint, style = visitTextStyle(14.sp, FontWeight.Normal, Color.Gray))
                }
                inner()
            }
        },
    )
}

@Composable
private fun VisitTimeSelector(value: String, onValueChange: (String) -> Unit) {
    val options = listOf("08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(VisitFieldShape)
            .background(MuyuSkip)
            .border(1.dp, MuyuAction, VisitFieldShape)
            .clickable {
                val next = (options.indexOf(value).takeIf { it >= 0 } ?: 0) + 1
                onValueChange(options[next % options.size])
            }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text("$value  ▾", style = visitTextStyle(15.sp, FontWeight.Normal))
    }
}

@Composable
private fun VisitSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = visitTextStyle(13.sp, FontWeight.Normal, Color.Gray))
        Text(value, style = visitTextStyle(13.sp, FontWeight.SemiBold))
    }
}

@Composable
private fun VisitPrimaryButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(VisitCardShape)
            .background(MuyuAction)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = visitTextStyle(16.sp, FontWeight.Bold, Color.White))
    }
}

private fun visitTextStyle(
    size: androidx.compose.ui.unit.TextUnit,
    weight: FontWeight,
    color: Color = Color.Black,
) = TextStyle(
    fontFamily = when (weight) {
        FontWeight.Bold -> UrbanistBold
        FontWeight.SemiBold -> UrbanistSemiBold
        else -> UrbanistRegular
    },
    fontWeight = weight,
    fontSize = size,
    color = color,
)
