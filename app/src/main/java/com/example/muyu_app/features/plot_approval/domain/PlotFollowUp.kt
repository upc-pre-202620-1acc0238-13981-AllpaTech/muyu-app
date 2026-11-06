package com.example.muyu_app.features.plot_approval.domain

/** Incidencia que el comerciante reporta sobre la entrega de la cosecha (US31). */
data class HarvestIncident(
    val reason: String = "",
    val details: String = "",
    val reportedAt: String = "",
    val reportedAtMillis: Long = 0L,
    val reportedBy: String = "",
) {
    companion object {
        const val REASON_NOT_DELIVERED = "not_delivered"
        const val REASON_INCOMPLETE = "incomplete"
        const val REASON_QUALITY = "quality"
        const val REASON_LATE = "late"
        const val REASON_OTHER = "other"

        val reasons = listOf(
            REASON_NOT_DELIVERED,
            REASON_INCOMPLETE,
            REASON_QUALITY,
            REASON_LATE,
            REASON_OTHER,
        )
    }
}

/** Medidas que el agricultor aplicó ante una alerta climática (US33). */
data class WeatherMeasure(
    val alertKey: String = "",
    val measures: List<String> = emptyList(),
    val attendedAt: String = "",
    val attendedAtMillis: Long = 0L,
    val attendedBy: String = "",
)

/** Estado de la entrega de la cosecha de una parcela financiada (US30 y US31). */
enum class HarvestDeliveryState { NotReady, Pending, WithIncident, Received }

fun Plot.harvestDeliveryState(): HarvestDeliveryState {
    if (harvestReceivedAt.isNotBlank()) return HarvestDeliveryState.Received
    val harvestApproved = milestoneStates.any {
        it.key == MilestoneState.KEY_HARVEST && it.isApproved
    }
    if (status != "funded" || !harvestApproved) return HarvestDeliveryState.NotReady
    return if (harvestIncidents.isEmpty()) {
        HarvestDeliveryState.Pending
    } else {
        HarvestDeliveryState.WithIncident
    }
}
