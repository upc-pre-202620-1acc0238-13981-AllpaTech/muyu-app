package com.example.muyu_app.features.plot_approval.infrastructure.repositories

import com.example.muyu_app.core.environment.Environment
import com.example.muyu_app.core.network.FakeApiStore
import com.example.muyu_app.core.network.PlotRecord
import com.example.muyu_app.features.plot_approval.domain.NewPlot
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotRepository
import com.example.muyu_app.features.plot_approval.infrastructure.remote.PlotDto
import com.example.muyu_app.features.plot_approval.infrastructure.remote.PlotService

class PlotRepositoryImpl(
    private val service: PlotService,
    private val store: FakeApiStore,
) : PlotRepository {

    suspend fun syncPlot(plot: Plot): Result<Plot> = try {
        val response = service.syncPlot(plot.id, plot.toDto())
        val dto = response.body()
        if (response.isSuccessful && dto != null) {
            store.upsertPlot(dto.toRecord())
            Result.success(dto.toPlot())
        } else {
            Result.failure(Exception(response.message()))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPlots(): Result<List<Plot>> {
        if (!Environment.USE_FAKE_API) {
            return Result.success(store.plots.map { store.toDomain(it) })
        }
        return try {
            val response = service.getPlots()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body.plots.map { it.toPlot() })
            } else {
                Result.success(store.plots.map { store.toDomain(it) })
            }
        } catch (e: Exception) {
            Result.success(store.plots.map { store.toDomain(it) })
        }
    }

    override suspend fun createPlot(plot: NewPlot): Result<Plot> {
        return try {
            val response = service.createPlot(
                PlotDto(
                    name = plot.name,
                    crop = plot.crop,
                    area = plot.area,
                    harvestDate = plot.harvestDate,
                    place = plot.place,
                    amount = plot.amount,
                    cycle = plot.cycle,
                    milestones = plot.milestones,
                    imageUri = plot.imageUri,
                    latitude = plot.latitude,
                    longitude = plot.longitude,
                    accuracyMeters = plot.accuracyMeters,
                ),
            )
            val dto = response.body()
            if (response.isSuccessful && dto != null) {
                store.upsertPlot(dto.toRecord())
                Result.success(dto.toPlot())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlot(plotId: Int, plot: NewPlot): Result<Plot> {
        return try {
            val response = service.updatePlot(
                plotId,
                PlotDto(
                    name = plot.name,
                    crop = plot.crop,
                    area = plot.area,
                    harvestDate = plot.harvestDate,
                    place = plot.place,
                    amount = plot.amount,
                    cycle = plot.cycle,
                    milestones = plot.milestones,
                    imageUri = plot.imageUri,
                    latitude = plot.latitude,
                    longitude = plot.longitude,
                    accuracyMeters = plot.accuracyMeters,
                ),
            )
            val dto = response.body()
            if (response.isSuccessful && dto != null) {
                store.upsertPlot(dto.toRecord())
                Result.success(dto.toPlot())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fundPlot(id: Int): Result<Plot> {
        return try {
            val response = service.fundPlot(id)
            val dto = response.body()
            if (response.isSuccessful && dto != null) {
                store.upsertPlot(dto.toRecord())
                Result.success(dto.toPlot())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
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
    )

    private fun Plot.toDto() = PlotDto(
        id, name, crop, area, harvestDate, place, stage, progress, status, amount,
        cycle, milestones, imageUri, ownerEmail, ownerName, fundedByEmail, latitude,
        longitude, accuracyMeters, publishedAt, milestoneStates, visitOutcome, visitDate,
    )

    private fun PlotDto.toRecord() = PlotRecord(
        id, name, crop, area, harvestDate, place, stage, progress, status, amount,
        cycle, milestones, imageUri, ownerEmail, ownerName, fundedByEmail, latitude,
        longitude, accuracyMeters, publishedAt, milestoneStates, visitOutcome, visitDate,
    )
}
