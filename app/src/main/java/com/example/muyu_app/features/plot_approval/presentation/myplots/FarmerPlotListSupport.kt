package com.example.muyu_app.features.plot_approval.presentation.myplots

import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.Plot

enum class FarmerPlotFilter {
    All,
    Funded,
    Available,
}

fun Plot.matchesFarmerFilter(filter: FarmerPlotFilter): Boolean = when (filter) {
    FarmerPlotFilter.All -> true
    FarmerPlotFilter.Funded -> status == "funded" || status == "approved"
    FarmerPlotFilter.Available -> status == "available" || status == "published" || status == "pending"
}

fun Plot.milestoneStatesOrSeed(): List<MilestoneState> =
    milestoneStates.ifEmpty { MilestoneCatalog.seed(stage, progress) }

fun Plot.approvedMilestoneCount(): Int =
    milestoneStatesOrSeed().count { it.isApproved }

fun Plot.isFundedPlot(): Boolean = status == "funded" || status == "approved"

fun Plot.stageTitleRes(): Int? {
    if (!isFundedPlot()) return null
    val states = milestoneStatesOrSeed()
    val current = states.firstOrNull { it.isQueued || it.isSubmitted }
        ?: states.firstOrNull { it.needsCorrection }
        ?: states.firstOrNull { it.status == MilestoneState.STATUS_PENDING }
    return current?.key?.let { MilestoneCatalog.definition(it).titleRes }
}

fun Plot.hasHeavyRainRisk(): Boolean =
    isFundedPlot() &&
        (
            place.contains("cusco", ignoreCase = true) ||
                place.contains("cuzco", ignoreCase = true)
            )

fun formatPlotArea(area: String): String {
    val trimmed = area.trim()
    if (trimmed.isBlank()) return "—"
    if (trimmed.contains("ha", ignoreCase = true)) return trimmed
    return "$trimmed ha"
}

fun Plot.matchesSearch(query: String): Boolean {
    if (query.isBlank()) return true
    val q = query.trim()
    return name.contains(q, ignoreCase = true) ||
        crop.contains(q, ignoreCase = true) ||
        place.contains(q, ignoreCase = true) ||
        listOfNotNull(latitude, longitude).any { it.toString().contains(q) }
}
