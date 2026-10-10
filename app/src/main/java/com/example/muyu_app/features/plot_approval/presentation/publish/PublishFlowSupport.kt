package com.example.muyu_app.features.plot_approval.presentation.publish

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.muyu_app.R
import com.example.muyu_app.features.plot_approval.domain.PlotDraft
import com.example.muyu_app.features.plot_approval.domain.PlotFinance

@Composable
fun publishFlowTitle(draft: PlotDraft): String =
    if (draft.isEditing) {
        stringResource(R.string.publish_edit_plot)
    } else {
        stringResource(R.string.publish_title)
    }

fun cycleLabelToRes(cycle: String): Int = when {
    cycle.contains("3", ignoreCase = true) -> R.string.publish_cycle_3
    cycle.contains("9", ignoreCase = true) -> R.string.publish_cycle_9
    cycle.contains("12", ignoreCase = true) -> R.string.publish_cycle_12
    else -> R.string.publish_cycle_6
}

fun draftAmountDisplay(amount: String): String {
    if (amount.isBlank()) return PlotFinance.formatAmount(8500.0)
    return if (amount.contains("S/", ignoreCase = true)) amount else PlotFinance.formatAmount(PlotFinance.parseAmount(amount))
}
