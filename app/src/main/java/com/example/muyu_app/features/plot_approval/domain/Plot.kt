package com.example.muyu_app.features.plot_approval.domain

data class Plot(
    val id: Int,
    val name: String,
    val crop: String,
    val area: String,
    val harvestDate: String,
    val place: String,
    val stage: String,
    val progress: Int,
    val status: String,
    val amount: String,
    val cycle: String,
    val milestones: String,
    val imageUri: String?,
    val ownerEmail: String,
    val ownerName: String = "",
    val fundedByEmail: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Int? = null,
    val publishedAt: String = "",
    val milestoneStates: List<MilestoneState> = emptyList(),
    val visitOutcome: String = "",
    val visitDate: String = "",
)

data class NewPlot(
    val name: String,
    val crop: String,
    val area: String,
    val harvestDate: String,
    val place: String,
    val amount: String,
    val cycle: String,
    val milestones: String,
    val imageUri: String?,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Int? = null,
)

class PlotDraft {
    var editingPlotId: Int? = null
    var name: String = ""
    var crop: String = ""
    var area: String = ""
    var harvestDate: String = ""
    var place: String = ""
    var amount: String = ""
    var cycle: String = ""
    var milestones: String = ""
    var imageUri: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var accuracyMeters: Int? = null
    var addressLine: String = ""
    var districtName: String = ""
    var provinceName: String = ""
    var regionName: String = ""

    val isEditing: Boolean get() = editingPlotId != null

    fun loadFrom(plot: Plot) {
        editingPlotId = plot.id
        name = plot.name
        crop = plot.crop
        area = plot.area
        harvestDate = plot.harvestDate
        place = plot.place
        amount = plot.amount
        cycle = plot.cycle.ifBlank { "6 months" }
        milestones = plot.milestones.ifBlank { "4" }
        imageUri = plot.imageUri
        latitude = plot.latitude
        longitude = plot.longitude
        accuracyMeters = plot.accuracyMeters
        val parts = plot.place.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        when {
            parts.size >= 4 -> {
                addressLine = parts[0]
                districtName = parts[1]
                provinceName = parts[2]
                regionName = parts[3]
            }
            parts.size == 3 -> {
                districtName = parts[0]
                provinceName = parts[1]
                regionName = parts[2]
            }
            parts.size == 2 -> {
                provinceName = parts[0]
                regionName = parts[1]
            }
            else -> regionName = plot.place
        }
    }

    fun toNewPlot() = NewPlot(
        name = name,
        crop = crop,
        area = area,
        harvestDate = harvestDate,
        place = place.ifBlank { "Peru" },
        amount = amount,
        cycle = cycle,
        milestones = milestones,
        imageUri = imageUri,
        latitude = latitude,
        longitude = longitude,
        accuracyMeters = accuracyMeters,
    )

    fun clear() {
        editingPlotId = null
        name = ""
        crop = ""
        area = ""
        harvestDate = ""
        place = ""
        amount = ""
        cycle = ""
        milestones = ""
        imageUri = null
        latitude = null
        longitude = null
        accuracyMeters = null
        addressLine = ""
        districtName = ""
        provinceName = ""
        regionName = ""
    }
}
