package com.example.muyu_app.features.plot_approval.application

import com.example.muyu_app.features.plot_approval.domain.PlotRepository

class DeletePlotUseCase(
    private val repository: PlotRepository,
) {
    suspend operator fun invoke(plotId: Int) = repository.deletePlot(plotId)
}
