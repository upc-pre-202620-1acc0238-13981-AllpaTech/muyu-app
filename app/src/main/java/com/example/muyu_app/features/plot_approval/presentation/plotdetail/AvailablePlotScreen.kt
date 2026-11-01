package com.example.muyu_app.features.plot_approval.presentation.plotdetail

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.designsystem.components.PlotPhoto
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.climate_monitoring.data.OpenMeteoWeatherClient
import com.example.muyu_app.features.climate_monitoring.presentation.alert.resolveWeatherLocation
import com.example.muyu_app.features.plot_approval.domain.PlotVisit
import com.example.muyu_app.features.plot_approval.presentation.myplots.formatPlotArea
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Frame "iPhone 17 - 249" (Mockups - Mobile): parcela publicada sin financiamiento.
private val PageBg = Color(0xFFE9F3EA)
private val CardShape = RoundedCornerShape(20.dp)
private val Muted = Color(0xFF4F5A50)

/**
 * Detalle de seguimiento (Home) de una parcela publicada que aún no tiene
 * financiamiento: muestra el estado de la visita más reciente y su resultado.
 */
@Composable
fun AvailablePlotScreen(
    onBack: () -> Unit,
    onViewVisitOutcome: () -> Unit,
    onManageVisits: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val context = LocalContext.current
    var plot by remember { mutableStateOf(container.selectedPlot) }
    var visits by remember { mutableStateOf<List<PlotVisit>>(emptyList()) }
    var temperature by remember { mutableStateOf<Double?>(null) }
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()

    LaunchedEffect(lifecycleState.value) {
        if (!lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) return@LaunchedEffect
        val id = plot?.id ?: return@LaunchedEffect
        container.getPlotsUseCase().onSuccess { all ->
            all.firstOrNull { it.id == id }?.let {
                plot = it
                container.selectedPlot = it
            }
        }
        container.plotVisits.getVisits(id).onSuccess { visits = it }
    }

    LaunchedEffect(plot?.id) {
        temperature = withContext(Dispatchers.IO) {
            context.resolveWeatherLocation(plot)?.let {
                OpenMeteoWeatherClient.fetch(it.latitude, it.longitude)?.current?.temperature
            }
        }
    }

    val current = plot
    val latest = visits.firstOrNull { !it.isRejected } ?: visits.firstOrNull()
    val lastCompleted = visits.firstOrNull { it.isCompleted }
    val hasOpenVisit = visits.any { it.isRequested || it.isConfirmed }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PageBg),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = current?.name.orEmpty(),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = MuyuScrollBottomInset),
            ) {
                PlotPhoto(
                    imageUri = current?.imageUri,
                    description = current?.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(239.dp),
                )
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(13.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MuyuAsset(
                            fileName = "home/flag_peru.svg",
                            contentDescription = stringResource(R.string.farmer_flag_peru),
                            width = 28.dp,
                            height = 16.dp,
                        )
                        Spacer(Modifier.padding(start = 6.dp))
                        Text(
                            text = stringResource(R.string.farmer_country_peru),
                            style = textStyle(UrbanistRegular, 15, Color.Black),
                            modifier = Modifier.weight(1f),
                        )
                        MuyuAsset(
                            fileName = "home/weather_sun_cloud.png",
                            contentDescription = stringResource(R.string.farmer_weather),
                            width = 34.dp,
                            height = 30.dp,
                        )
                        Spacer(Modifier.padding(start = 8.dp))
                        Text(
                            text = temperature?.let { "${it.toInt()}°C" } ?: "—°C",
                            style = textStyle(UrbanistBold, 16, Color.Black),
                        )
                    }
                    Text(
                        text = stringResource(
                            R.string.available_plot_title,
                            current?.name.orEmpty(),
                            stringResource(current.statusLabel()),
                        ),
                        style = textStyle(UrbanistSemiBold, 21, MuyuForest),
                    )
                    Column {
                        Text(
                            text = listOfNotNull(
                                current?.crop,
                                current?.area?.let { formatPlotArea(it) },
                                current?.place,
                            ).filter { it.isNotBlank() && it != "—" }.joinToString(" · "),
                            style = textStyle(UrbanistRegular, 14, Muted),
                        )
                        if (!current?.ownerName.isNullOrBlank()) {
                            Text(
                                text = stringResource(R.string.available_plot_owner, current?.ownerName.orEmpty()),
                                style = textStyle(UrbanistRegular, 14, Muted),
                            )
                        }
                    }

                    VisitStatusCard(latest = latest, lastCompleted = lastCompleted)

                    if (lastCompleted != null) {
                        PrimaryButton(stringResource(R.string.visit_view_outcome)) {
                            container.selectedVisit = lastCompleted
                            onViewVisitOutcome()
                        }
                    }
                    PrimaryButton(
                        stringResource(
                            if (hasOpenVisit) R.string.available_plot_view_requests else R.string.available_plot_arrange_visit,
                        ),
                        onClick = onManageVisits,
                    )
                    Text(
                        text = stringResource(R.string.available_plot_arrange_hint),
                        style = textStyle(UrbanistRegular, 14, Muted),
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
private fun VisitStatusCard(latest: PlotVisit?, lastCompleted: PlotVisit?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.available_plot_listed),
            style = textStyle(UrbanistSemiBold, 16, MuyuForest),
        )
        Column {
            val lines = when {
                latest == null -> listOf(stringResource(R.string.available_plot_no_visits))
                latest.isRequested -> listOf(
                    stringResource(R.string.available_plot_visit_requested, latest.whenLabel()),
                    stringResource(R.string.available_plot_requested_by, latest.merchantName),
                )
                latest.isConfirmed -> listOf(
                    stringResource(R.string.available_plot_visit_confirmed, latest.whenLabel()),
                    stringResource(R.string.available_plot_requested_by, latest.merchantName),
                )
                latest.isCompleted -> listOfNotNull(
                    stringResource(R.string.available_plot_visit_completed, latest.date),
                    stringResource(
                        if (latest.reachedAgreement) {
                            R.string.available_plot_outcome_agreement
                        } else {
                            R.string.available_plot_outcome_no_agreement
                        },
                    ),
                    latest.agreedPrice.takeIf { latest.reachedAgreement && it.isNotBlank() }
                        ?.let { stringResource(R.string.available_plot_agreed_price, it) },
                )
                else -> listOf(stringResource(R.string.available_plot_visit_declined))
            }
            lines.forEach { Text(text = it, style = textStyle(UrbanistRegular, 16, Color.Black)) }
        }
        Column {
            Text(
                text = stringResource(R.string.available_plot_escrow_pending),
                style = textStyle(UrbanistRegular, 16, Muted),
            )
            Text(
                text = stringResource(
                    if (lastCompleted?.reachedAgreement == true) {
                        R.string.available_plot_financing_awaiting
                    } else {
                        R.string.available_plot_financing_not_started
                    },
                ),
                style = textStyle(UrbanistRegular, 16, Muted),
            )
        }
        Text(
            text = stringResource(R.string.available_plot_agreement_note),
            style = textStyle(UrbanistRegular, 14, Muted),
        )
    }
}

@Composable
private fun PrimaryButton(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = textStyle(UrbanistSemiBold, 18, Color.White).copy(textAlign = TextAlign.Center),
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(MuyuAction)
            .clickable(onClick = onClick)
            .padding(16.dp),
    )
}

private fun PlotVisit.whenLabel(): String =
    listOf(date, time).filter { it.isNotBlank() }.joinToString(", ")

private fun com.example.muyu_app.features.plot_approval.domain.Plot?.statusLabel(): Int =
    when (this?.status?.lowercase()) {
        "approved" -> R.string.farmer_status_approved
        "pending" -> R.string.farmer_status_pending
        else -> R.string.merchant_available
    }

private fun textStyle(family: FontFamily, size: Int, color: Color) = TextStyle(
    fontFamily = family,
    fontSize = size.sp,
    color = color,
)
