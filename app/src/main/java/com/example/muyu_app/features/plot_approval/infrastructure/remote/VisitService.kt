package com.example.muyu_app.features.plot_approval.infrastructure.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface VisitService {
    @GET("visits")
    suspend fun getVisits(@Query("plotId") plotId: Int?): Response<VisitsResponseDto>

    @POST("visits")
    @Headers("Content-Type: application/json")
    suspend fun requestVisit(@Body body: VisitRequestDto): Response<VisitDto>

    @PUT("visits/{id}")
    @Headers("Content-Type: application/json")
    suspend fun updateVisit(@Path("id") id: Int, @Body body: VisitUpdateDto): Response<VisitDto>
}

data class VisitDto(
    val id: Int = 0,
    val plotId: Int = 0,
    val plotName: String? = "",
    val plotPlace: String? = "",
    val farmerEmail: String? = "",
    val farmerName: String? = "",
    val merchantEmail: String? = "",
    val merchantName: String? = "",
    val date: String? = "",
    val time: String? = "",
    val message: String? = "",
    val status: String? = "",
    val outcome: String? = "",
    val agreedPrice: String? = "",
)

data class VisitsResponseDto(
    val visits: List<VisitDto> = emptyList(),
)

data class VisitRequestDto(
    val plotId: Int,
    val date: String,
    val time: String,
    val message: String,
)

// Gson omite los campos nulos: solo se envía lo que cambia.
data class VisitUpdateDto(
    val status: String? = null,
    val outcome: String? = null,
    val agreedPrice: String? = null,
)
