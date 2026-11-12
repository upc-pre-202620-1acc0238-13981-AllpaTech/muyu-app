package com.example.muyu_app.features.plot_approval.domain

/** Visita presencial que un comerciante solicita a una parcela publicada. */
data class PlotVisit(
    val id: Int,
    val plotId: Int,
    val plotName: String,
    val plotPlace: String,
    val farmerEmail: String,
    val farmerName: String,
    val merchantEmail: String,
    val merchantName: String,
    val date: String,
    val time: String,
    val message: String,
    val status: String,
    val outcome: String,
    val agreedPrice: String,
) {
    val isRequested: Boolean get() = status == STATUS_REQUESTED
    val isConfirmed: Boolean get() = status == STATUS_CONFIRMED
    val isCompleted: Boolean get() = status == STATUS_COMPLETED
    val isRejected: Boolean get() = status == STATUS_REJECTED
    val reachedAgreement: Boolean get() = outcome == MilestoneState.VISIT_AGREEMENT

    companion object {
        const val STATUS_REQUESTED = "requested"
        const val STATUS_CONFIRMED = "confirmed"
        const val STATUS_REJECTED = "rejected"
        const val STATUS_COMPLETED = "completed"
    }
}

interface PlotVisitRepository {
    suspend fun getVisits(plotId: Int? = null): Result<List<PlotVisit>>
    suspend fun requestVisit(plotId: Int, date: String, time: String, message: String): Result<PlotVisit>
    suspend fun updateStatus(visitId: Int, status: String): Result<PlotVisit>
    suspend fun recordOutcome(visitId: Int, outcome: String, agreedPrice: String): Result<PlotVisit>
}
