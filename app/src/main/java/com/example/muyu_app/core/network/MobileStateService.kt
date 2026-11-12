package com.example.muyu_app.core.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import com.example.muyu_app.features.plot_approval.domain.PlotVisit

/** Synchronizes all mobile-owned feature state with the authenticated backend user. */
interface MobileStateService {
    @GET("mobile/state")
    suspend fun getState(): Response<BackendSnapshot>

    @PUT("mobile/state")
    suspend fun putState(@Body snapshot: BackendSnapshot): Response<Unit>

    @GET("mobile/plots")
    suspend fun getPlots(): Response<List<PlotRecord>>

    @PUT("mobile/plots/{id}")
    suspend fun putPlot(@Path("id") id: Int, @Body plot: PlotRecord): Response<Unit>

    @DELETE("mobile/plots/{id}")
    suspend fun deletePlot(@Path("id") id: Int): Response<Unit>

    @GET("mobile/visits")
    suspend fun getVisits(): Response<List<PlotVisit>>

    @PUT("mobile/visits/{id}")
    suspend fun putVisit(@Path("id") id: Int, @Body visit: PlotVisit): Response<Unit>
}
