package com.example.muyu_app.features.labor_execution.presentation.sync

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import coil3.compose.AsyncImage
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.network.SyncHistoryRecord
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.labor_execution.presentation.evidence.QueuedEvidenceItem
import com.example.muyu_app.features.labor_execution.presentation.evidence.formatSyncItemDate
import com.example.muyu_app.features.labor_execution.presentation.evidence.queuedEvidenceForOwner
import com.example.muyu_app.features.labor_execution.presentation.evidence.rememberAutoEvidenceSync
import com.example.muyu_app.features.labor_execution.presentation.evidence.rememberNetworkOnline
import com.example.muyu_app.features.labor_execution.presentation.evidence.syncQueuedEvidenceForOwner
import com.example.muyu_app.features.labor_execution.presentation.evidence.syncedEvidenceCountForOwner
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(12.dp)
private val LightGreenBg = Color(0xFFDCF0DD)
private val DarkGreenText = Color(0xFF2E6930)
private val GrayChipBg = Color(0xFFE0E0E0)
private val GrayChipText = Color(0xFF616161)

@Composable
fun SyncScreen(
    onBack: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val online by rememberNetworkOnline()
    val farmerEmail = container.fakeStore.currentUser?.email.orEmpty()
    var queue by remember { mutableStateOf<List<QueuedEvidenceItem>>(emptyList()) }
    var syncedTotal by remember { mutableIntStateOf(0) }
    var uploadingId by remember { mutableStateOf<String?>(null) }
    var historyExpanded by remember { mutableStateOf(false) }
    var historyItems by remember { mutableStateOf<List<SyncHistoryRecord>>(emptyList()) }
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()

    fun reload() {
        queue = container.fakeStore.queuedEvidenceForOwner(farmerEmail)
        syncedTotal = container.fakeStore.syncedEvidenceCountForOwner(farmerEmail)
        historyItems = container.fakeStore.syncHistoryForOwner(farmerEmail)
    }

    rememberAutoEvidenceSync(container, farmerEmail) { reload() }

    androidx.compose.runtime.LaunchedEffect(lifecycleState.value, farmerEmail) {
        if (lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) {
            reload()
        }
    }

    androidx.compose.runtime.LaunchedEffect(online) {
        if (online && queue.isNotEmpty()) {
            uploadingId = queue.firstOrNull()?.let { "${it.plotId}_${it.milestoneKey}" }
            container.fakeStore.syncQueuedEvidenceForOwner(farmerEmail)
            reload()
            uploadingId = null
        }
    }

    val allFundedLabel = stringResource(R.string.sync_all_funded_plots)
    val contextLine = remember(queue, allFundedLabel) {
        queue.firstOrNull()?.let { item ->
            "${item.plotName} - ${item.crop} · ${PlotFinance.seasonYear(item.cycle)}"
        } ?: allFundedLabel
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.sync_title),
                onBack = onBack,
                trailing = {
                    ConnectionChip(online = online)
                },
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                Text(
                    text = contextLine,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(LightGreenBg)
                        .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.sync_pending_proofs_count, queue.size),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreenText,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.sync_pending_body),
                            fontSize = 13.sp,
                            color = DarkGreenText.copy(alpha = 0.8f),
                            lineHeight = 18.sp,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (queue.isEmpty()) {
                    Text(
                        text = stringResource(
                            if (online) R.string.sync_nothing_pending else R.string.sync_offline_later,
                        ),
                        fontSize = 15.sp,
                        color = Color.Black.copy(alpha = 0.55f),
                    )
                } else {
                    queue.forEach { item ->
                        val itemId = "${item.plotId}_${item.milestoneKey}"
                        val uploading = uploadingId == itemId && online
                        SyncItemCard(
                            item = item,
                            waitingForSignal = !online,
                            uploading = uploading,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(CardShape)
                        .background(
                            if (online && queue.isNotEmpty()) MuyuAction else LightGreenBg.copy(alpha = 0.5f),
                        )
                        .then(
                            if (!online || queue.isEmpty()) {
                                Modifier.border(1.dp, DarkGreenText.copy(alpha = 0.6f), CardShape)
                            } else {
                                Modifier
                            },
                        )
                        .then(
                            if (online && queue.isNotEmpty()) {
                                Modifier.clickable {
                                    uploadingId = queue.firstOrNull()?.let { "${it.plotId}_${it.milestoneKey}" }
                                    container.fakeStore.syncQueuedEvidenceForOwner(farmerEmail)
                                    reload()
                                    uploadingId = null
                                }
                            } else {
                                Modifier
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = when {
                            !online -> stringResource(R.string.sync_offline_later)
                            queue.isEmpty() -> stringResource(R.string.sync_all_up_to_date)
                            else -> stringResource(R.string.sync_now)
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (online && queue.isNotEmpty()) Color.White else DarkGreenText,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, Color.Gray.copy(alpha = 0.4f), CardShape)
                        .clickable { historyExpanded = !historyExpanded }
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.sync_history),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.sync_proofs_synced_count, syncedTotal),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                            )
                        }
                        MuyuAsset(
                            fileName = "publish/chevron.svg",
                            contentDescription = stringResource(R.string.sync_expand_history),
                            width = 16.dp,
                            height = 10.dp,
                            modifier = Modifier.rotate(if (historyExpanded) 180f else 0f),
                        )
                    }
                    if (historyExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        if (historyItems.isEmpty()) {
                            Text(
                                text = stringResource(R.string.sync_history_empty),
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.55f),
                            )
                        } else {
                            historyItems.forEach { entry ->
                                SyncHistoryRow(entry = entry)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
        MuyuBottomBar(
            selectedTab = MuyuTab.Sync,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun ConnectionChip(online: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (online) LightGreenBg else Color.White.copy(alpha = 0.85f))
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = stringResource(if (online) R.string.sync_connected else R.string.sync_offline),
            color = if (online) DarkGreenText else Color(0xFF424242),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SyncItemCard(
    item: QueuedEvidenceItem,
    waitingForSignal: Boolean,
    uploading: Boolean,
) {
    val activity = stringResource(MilestoneCatalog.definition(item.milestoneKey).titleRes)
    val capturedAt = item.milestone.submittedAtMillis.takeIf { it > 0 }
        ?: System.currentTimeMillis()
    val hasGps = item.milestone.latitude != null && item.milestone.longitude != null
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest.copy(alpha = 0.5f), CardShape)
            .padding(14.dp),
    ) {
        Column {
            Text(
                text = "${item.plotName} · $activity",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = item.milestone.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusChip(
                        text = when {
                            uploading -> stringResource(R.string.sync_uploading)
                            waitingForSignal -> stringResource(R.string.sync_waiting_signal)
                            else -> stringResource(R.string.sync_now)
                        },
                        bg = if (uploading) Color(0xFFE8EAF6) else GrayChipBg,
                        textColor = if (uploading) Color(0xFF283593) else GrayChipText,
                    )
                    StatusChip(
                        text = if (hasGps) {
                            stringResource(R.string.sync_location_recorded)
                        } else {
                            stringResource(R.string.merchant_gps_not_recorded)
                        },
                        bg = LightGreenBg,
                        textColor = DarkGreenText,
                    )
                    StatusChip(
                        text = formatSyncItemDate(capturedAt),
                        bg = LightGreenBg,
                        textColor = DarkGreenText,
                    )
                }
            }
            if (uploading) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = DarkGreenText,
                    trackColor = LightGreenBg,
                )
            }
        }
    }
}

@Composable
private fun SyncHistoryRow(entry: SyncHistoryRecord) {
    val activity = stringResource(MilestoneCatalog.definition(entry.milestoneKey).titleRes)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightGreenBg.copy(alpha = 0.45f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = "${entry.plotName} · $activity",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.sync_history_offline_line),
            fontSize = 12.sp,
            color = DarkGreenText,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(
                R.string.sync_history_times_fmt,
                formatSyncItemDate(entry.queuedAtMillis),
                formatSyncItemDate(entry.syncedAtMillis),
            ),
            fontSize = 12.sp,
            color = Color.Black.copy(alpha = 0.65f),
        )
    }
}

@Composable
private fun StatusChip(text: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun SyncScreenPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme {
        SyncScreen(onBack = {}, onTabSelected = {})
    }
}
