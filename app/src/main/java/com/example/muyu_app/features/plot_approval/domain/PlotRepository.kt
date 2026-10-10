package com.example.muyu_app.features.plot_approval.domain

interface PlotRepository {
    suspend fun getPlots(): Result<List<Plot>>
    suspend fun createPlot(plot: NewPlot): Result<Plot>
    suspend fun updatePlot(plotId: Int, plot: NewPlot): Result<Plot>
    suspend fun fundPlot(id: Int): Result<Plot>
}
