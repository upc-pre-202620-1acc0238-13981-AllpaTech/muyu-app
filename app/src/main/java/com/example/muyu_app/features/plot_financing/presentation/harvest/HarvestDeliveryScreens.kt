package com.example.muyu_app.features.plot_financing.presentation.harvest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MascotMessage
import com.example.muyu_app.core.designsystem.components.MascotMood
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.features.plot_approval.domain.HarvestIncident
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.features.plot_approval.presentation.myplots.formatPlotArea
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuCheckbox
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuGrayChip
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.MuyuTermsBg
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistMedium
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import kotlinx.coroutines.launch

private val CardShape = RoundedCornerShape(20.dp)
private val ButtonShape = RoundedCornerShape(15.dp)
private val ChipShape = RoundedCornerShape(100.dp)
private val Muted = Color.Black.copy(alpha = 0.55f)
private val ErrorRed = Color(0xFFD83939)
private const val MIN_INCIDENT_DETAIL = 10

/** US30: el comerciante revisa la entrega y confirma que recibió la cosecha. */
@Composable
fun HarvestReceiptScreen(
    onBack: () -> Unit,
    onBackToPlot: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    var plot by remember { mutableStateOf(container.liveSelectedPlot()) }
    // Si ya estaba recibida se muestra el cierre existente en lugar de confirmar otra vez.
    var justConfirmed by rememberSaveable { mutableStateOf(false) }
    var agreed by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    val current = plot

    HarvestScaffold(
        title = stringResource(R.string.harvest_receipt_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        if (current == null) return@HarvestScaffold
        if (current.harvestReceivedAt.isNotBlank()) {
            MascotMessage(
                mood = MascotMood.Success,
                title = stringResource(R.string.harvest_received_title),
                body = stringResource(
                    if (justConfirmed) R.string.harvest_received_body else R.string.harvest_already_received_body,
                    current.ownerName,
                ),
            )
            Spacer(Modifier.height(18.dp))
            DeliveryDataCard(current)
            Spacer(Modifier.height(12.dp))
            InfoCard {
                DataLine(stringResource(R.string.harvest_status_label), stringResource(R.string.harvest_status_received))
                DataLine(stringResource(R.string.harvest_received_at_label), current.harvestReceivedAt)
            }
            Spacer(Modifier.height(18.dp))
            HarvestButton(stringResource(R.string.harvest_back_to_plot), primary = true, onClick = onBackToPlot)
            return@HarvestScaffold
        }

        Text(
            text = stringResource(R.string.harvest_receipt_intro),
            style = TextStyle(fontFamily = UrbanistMedium, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Muted),
        )
        Spacer(Modifier.height(14.dp))
        DeliveryDataCard(current)
        if (current.harvestIncidents.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            IncidentHistoryCard(current.harvestIncidents)
        }
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(MuyuTermsBg)
                .border(1.dp, MuyuForest, CardShape)
                .toggleable(value = agreed, role = Role.Checkbox, onValueChange = { agreed = it })
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (agreed) MuyuOlive else MuyuCheckbox),
                contentAlignment = Alignment.Center,
            ) {
                if (agreed) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.harvest_receipt_agree),
                style = TextStyle(fontFamily = UrbanistMedium, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.Black),
            )
        }
        Spacer(Modifier.height(18.dp))
        HarvestButton(
            label = stringResource(if (saving) R.string.harvest_saving else R.string.harvest_confirm_receipt),
            primary = true,
            enabled = agreed && !saving,
        ) {
            saving = true
            scope.launch {
                val merchant = container.currentUserEmail().orEmpty()
                container.backendStore.confirmHarvestReceived(current.id, merchant)
                plot = container.syncFollowUp(current.id)
                justConfirmed = true
                saving = false
            }
        }
    }
}

/** US31: el comerciante reporta un problema en la entrega y la recepción queda pendiente. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HarvestIncidentScreen(
    onBack: () -> Unit,
    onBackToPlot: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val plot = remember { container.liveSelectedPlot() }
    var reason by rememberSaveable { mutableStateOf(HarvestIncident.REASON_NOT_DELIVERED) }
    var details by rememberSaveable { mutableStateOf("") }
    var showError by rememberSaveable { mutableStateOf(false) }
    var sent by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }

    HarvestScaffold(
        title = stringResource(R.string.harvest_incident_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        if (plot == null) return@HarvestScaffold
        if (sent) {
            MascotMessage(
                mood = MascotMood.Reviewing,
                title = stringResource(R.string.harvest_incident_sent_title),
                body = stringResource(R.string.harvest_incident_sent_body, plot.ownerName),
            )
            Spacer(Modifier.height(18.dp))
            InfoCard {
                DataLine(stringResource(R.string.harvest_incident_reason), stringResource(incidentReasonLabel(reason)))
                DataLine(stringResource(R.string.harvest_incident_details), details.trim())
                DataLine(stringResource(R.string.harvest_status_label), stringResource(R.string.harvest_status_pending))
            }
            Spacer(Modifier.height(18.dp))
            HarvestButton(stringResource(R.string.harvest_back_to_plot), primary = true, onClick = onBackToPlot)
            return@HarvestScaffold
        }

        Text(
            text = stringResource(R.string.harvest_incident_intro, plot.name),
            style = TextStyle(fontFamily = UrbanistMedium, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Muted),
        )
        Spacer(Modifier.height(16.dp))
        SectionLabel(stringResource(R.string.harvest_incident_reason))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HarvestIncident.reasons.forEach { key ->
                val selected = key == reason
                Box(
                    modifier = Modifier
                        .clip(ChipShape)
                        .background(if (selected) MuyuOlive else MuyuGrayChip)
                        .clickable { reason = key }
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                ) {
                    Text(
                        text = stringResource(incidentReasonLabel(key)),
                        style = TextStyle(
                            fontFamily = UrbanistSemiBold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = if (selected) Color.White else Color.Black,
                        ),
                    )
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        SectionLabel(stringResource(R.string.harvest_incident_details))
        val invalid = showError && details.trim().length < MIN_INCIDENT_DETAIL
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(Color.White)
                .border(1.dp, if (invalid) ErrorRed else MuyuForest, CardShape),
        ) {
            if (details.isEmpty()) {
                Text(
                    text = stringResource(R.string.harvest_incident_details_hint),
                    modifier = Modifier.padding(14.dp),
                    style = TextStyle(fontFamily = UrbanistRegular, fontSize = 14.sp, color = Muted),
                )
            }
            // Todo el cuadro recibe el toque para abrir el teclado.
            BasicTextField(
                value = details,
                onValueChange = { details = it.take(400) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .padding(14.dp),
                textStyle = TextStyle(fontFamily = UrbanistRegular, fontSize = 14.sp, lineHeight = 20.sp, color = Color.Black),
                cursorBrush = SolidColor(Color.Black),
            )
        }
        if (invalid) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.harvest_incident_details_error),
                style = TextStyle(fontFamily = UrbanistSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = ErrorRed),
            )
        }
        Spacer(Modifier.height(20.dp))
        HarvestButton(
            label = stringResource(if (saving) R.string.harvest_saving else R.string.harvest_incident_send),
            primary = true,
            enabled = !saving,
        ) {
            // El formulario no se envía sin una descripción del problema.
            if (details.trim().length < MIN_INCIDENT_DETAIL) {
                showError = true
                return@HarvestButton
            }
            saving = true
            scope.launch {
                val now = System.currentTimeMillis()
                container.backendStore.reportHarvestIncident(
                    plot.id,
                    HarvestIncident(
                        reason = reason,
                        details = details.trim(),
                        reportedAt = PlotFinance.timestampNow(),
                        reportedAtMillis = now,
                        reportedBy = container.currentUserEmail().orEmpty(),
                    ),
                )
                container.syncFollowUp(plot.id)
                sent = true
                saving = false
            }
        }
        Spacer(Modifier.height(10.dp))
        HarvestButton(stringResource(R.string.harvest_cancel), primary = false, onClick = onBack)
    }
}

/** Lee la versión guardada de la parcela seleccionada. */
internal fun AppContainer.liveSelectedPlot(): Plot? {
    val id = selectedPlot?.id ?: return selectedPlot
    return backendStore.plotById(id)?.let { backendStore.toDomain(it) } ?: selectedPlot
}

/** Guarda el seguimiento localmente y lo envía al servidor cuando hay conexión. */
internal suspend fun AppContainer.syncFollowUp(plotId: Int): Plot? {
    val local = backendStore.plotById(plotId)?.let { backendStore.toDomain(it) } ?: return null
    selectedPlot = local
    syncPlot(local).onSuccess { selectedPlot = it }
    return selectedPlot
}

fun incidentReasonLabel(reason: String): Int = when (reason) {
    HarvestIncident.REASON_NOT_DELIVERED -> R.string.harvest_reason_not_delivered
    HarvestIncident.REASON_INCOMPLETE -> R.string.harvest_reason_incomplete
    HarvestIncident.REASON_QUALITY -> R.string.harvest_reason_quality
    HarvestIncident.REASON_LATE -> R.string.harvest_reason_late
    else -> R.string.harvest_reason_other
}

@Composable
private fun HarvestScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground)
            .imePadding(),
    ) {
        GreenTitleHeader(title = title, onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            content = content,
        )
    }
}

@Composable
private fun DeliveryDataCard(plot: Plot) {
    val harvest = plot.milestoneStates.find { it.key == MilestoneState.KEY_HARVEST }
    InfoCard {
        Text(
            text = stringResource(R.string.harvest_delivery_data),
            style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MuyuForest),
        )
        Spacer(Modifier.height(10.dp))
        DataLine(stringResource(R.string.harvest_plot_label), plot.name)
        DataLine(
            stringResource(R.string.harvest_crop_label),
            listOf(plot.crop, formatPlotArea(plot.area)).filter { it.isNotBlank() }.joinToString(" · "),
        )
        DataLine(stringResource(R.string.harvest_farmer_label), plot.ownerName)
        DataLine(stringResource(R.string.harvest_date_label), harvest?.recordedAt?.ifBlank { null } ?: plot.harvestDate)
        if (!harvest?.amountApplied.isNullOrBlank()) {
            DataLine(stringResource(R.string.harvest_quantity_label), harvest?.amountApplied.orEmpty())
        }
        if (!harvest?.notes.isNullOrBlank()) {
            DataLine(stringResource(R.string.harvest_notes_label), harvest?.notes.orEmpty())
        }
        DataLine(stringResource(R.string.harvest_amount_label), plot.amount)
    }
}

@Composable
private fun IncidentHistoryCard(incidents: List<HarvestIncident>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(MuyuPendingBg)
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.harvest_incidents_reported, incidents.size),
            style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MuyuPendingText),
        )
        incidents.takeLast(3).forEach { incident ->
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${incident.reportedAt} · ${stringResource(incidentReasonLabel(incident.reason))}",
                style = TextStyle(fontFamily = UrbanistSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MuyuPendingText),
            )
            Text(
                text = incident.details,
                style = TextStyle(fontFamily = UrbanistRegular, fontSize = 13.sp, color = Color.Black),
            )
        }
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(16.dp),
        content = content,
    )
}

@Composable
private fun DataLine(label: String, value: String) {
    if (value.isBlank()) return
    Text(label, style = TextStyle(fontFamily = UrbanistRegular, fontSize = 12.sp, color = Muted))
    Text(value, style = TextStyle(fontFamily = UrbanistSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.Black))
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 8.dp),
        style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MuyuForest),
    )
}

@Composable
internal fun HarvestButton(
    label: String,
    primary: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    // Colores atenuados en lugar de alpha para que el fondo siempre se dibuje.
    val background = when {
        !primary -> Color.White
        enabled -> MuyuOlive
        else -> MuyuTermsBg
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(ButtonShape)
            .background(background)
            .border(1.dp, MuyuForest, ButtonShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = when {
                    !primary -> MuyuForest
                    enabled -> Color.White
                    else -> Muted
                },
            ),
        )
    }
}
