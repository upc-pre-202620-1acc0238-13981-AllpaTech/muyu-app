package com.example.muyu_app.features.plot_approval.infrastructure.repositories

import com.example.muyu_app.features.plot_approval.domain.PlotVisit
import com.example.muyu_app.features.plot_approval.domain.PlotVisitRepository
import com.example.muyu_app.features.plot_approval.infrastructure.remote.VisitDto
import com.example.muyu_app.features.plot_approval.infrastructure.remote.VisitRequestDto
import com.example.muyu_app.features.plot_approval.infrastructure.remote.VisitService
import com.example.muyu_app.features.plot_approval.infrastructure.remote.VisitUpdateDto
import retrofit2.Response

class PlotVisitRepositoryImpl(
    private val service: VisitService,
) : PlotVisitRepository {

    override suspend fun getVisits(plotId: Int?): Result<List<PlotVisit>> = call {
        service.getVisits(plotId)
    }.map { body -> body.visits.map { it.toDomain() }.sortedByDescending { it.id } }

    override suspend fun requestVisit(
        plotId: Int,
        date: String,
        time: String,
        message: String,
    ): Result<PlotVisit> = call {
        service.requestVisit(VisitRequestDto(plotId, date, time, message))
    }.map { it.toDomain() }

    override suspend fun updateStatus(visitId: Int, status: String): Result<PlotVisit> = call {
        service.updateVisit(visitId, VisitUpdateDto(status = status))
    }.map { it.toDomain() }

    override suspend fun recordOutcome(
        visitId: Int,
        outcome: String,
        agreedPrice: String,
    ): Result<PlotVisit> = call {
        service.updateVisit(visitId, VisitUpdateDto(outcome = outcome, agreedPrice = agreedPrice))
    }.map { it.toDomain() }

    private suspend fun <T> call(request: suspend () -> Response<T>): Result<T> = try {
        val response = request()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Result.success(body)
        } else {
            Result.failure(Exception(response.errorBody()?.string() ?: response.message()))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun VisitDto.toDomain() = PlotVisit(
        id = id,
        plotId = plotId,
        plotName = plotName.orEmpty(),
        plotPlace = plotPlace.orEmpty(),
        farmerEmail = farmerEmail.orEmpty(),
        farmerName = farmerName.orEmpty(),
        merchantEmail = merchantEmail.orEmpty(),
        merchantName = merchantName.orEmpty(),
        date = date.orEmpty(),
        time = time.orEmpty(),
        message = message.orEmpty(),
        status = status.orEmpty(),
        outcome = outcome.orEmpty(),
        agreedPrice = agreedPrice.orEmpty(),
    )
}
