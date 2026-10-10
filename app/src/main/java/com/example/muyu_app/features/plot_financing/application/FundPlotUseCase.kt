package com.example.muyu_app.features.plot_financing.application

import com.example.muyu_app.features.plot_approval.domain.PlotRepository

class FundPlotUseCase(private val repository: PlotRepository) {
    suspend operator fun invoke(id: Int) = repository.fundPlot(id)
}