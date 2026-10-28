package com.example.muyu_app.features.plot_approval.domain

import com.example.muyu_app.R

data class MilestoneState(
    val key: String = KEY_PLANTING,
    val status: String = STATUS_PENDING,
    val dueLabel: String = "",
    val photoUri: String = "",
    val inputUsed: String = "",
    val amountApplied: String = "",
    val notes: String = "",
    val recordedAt: String = "",
    val submittedAtMillis: Long = 0L,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Int? = null,
    val extensionDate: String = "",
    val extensionReason: String = "",
) {
    val isApproved: Boolean get() = status == STATUS_APPROVED
    val isSubmitted: Boolean get() = status == STATUS_SUBMITTED
    val isQueued: Boolean get() = status == STATUS_QUEUED
    val needsCorrection: Boolean get() = status == STATUS_NEEDS_CORRECTION
    val canRecord: Boolean get() = status == STATUS_PENDING || needsCorrection

    companion object {
        const val KEY_PLANTING = "planting"
        const val KEY_IRRIGATION = "irrigation"
        const val KEY_FERTILIZATION = "fertilization"
        const val KEY_HARVEST = "harvest"

        const val STATUS_APPROVED = "approved"
        const val STATUS_PENDING = "pending"
        const val STATUS_SUBMITTED = "submitted"
        const val STATUS_QUEUED = "queued"
        const val STATUS_NEEDS_CORRECTION = "needs_correction"

        const val VISIT_AGREEMENT = "agreement"
        const val VISIT_NO_AGREEMENT = "no_agreement"
    }
}

data class MilestoneDefinition(
    val key: String,
    val titleRes: Int,
    val hintRes: Int,
    val iconRes: Int,
    val inputs: List<Int>,
)

object MilestoneCatalog {
    val all = listOf(
        MilestoneDefinition(
            key = MilestoneState.KEY_PLANTING,
            titleRes = R.string.publish_plan_planting,
            hintRes = R.string.evidence_hint_planting,
            iconRes = R.drawable.payment04,
            inputs = listOf(R.string.evidence_input_seed, R.string.evidence_input_compost),
        ),
        MilestoneDefinition(
            key = MilestoneState.KEY_IRRIGATION,
            titleRes = R.string.publish_plan_irrigation,
            hintRes = R.string.evidence_hint_irrigation,
            iconRes = R.drawable.payment02,
            inputs = listOf(R.string.evidence_input_water, R.string.evidence_input_drip),
        ),
        MilestoneDefinition(
            key = MilestoneState.KEY_FERTILIZATION,
            titleRes = R.string.publish_plan_fertilization,
            hintRes = R.string.evidence_hint_fertilization,
            iconRes = R.drawable.payment01,
            inputs = listOf(R.string.evidence_input_npk, R.string.evidence_input_compost, R.string.evidence_input_urea),
        ),
        MilestoneDefinition(
            key = MilestoneState.KEY_HARVEST,
            titleRes = R.string.publish_plan_harvest,
            hintRes = R.string.evidence_hint_harvest,
            iconRes = R.drawable.payment03,
            inputs = listOf(R.string.evidence_input_crates, R.string.evidence_input_sacks),
        ),
    )

    fun definition(key: String): MilestoneDefinition =
        all.firstOrNull { it.key == key } ?: all[2]

    fun currentIndex(stage: String, progress: Int): Int {
        val value = stage.lowercase()
        return when {
            "harvest" in value -> 3
            "fertil" in value -> 2
            "irrig" in value -> 1
            "plant" in value || "sow" in value -> 0
            progress >= 75 -> 3
            progress >= 50 -> 2
            progress >= 25 -> 1
            else -> 0
        }
    }

    fun seed(stage: String, progress: Int): List<MilestoneState> {
        val current = currentIndex(stage, progress)
        return all.mapIndexed { index, item ->
            MilestoneState(
                key = item.key,
                status = if (index < current) MilestoneState.STATUS_APPROVED else MilestoneState.STATUS_PENDING,
                dueLabel = "",
            )
        }
    }
}