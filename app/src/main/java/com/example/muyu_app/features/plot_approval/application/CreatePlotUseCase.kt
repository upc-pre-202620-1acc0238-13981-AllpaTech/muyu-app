package com.example.muyu_app.features.plot_approval.application

import com.example.muyu_app.features.plot_approval.domain.NewPlot
import com.example.muyu_app.features.plot_approval.domain.PlotRepository

class CreatePlotUseCase(private val repository: PlotRepository) {
    suspend operator fun invoke(plot: NewPlot) = repository.createPlot(plot)
}
