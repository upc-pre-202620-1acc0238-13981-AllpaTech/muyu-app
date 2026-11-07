package com.example.muyu_app.core.network

import com.example.muyu_app.features.plot_approval.domain.PlotVisit

data class SyncHistoryRecord(
    val ownerEmail: String = "",
    val plotId: Int = 0,
    val plotName: String = "",
    val milestoneKey: String = "",
    val queuedAtMillis: Long = 0L,
    val syncedAtMillis: Long = 0L,
)

data class RatingRecord(
    val authorEmail: String,
    val score: Int,
    val comment: String,
    val createdAtMillis: Long,
)

data class BackendSnapshot(
    val users: List<UserRecord> = emptyList(),
    val plots: List<PlotRecord> = emptyList(),
    val merchantNotifications: List<MerchantNotificationRecord> = emptyList(),
    val syncHistory: List<SyncHistoryRecord> = emptyList(),
    val farmerNotifications: List<FarmerNotificationRecord>? = emptyList(),
    val preferences: Map<String, String> = emptyMap(),
    val visits: List<PlotVisit> = emptyList(),
    val ratings: List<RatingRecord> = emptyList(),
)
