package com.example.muyu_app.features.plot_approval.presentation.visits

import com.example.muyu_app.ui.theme.MuyuBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.features.plot_approval.domain.PlotVisit
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuSoftGreen
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import kotlinx.coroutines.launch

private val CardShape = RoundedCornerShape(20.dp)
private val ButtonShape = RoundedCornerShape(14.dp)

// Visits are loaded from the backend: the farmer responds here to what the
// comerciante solicitó desde su propio teléfono.
@Composable
fun VisitRequestsScreen(
    onBack: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    onRecordOutcome: () -> Unit = {},
    selectedTab: MuyuTab = MuyuTab.Plot,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val plot = container.selectedPlot
    val scope = rememberCoroutineScope()
    var visits by remember { mutableStateOf<List<PlotVisit>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var updatingId by remember { mutableIntStateOf(0) }
    var reload by remember { mutableIntStateOf(0) }

    LaunchedEffect(plot?.id, reload) {
        val plotId = plot?.id ?: return@LaunchedEffect
        loading = true
        container.plotVisits.getVisits(plotId)
            .onSuccess {
                visits = it
                error = null
            }
            .onFailure { error = it.message.orEmpty() }
        loading = false
    }

    fun updateStatus(visit: PlotVisit, status: String) {
        updatingId = visit.id
        scope.launch {
            container.plotVisits.updateStatus(visit.id, status)
                .onSuccess { updated -> visits = visits.map { if (it.id == updated.id) updated else it } }
                .onFailure { error = it.message.orEmpty() }
            updatingId = 0
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.visit_requests_title),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = plot?.name.orEmpty(),
                    fontFamily = UrbanistSemiBold,
                    fontSize = 18.sp,
                    color = MuyuForest,
                )
                when {
                    loading && visits.isEmpty() -> Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MuyuAction)
                    }
                    error != null && visits.isEmpty() -> InfoCard(
                        title = stringResource(R.string.visit_requests_error_title),
                        body = error.orEmpty(),
                        actionLabel = stringResource(R.string.visit_requests_retry),
                        onAction = { reload++ },
                    )
                    visits.isEmpty() -> InfoCard(
                        title = stringResource(R.string.visit_requests_empty_title),
                        body = stringResource(R.string.visit_requests_empty_body),
                    )
                    else -> visits.forEach { visit ->
                        VisitCard(
                            visit = visit,
                            busy = updatingId == visit.id,
                            onAccept = { updateStatus(visit, PlotVisit.STATUS_CONFIRMED) },
                            onDecline = { updateStatus(visit, PlotVisit.STATUS_REJECTED) },
                            onRecordOutcome = {
                                container.selectedVisit = visit
                                onRecordOutcome()
                            },
                        )
                    }
                }
            }
        }
        MuyuBottomBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun VisitCard(
    visit: PlotVisit,
    busy: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onRecordOutcome: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(visit.statusLabel()),
            fontFamily = UrbanistSemiBold,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = MuyuForest,
        )
        VisitRequestLine(stringResource(R.string.merchant_funder), visit.merchantName)
        VisitRequestLine(stringResource(R.string.merchant_visit_date), visit.date)
        VisitRequestLine(stringResource(R.string.merchant_visit_time), visit.time)
        VisitRequestLine(stringResource(R.string.merchant_visit_place), visit.plotPlace)
        if (visit.message.isNotBlank()) {
            Text(visit.message, fontFamily = UrbanistRegular, fontSize = 14.sp)
        }
        if (visit.isCompleted && visit.reachedAgreement && visit.agreedPrice.isNotBlank()) {
            VisitRequestLine(stringResource(R.string.visit_agreed_price), "S/ ${visit.agreedPrice}")
        }
        when {
            visit.isRequested -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(
                    label = stringResource(R.string.visit_request_decline),
                    filled = false,
                    enabled = !busy,
                    onClick = onDecline,
                    modifier = Modifier.weight(1f),
                )
                ActionButton(
                    label = stringResource(R.string.visit_request_accept),
                    filled = true,
                    enabled = !busy,
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                )
            }
            visit.isConfirmed || visit.isCompleted -> ActionButton(
                label = stringResource(
                    if (visit.isCompleted) R.string.visit_view_outcome else R.string.visit_record_outcome,
                ),
                filled = true,
                enabled = !busy,
                onClick = onRecordOutcome,
                modifier = Modifier.fillMaxWidth(),
            )
            else -> Unit
        }
    }
}

private fun PlotVisit.statusLabel(): Int = when {
    isCompleted && reachedAgreement -> R.string.visit_status_agreement
    isCompleted -> R.string.visit_status_no_agreement
    isConfirmed -> R.string.visit_request_confirmed
    isRejected -> R.string.visit_status_declined
    else -> R.string.visit_request_pending
}

@Composable
private fun InfoCard(
    title: String,
    body: String,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuSoftGreen, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, fontFamily = UrbanistSemiBold, fontSize = 16.sp, color = MuyuForest)
        Text(body, fontFamily = UrbanistRegular, fontSize = 14.sp, color = Color(0xFF555555))
        if (actionLabel != null) {
            ActionButton(
                label = actionLabel,
                filled = false,
                enabled = true,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    filled: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(ButtonShape)
            .background(if (filled) MuyuForest else Color.White)
            .border(1.dp, MuyuForest, ButtonShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (filled) Color.White else MuyuForest,
            fontFamily = UrbanistSemiBold,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun VisitRequestLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, fontFamily = UrbanistRegular, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontFamily = UrbanistSemiBold, fontSize = 13.sp, color = Color.Black)
    }
}
