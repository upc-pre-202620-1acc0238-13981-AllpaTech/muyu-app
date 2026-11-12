package com.example.muyu_app.features.labor_execution.presentation.evidence

import androidx.compose.foundation.background
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
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.features.plot_approval.domain.HarvestDeliveryState
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.harvestDeliveryState
import com.example.muyu_app.features.plot_financing.presentation.harvest.incidentReasonLabel
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular

private val NoticeShape = RoundedCornerShape(20.dp)

/** Avisa al agricultor si el comerciante recibió la cosecha o reportó una incidencia. */
@Composable
fun FarmerHarvestNotice(plot: Plot, modifier: Modifier = Modifier) {
    val state = plot.harvestDeliveryState()
    if (state != HarvestDeliveryState.Received && state != HarvestDeliveryState.WithIncident) return
    val container = LocalAppContainer.current
    val merchant = plot.harvestReceivedBy.ifBlank { plot.fundedByEmail.orEmpty() }
        .let { email -> container.backendStore.ownerName(email).ifBlank { email } }
    val received = state == HarvestDeliveryState.Received
    val title = stringResource(
        if (received) R.string.farmer_harvest_received_title else R.string.farmer_harvest_incident_title,
    )
    val body = if (received) {
        stringResource(R.string.farmer_harvest_received_body, merchant, plot.harvestReceivedAt)
    } else {
        val last = plot.harvestIncidents.last()
        stringResource(
            R.string.farmer_harvest_incident_body,
            stringResource(incidentReasonLabel(last.reason)),
            last.details,
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(NoticeShape)
            .background(if (received) MuyuApprovedBg else MuyuPendingBg)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MuyuMascot(mood = if (received) MascotMood.Success else MascotMood.Sad, size = 64.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (received) MuyuForest else MuyuPendingText,
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = body,
                style = TextStyle(fontFamily = UrbanistRegular, fontSize = 13.sp, color = Color.Black, lineHeight = 18.sp),
            )
        }
    }
}
