package com.example.muyu_app.core.di

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.muyu_app.core.i18n.LanguagePreferences
import com.example.muyu_app.core.network.FakeApiPersistence
import com.example.muyu_app.core.network.FakeApiStore
import com.example.muyu_app.features.plot_approval.application.CreatePlotUseCase
import com.example.muyu_app.features.plot_financing.application.FundPlotUseCase
import com.example.muyu_app.features.plot_approval.application.GetPlotsUseCase
import com.example.muyu_app.features.plot_approval.application.UpdatePlotUseCase
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotDraft
import com.example.muyu_app.features.plot_approval.infrastructure.remote.PlotService
import com.example.muyu_app.features.plot_approval.infrastructure.repositories.PlotRepositoryImpl
import com.example.muyu_app.features.user_management.application.LoginUseCase
import com.example.muyu_app.features.user_management.application.RegisterUseCase
import com.example.muyu_app.features.user_management.infrastructure.remote.AuthService
import com.example.muyu_app.features.user_management.infrastructure.repositories.AuthRepositoryImpl
import com.example.muyu_app.core.designsystem.components.MerchantTab

enum class MerchantEvidenceOrigin {
    Alerts,
    MyPlots,
    PlotDetail,
}

enum class FarmerPlotReturn {
    Home,
    MyPlots,
}

enum class MerchantPlotReturn {
    Catalog,
    MyPlots,
    Alerts,
}

data class MerchantVisitRequest(
    val date: String = "28/08/2026",
    val time: String = "10:00",
    val message: String = "",
    val confirmed: Boolean = false,
)

class AppContainer(
    context: Context? = null,
) {
    val fakeStore = FakeApiStore(context?.let { FakeApiPersistence(it) })
    val plotDraft = PlotDraft()
    val languages = LanguagePreferences()
    var selectedPlot: Plot? = null
    var selectedMilestoneKey: String = "fertilization"
    var merchantEvidenceOrigin: MerchantEvidenceOrigin = MerchantEvidenceOrigin.Alerts
    var farmerPlotReturn: FarmerPlotReturn = FarmerPlotReturn.Home
    var merchantPlotReturn: MerchantPlotReturn = MerchantPlotReturn.Catalog
    var rejectionReason: String = ""
    var rejectionCorrection: String = ""
    var reviewCompletedAt: Long = 0L
    var merchantVisitRequest: MerchantVisitRequest? = null

    // Mantiene la pestaña desde la que se abrió el detalle de la parcela.
    fun merchantEvidenceBottomTab(): MerchantTab = when (merchantEvidenceOrigin) {
        MerchantEvidenceOrigin.MyPlots -> MerchantTab.MyPlots
        MerchantEvidenceOrigin.Alerts -> MerchantTab.Alerts
        MerchantEvidenceOrigin.PlotDetail -> when (merchantPlotReturn) {
            MerchantPlotReturn.Catalog -> MerchantTab.Catalog
            MerchantPlotReturn.MyPlots -> MerchantTab.MyPlots
            MerchantPlotReturn.Alerts -> MerchantTab.Alerts
        }
    }

    private val retrofit = NetworkModule.provideRetrofit(fakeStore)

    val authService: AuthService = retrofit.create(AuthService::class.java)
    val plotService: PlotService = retrofit.create(PlotService::class.java)

    private val authRepository = AuthRepositoryImpl(authService, fakeStore)
    private val plotRepository = PlotRepositoryImpl(plotService, fakeStore)

    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val getPlotsUseCase = GetPlotsUseCase(plotRepository)
    val createPlotUseCase = CreatePlotUseCase(plotRepository)
    val updatePlotUseCase = UpdatePlotUseCase(plotRepository)
    val fundPlotUseCase = FundPlotUseCase(plotRepository)

    suspend fun syncPlot(plot: Plot): Result<Plot> = plotRepository.syncPlot(plot)

    fun clearSession() {
        fakeStore.endSession()
        selectedPlot = null
        // Limpia los destinos anteriores para no heredarlos en otra sesión.
        merchantEvidenceOrigin = MerchantEvidenceOrigin.Alerts
        merchantPlotReturn = MerchantPlotReturn.Catalog
        farmerPlotReturn = FarmerPlotReturn.Home
        selectedMilestoneKey = "fertilization"
        rejectionReason = ""
        rejectionCorrection = ""
        reviewCompletedAt = 0L
        merchantVisitRequest = null
        plotDraft.clear()
    }
}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer was not provided")
}
