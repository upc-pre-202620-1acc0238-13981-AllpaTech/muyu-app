package com.example.muyu_app.features.plot_approval.infrastructure.remote

data class PlotDto(
    val id: Int = 0,
    val name: String = "",
    val crop: String = "",
    val area: String = "",
    val harvestDate: String = "",
    val place: String = "",
    val stage: String = "",
    val progress: Int = 0,
    val status: String = "",
    val amount: String = "",
    val cycle: String = "",
    val milestones: String = "",
    val imageUri: String? = null,
    val ownerEmail: String = "",
    val ownerName: String = "",
    val fundedByEmail: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Int? = null,
    val publishedAt: String = "",
    val milestoneStates: List<com.example.muyu_app.features.plot_approval.domain.MilestoneState> = emptyList(),
    val visitOutcome: String = "",
    val visitDate: String = "",
)

data class PlotsResponseDto(
    val plots: List<PlotDto> = emptyList(),
)
