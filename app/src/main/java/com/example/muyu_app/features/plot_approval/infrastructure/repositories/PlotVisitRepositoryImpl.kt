package com.example.muyu_app.features.plot_approval.infrastructure.repositories

import com.example.muyu_app.features.plot_approval.domain.PlotVisit
import com.example.muyu_app.features.plot_approval.domain.PlotVisitRepository
import com.example.muyu_app.core.network.BackendStateStore

class PlotVisitRepositoryImpl(
    private val store: BackendStateStore,
) : PlotVisitRepository {

    override suspend fun getVisits(plotId: Int?): Result<List<PlotVisit>> = Result.success(
        store.visits.filter { plotId == null || it.plotId == plotId }.sortedByDescending { it.id },
    )

    override suspend fun requestVisit(
        plotId: Int,
        date: String,
        time: String,
        message: String,
    ): Result<PlotVisit> {
        val plot = store.plotById(plotId)
            ?: return Result.failure(IllegalArgumentException("Plot $plotId was not found"))
        val merchant = store.currentUser
            ?: return Result.failure(IllegalStateException("Authentication is required"))
        val visit = PlotVisit(
            id = (store.visits.maxOfOrNull { it.id } ?: 0) + 1,
            plotId = plotId,
            plotName = plot.name,
            plotPlace = plot.place,
            farmerEmail = plot.ownerEmail,
            farmerName = plot.ownerName,
            merchantEmail = merchant.email,
            merchantName = merchant.displayName,
            date = date,
            time = time,
            message = message,
            status = "pending",
            outcome = "",
            agreedPrice = "",
        )
        store.visits.add(visit)
        store.persist()
        return Result.success(visit)
    }

    override suspend fun updateStatus(visitId: Int, status: String): Result<PlotVisit> =
        updateVisit(visitId) { it.copy(status = status) }

    override suspend fun recordOutcome(
        visitId: Int,
        outcome: String,
        agreedPrice: String,
    ): Result<PlotVisit> = updateVisit(visitId) {
        it.copy(outcome = outcome, agreedPrice = agreedPrice)
    }

    private fun updateVisit(visitId: Int, change: (PlotVisit) -> PlotVisit): Result<PlotVisit> {
        val index = store.visits.indexOfFirst { it.id == visitId }
        if (index < 0) return Result.failure(IllegalArgumentException("Visit $visitId was not found"))
        val updated = change(store.visits[index])
        store.visits[index] = updated
        store.persist()
        return Result.success(updated)
    }

}
