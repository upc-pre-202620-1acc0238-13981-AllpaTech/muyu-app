package com.example.muyu_app.features.labor_execution.presentation.evidence

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.network.FakeApiStore
import com.example.muyu_app.core.network.SyncHistoryRecord
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.core.network.isNetworkConnected
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import java.text.DateFormat
import java.util.Date

data class QueuedEvidenceItem(
    val plotId: Int,
    val plotName: String,
    val crop: String,
    val cycle: String,
    val milestoneKey: String,
    val milestone: MilestoneState,
)

fun FakeApiStore.queuedEvidenceForOwner(ownerEmail: String): List<QueuedEvidenceItem> {
    if (ownerEmail.isBlank()) return emptyList()
    return plots
        .filter { it.ownerEmail.equals(ownerEmail, ignoreCase = true) && it.status == "funded" }
        .flatMap { plot ->
            val states = plot.milestoneStates.ifEmpty { MilestoneCatalog.seed(plot.stage, plot.progress) }
            states
                .filter { it.status == MilestoneState.STATUS_QUEUED }
                .map { milestone ->
                    QueuedEvidenceItem(
                        plotId = plot.id,
                        plotName = plot.name,
                        crop = plot.crop,
                        cycle = plot.cycle,
                        milestoneKey = milestone.key,
                        milestone = milestone,
                    )
                }
        }
        .sortedByDescending { it.milestone.submittedAtMillis }
}

fun FakeApiStore.syncedEvidenceCountForOwner(ownerEmail: String): Int {
    if (ownerEmail.isBlank()) return 0
    return plots
        .filter { it.ownerEmail.equals(ownerEmail, ignoreCase = true) }
        .sumOf { plot ->
            plot.milestoneStates.count {
                it.status == MilestoneState.STATUS_SUBMITTED || it.status == MilestoneState.STATUS_APPROVED
            }
        }
}

fun FakeApiStore.syncQueuedEvidenceForOwner(ownerEmail: String): Int {
    val pending = queuedEvidenceForOwner(ownerEmail)
    if (pending.isEmpty()) return 0
    val syncedAt = System.currentTimeMillis()
    pending.forEach { item ->
        syncHistory.add(
            0,
            SyncHistoryRecord(
                ownerEmail = ownerEmail,
                plotId = item.plotId,
                plotName = item.plotName,
                milestoneKey = item.milestoneKey,
                queuedAtMillis = item.milestone.submittedAtMillis.takeIf { it > 0L } ?: syncedAt,
                syncedAtMillis = syncedAt,
            ),
        )
    }
    while (syncHistory.size > 50) {
        syncHistory.removeAt(syncHistory.lastIndex)
    }
    pending.forEach { item ->
        updateMilestone(
            item.plotId,
            item.milestone.copy(status = MilestoneState.STATUS_SUBMITTED),
        )
    }
    return pending.size
}

fun formatSyncItemDate(millis: Long): String {
    if (millis <= 0L) return "—"
    val date = PlotFinance.shortDateFromMillis(millis)
    val time = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(millis))
    return "$date, $time"
}

@Composable
fun rememberNetworkOnline(context: Context = LocalContext.current): State<Boolean> {
    val online = remember { mutableStateOf(isNetworkConnected(context)) }
    DisposableEffect(context) {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (manager == null) {
            onDispose { }
        } else {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    online.value = true
                }

                override fun onLost(network: Network) {
                    online.value = isNetworkConnected(context)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    capabilities: android.net.NetworkCapabilities,
                ) {
                    online.value = isNetworkConnected(context)
                }
            }
            manager.registerDefaultNetworkCallback(callback)
            online.value = isNetworkConnected(context)
            onDispose { manager.unregisterNetworkCallback(callback) }
        }
    }
    return online
}

@Composable
fun rememberAutoEvidenceSync(
    container: AppContainer,
    farmerEmail: String,
    onSynced: () -> Unit = {},
): Int {
    val online by rememberNetworkOnline()
    var syncGeneration by remember { mutableIntStateOf(0) }
    androidx.compose.runtime.LaunchedEffect(online, farmerEmail) {
        if (online && farmerEmail.isNotBlank()) {
            val synced = container.fakeStore.syncQueuedEvidenceForOwner(farmerEmail)
            if (synced > 0) {
                syncGeneration++
                onSynced()
            }
        }
    }
    return syncGeneration
}
