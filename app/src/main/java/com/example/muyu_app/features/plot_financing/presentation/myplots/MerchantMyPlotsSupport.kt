package com.example.muyu_app.features.plot_financing.presentation.myplots

import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.Plot

fun Plot.milestoneStatesOrSeed(): List<MilestoneState> =
    milestoneStates.ifEmpty { MilestoneCatalog.seed(stage, progress) }

fun Plot.approvedMilestoneCount(): Int =
    milestoneStatesOrSeed().count { it.isApproved }

fun Plot.submittedForReview(): MilestoneState? =
    milestoneStatesOrSeed().firstOrNull { it.status == MilestoneState.STATUS_SUBMITTED }

fun Plot.needsMerchantReview(): Boolean = submittedForReview() != null

fun Plot.evidenceMilestoneKey(): String {
    val states = milestoneStatesOrSeed()
    submittedForReview()?.key?.let { return it }
    states.firstOrNull { it.needsCorrection }?.key?.let { return it }
    states.firstOrNull { it.status == MilestoneState.STATUS_PENDING }?.key?.let { return it }
    states.lastOrNull { it.isApproved }?.key?.let { return it }
    return MilestoneCatalog.all.first().key
}

fun Plot.nextPendingMilestone(): MilestoneState? =
    milestoneStatesOrSeed().firstOrNull { it.status == MilestoneState.STATUS_PENDING }

fun Plot.currentMilestoneLabelKey(): String? =
    submittedForReview()?.key ?: nextPendingMilestone()?.key

fun Plot.sortPriorityNeedsReview(): Int = if (needsMerchantReview()) 0 else 1

fun plotsNeedingReviewCount(plots: List<Plot>): Int = plots.count { it.needsMerchantReview() }

fun cuscoFundedPlots(plots: List<Plot>): List<Plot> =
    plots.filter { it.place.contains("cusco", ignoreCase = true) || it.place.contains("cuzco", ignoreCase = true) }
