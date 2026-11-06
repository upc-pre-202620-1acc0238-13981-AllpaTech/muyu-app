package com.example.muyu_app.features.plot_financing.presentation.harvest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MascotMood
import com.example.muyu_app.core.designsystem.components.MuyuMascot
import com.example.muyu_app.features.plot_approval.domain.HarvestDeliveryState
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.harvestDeliveryState
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(100.dp)

/** Estado de la entrega de la cosecha en el detalle de la parcela del comerciante. */
@Composable
fun HarvestDeliveryCard(
    plot: Plot,
    onConfirm: () -> Unit,
    onReportIncident: () -> Unit,
) {
    val state = plot.harvestDeliveryState()
    if (state == HarvestDeliveryState.NotReady) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.harvest_delivery_title),
                    style = TextStyle(fontFamily = UrbanistBold, fontWeight = FontWeight.Bold, fontSize = 19.sp, color = MuyuForest),
                )
                Spacer(Modifier.height(6.dp))
                StateChip(state)
            }
            MuyuMascot(
                mood = when (state) {
                    HarvestDeliveryState.Received -> MascotMood.Success
                    HarvestDeliveryState.WithIncident -> MascotMood.Sad
                    else -> MascotMood.Reviewing
                },
                size = 72.dp,
            )
        }
        Spacer(Modifier.height(10.dp))
        val body = when (state) {
            HarvestDeliveryState.Received ->
                stringResource(R.string.harvest_card_received_body, plot.harvestReceivedAt)
            HarvestDeliveryState.WithIncident -> {
                val last = plot.harvestIncidents.last()
                stringResource(
                    R.string.harvest_card_incident_body,
                    stringResource(incidentReasonLabel(last.reason)),
                    last.reportedAt,
                )
            }
            else -> stringResource(R.string.harvest_card_pending_body)
        }
        Text(
            text = body,
            style = TextStyle(fontFamily = UrbanistRegular, fontSize = 14.sp, color = Color.Black.copy(alpha = 0.7f), lineHeight = 20.sp),
        )
        Spacer(Modifier.height(14.dp))
        if (state == HarvestDeliveryState.Received) {
            HarvestButton(stringResource(R.string.harvest_view_closing), primary = false, onClick = onConfirm)
        } else {
            HarvestButton(stringResource(R.string.harvest_confirm_receipt), primary = true, onClick = onConfirm)
            Spacer(Modifier.height(10.dp))
            HarvestButton(stringResource(R.string.harvest_report_incident), primary = false, onClick = onReportIncident)
        }
    }
}

@Composable
private fun StateChip(state: HarvestDeliveryState) {
    val (label, bg, fg) = when (state) {
        HarvestDeliveryState.Received -> Triple(R.string.harvest_status_received, MuyuApprovedBg, MuyuForest)
        HarvestDeliveryState.WithIncident -> Triple(R.string.harvest_status_incident, MuyuPendingBg, MuyuPendingText)
        else -> Triple(R.string.harvest_status_pending, MuyuPendingBg, MuyuPendingText)
    }
    Row {
        Text(
            text = stringResource(label),
            modifier = Modifier
                .clip(ChipShape)
                .background(bg)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            style = TextStyle(fontFamily = UrbanistSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = fg),
        )
        Spacer(Modifier.width(4.dp))
    }
}
