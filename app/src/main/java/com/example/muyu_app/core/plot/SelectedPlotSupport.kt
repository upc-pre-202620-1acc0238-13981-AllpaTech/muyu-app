package com.example.muyu_app.core.plot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.features.plot_approval.domain.Plot

fun AppContainer.liveFundedPlot(): Plot? {
    val id = selectedPlot?.id ?: return selectedPlot
    val record = fakeStore.ensureMilestones(id) ?: return selectedPlot
    val domain = fakeStore.toDomain(record)
    selectedPlot = domain
    return domain
}

fun AppContainer.refreshSelectedPlot() {
    val id = selectedPlot?.id ?: return
    val record = fakeStore.ensureMilestones(id) ?: fakeStore.plotById(id) ?: return
    selectedPlot = fakeStore.toDomain(record)
}

@Composable
fun rememberLiveFundedPlot(): Plot? {
    val container = LocalAppContainer.current
    var plot by remember { mutableStateOf(container.liveFundedPlot()) }
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    LaunchedEffect(
        lifecycleState.value,
        container.selectedPlot?.id,
        container.selectedMilestoneKey,
    ) {
        if (lifecycleState.value.isAtLeast(Lifecycle.State.RESUMED)) {
            plot = container.liveFundedPlot()
        }
    }
    return plot
}

fun AppContainer.merchantDisplayName(plot: Plot?): String {
    val email = plot?.fundedByEmail.orEmpty()
    if (email.isBlank()) return "AgroAndes"
    return fakeStore.users.find { it.email.equals(email, ignoreCase = true) }?.displayName
        ?.ifBlank { null }
        ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }
}
