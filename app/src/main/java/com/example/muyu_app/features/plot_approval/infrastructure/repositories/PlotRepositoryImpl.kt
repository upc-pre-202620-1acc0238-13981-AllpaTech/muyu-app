package com.example.muyu_app.features.plot_approval.infrastructure.repositories

import com.example.muyu_app.core.environment.Environment
import com.example.muyu_app.core.network.BackendStateStore
import com.example.muyu_app.core.network.PlotRecord
import com.example.muyu_app.core.network.MobileStateService
import com.example.muyu_app.features.plot_approval.domain.NewPlot
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotRepository
import com.example.muyu_app.features.plot_approval.infrastructure.remote.PlotDto

class PlotRepositoryImpl(
    private val store: BackendStateStore,
    private val mobileStateService: MobileStateService,
) : PlotRepository {

    suspend fun syncPlot(plot: Plot): Result<Plot> = runCatching {
        store.upsertPlot(plot.toDto().toRecord())
        plot
    }

    /** Consulta el estado real de una parcela en el servidor y lo guarda en el dispositivo. */
    suspend fun fetchRemotePlot(id: Int): Result<Plot?> = Result.success(
        store.plotById(id)?.let(store::toDomain),
    )

    override suspend fun getPlots(): Result<List<Plot>> {
        return Result.success(store.plots.map(store::toDomain))
    }

    override suspend fun createPlot(plot: NewPlot): Result<Plot> {
        val owner = store.currentUser
            ?: return Result.failure(IllegalStateException("Authentication is required"))
        val record = PlotRecord(
            id = store.nextPlotId(), name = plot.name, crop = plot.crop, area = plot.area,
            harvestDate = plot.harvestDate, place = plot.place, stage = "Published", progress = 0,
            status = "available", amount = plot.amount, cycle = plot.cycle,
            milestones = plot.milestones, imageUri = plot.imageUri, ownerEmail = owner.email,
            ownerName = owner.displayName, latitude = plot.latitude, longitude = plot.longitude,
            accuracyMeters = plot.accuracyMeters, publishedAt = System.currentTimeMillis().toString(),
        )
        store.addPlot(record)
        return Result.success(store.toDomain(record))
    }

    override suspend fun updatePlot(plotId: Int, plot: NewPlot): Result<Plot> {
        val email = store.currentUser?.email
            ?: return Result.failure(IllegalStateException("Authentication is required"))
        val updated = store.updatePlot(
            plotId,
            com.example.muyu_app.core.network.PlotUpdatePayload(
                plot.name, plot.crop, plot.area, plot.harvestDate, plot.place, plot.amount,
                plot.cycle, plot.milestones, plot.imageUri, plot.latitude, plot.longitude,
                plot.accuracyMeters,
            ),
            email,
        ) ?: return Result.failure(IllegalStateException("The plot cannot be updated"))
        return Result.success(store.toDomain(updated))
    }

    override suspend fun fundPlot(id: Int): Result<Plot> {
        val email = store.currentUser?.email
            ?: return Result.failure(IllegalStateException("Authentication is required"))
        val funded = store.fundPlot(id, email)
            ?: return Result.failure(IllegalArgumentException("Plot $id was not found"))
        return Result.success(store.toDomain(funded))
    }

    override suspend fun deletePlot(id: Int): Result<Unit> {
        val response = mobileStateService.deletePlot(id)
        if (!response.isSuccessful) {
            return Result.failure(IllegalStateException("Delete failed: ${response.code()}"))
        }
        store.plots.removeAll { it.id == id }
        store.persist()
        return Result.success(Unit)
    }

    private fun PlotDto.toPlot() = Plot(
        id = id,
        name = name,
        crop = crop,
        area = area,
        harvestDate = harvestDate,
        place = place,
        stage = stage,
        progress = progress,
        status = status,
        amount = amount,
        cycle = cycle,
        milestones = milestones,
        imageUri = imageUri,
        ownerEmail = ownerEmail,
        ownerName = ownerName,
        fundedByEmail = fundedByEmail,
        latitude = latitude,
        longitude = longitude,
        accuracyMeters = accuracyMeters,
        publishedAt = publishedAt,
        milestoneStates = milestoneStates,
        visitOutcome = visitOutcome,
        visitDate = visitDate,
        agreedPrice = agreedPrice.orEmpty(),
        harvestReceivedAt = harvestReceivedAt.orEmpty(),
        harvestReceivedBy = harvestReceivedBy.orEmpty(),
        harvestIncidents = harvestIncidents.orEmpty(),
        weatherMeasures = weatherMeasures.orEmpty(),
    )

    private fun Plot.toDto() = PlotDto(
        id, name, crop, area, harvestDate, place, stage, progress, status, amount,
        cycle, milestones, imageUri, ownerEmail, ownerName, fundedByEmail, latitude,
        longitude, accuracyMeters, publishedAt, milestoneStates, visitOutcome, visitDate, agreedPrice,
        harvestReceivedAt, harvestReceivedBy, harvestIncidents, weatherMeasures,
    )

    private fun PlotDto.toRecord() = PlotRecord(
        id, name, crop, area, harvestDate, place, stage, progress, status, amount,
        cycle, milestones, imageUri, ownerEmail, ownerName, fundedByEmail, latitude,
        longitude, accuracyMeters, publishedAt, milestoneStates, visitOutcome, visitDate,
        agreedPrice.orEmpty(),
        harvestReceivedAt.orEmpty(), harvestReceivedBy.orEmpty(),
        harvestIncidents.orEmpty(), weatherMeasures.orEmpty(),
    )
}
