package com.example.muyu_app.features.plot_approval.infrastructure.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PlotService {
    @GET("plots")
    suspend fun getPlots(): Response<PlotsResponseDto>

    @POST("plots")
    @Headers("Content-Type: application/json")
    suspend fun createPlot(@Body body: PlotDto): Response<PlotDto>

    @PUT("plots/{id}")
    @Headers("Content-Type: application/json")
    suspend fun updatePlot(@Path("id") id: Int, @Body body: PlotDto): Response<PlotDto>

    @PUT("plots/{id}")
    @Headers("Content-Type: application/json")
    suspend fun syncPlot(@Path("id") id: Int, @Body body: PlotDto): Response<PlotDto>

    @POST("plots/{id}/fund")
    suspend fun fundPlot(@Path("id") id: Int): Response<PlotDto>
}
