package com.example.muyu_app.features.plot_approval.domain

fun Plot.milestoneForEvidence(selectedKey: String): MilestoneState? {
    milestoneStates.find { it.key == selectedKey }?.let { return it }
    val states = milestoneStates.ifEmpty { MilestoneCatalog.seed(stage, progress) }
    return states.firstOrNull { it.status == MilestoneState.STATUS_SUBMITTED }
}

fun Plot.currentMilestone(): MilestoneState? {
    val states = milestoneStates.ifEmpty { MilestoneCatalog.seed(stage, progress) }
    return states.firstOrNull { it.needsCorrection }
        ?: states.firstOrNull { it.status == MilestoneState.STATUS_PENDING }
        ?: states.firstOrNull { it.canRecord }
}
