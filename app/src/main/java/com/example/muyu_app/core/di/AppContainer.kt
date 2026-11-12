package com.example.muyu_app.core.di

import android.content.Context
import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.muyu_app.core.i18n.LanguagePreferences
import com.example.muyu_app.core.network.BackendSnapshot
import com.example.muyu_app.core.network.BackendStateStore
import com.example.muyu_app.core.network.MobileStateService
import com.example.muyu_app.core.network.isSyncableBy
import com.example.muyu_app.features.plot_approval.application.CreatePlotUseCase
import com.example.muyu_app.features.plot_approval.application.DeletePlotUseCase
import com.example.muyu_app.features.plot_financing.application.FundPlotUseCase
import com.example.muyu_app.features.plot_approval.application.GetPlotsUseCase
import com.example.muyu_app.features.plot_approval.application.UpdatePlotUseCase
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotDraft
import com.example.muyu_app.features.plot_approval.domain.PlotVisit
import com.example.muyu_app.features.plot_approval.domain.PlotVisitRepository
import com.example.muyu_app.features.plot_approval.infrastructure.repositories.PlotRepositoryImpl
import com.example.muyu_app.features.plot_approval.infrastructure.repositories.PlotVisitRepositoryImpl
import com.example.muyu_app.features.user_management.application.LoginUseCase
import com.example.muyu_app.features.user_management.application.RegisterUseCase
import com.example.muyu_app.features.user_management.presentation.incident.IncidentDraft
import com.example.muyu_app.features.user_management.presentation.profile.SavedBankCard
import com.example.muyu_app.features.user_management.presentation.profile.DigitalWalletAccount
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.muyu_app.features.user_management.infrastructure.remote.AuthService
import com.example.muyu_app.features.user_management.infrastructure.remote.ChangePasswordRequestDto
import com.example.muyu_app.features.user_management.infrastructure.repositories.AuthRepositoryImpl
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.google.gson.Gson
import com.example.muyu_app.core.storage.BackendPreferences
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Response

private const val SYNC_LOG_TAG = "MuyuSync"

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

// Carries the cross-role visit proposal until the remote API exposes visit endpoints.
data class MerchantVisitRequest(
    val id: Int = 0,
    val plotId: Int = 0,
    val plotName: String = "",
    val plotPlace: String = "",
    val farmerEmail: String = "",
    val farmerName: String = "",
    val merchantName: String = "",
    val date: String = "28/08/2026",
    val time: String = "10:00",
    val message: String = "",
    val confirmed: Boolean = false,
)

class AppContainer(
    context: Context? = null,
) {
    // Runtime cache only. The backend is the sole persistent source of truth.
    val backendStore = BackendStateStore()
    val plotDraft = PlotDraft()
    val incidentDraft = IncidentDraft()
    private val gson = Gson()
    private val networkScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()
    val userPreferences = BackendPreferences(backendStore)

    // La tarjeta pertenece al usuario de la sesión. Una cuenta nueva empieza sin tarjeta.
    private var bankCardVersion by mutableIntStateOf(0)
    private val bankCardCache = mutableMapOf<String, SavedBankCard?>()
    var savedBankCard: SavedBankCard?
        get() {
            bankCardVersion
            val email = currentUserEmail() ?: return null
            return bankCardCache.getOrPut(email) {
                backendStore.preferences[preferenceKey(email, KEY_BANK_CARD)]
                    ?.takeIf(String::isNotBlank)
                    ?.let { gson.fromJson(it, SavedBankCard::class.java) }
            }
        }
        set(value) {
            val email = currentUserEmail() ?: return
            bankCardCache[email] = value
            val key = preferenceKey(email, KEY_BANK_CARD)
            if (value == null) backendStore.preferences.remove(key)
            else backendStore.preferences[key] = gson.toJson(value)
            backendStore.persist()
            bankCardVersion++
        }

    fun currentUserEmail(): String? =
        backendStore.currentUser?.email?.trim()?.lowercase()?.takeIf { it.isNotBlank() }

    // Yape y Plin del agricultor: se guardan por usuario y una cuenta nueva empieza sin billeteras.
    private var walletVersion by mutableIntStateOf(0)
    var yapeAccount: DigitalWalletAccount?
        get() = loadWallet(KEY_YAPE)
        set(value) = saveWallet(KEY_YAPE, value)
    var plinAccount: DigitalWalletAccount?
        get() = loadWallet(KEY_PLIN)
        set(value) = saveWallet(KEY_PLIN, value)

    private fun loadWallet(name: String): DigitalWalletAccount? {
        walletVersion
        val email = currentUserEmail() ?: return null
        val parts = backendStore.preferences[preferenceKey(email, name)].orEmpty().split(WALLET_SEPARATOR)
        val phone = parts.getOrNull(0).orEmpty()
        if (phone.isBlank()) return null
        return DigitalWalletAccount(
            phone = phone,
            holderName = parts.getOrNull(1).orEmpty(),
            isPrimary = parts.getOrNull(2) == "1",
            isVerified = parts.getOrNull(3) == "1",
        )
    }

    private fun saveWallet(name: String, account: DigitalWalletAccount?) {
        val email = currentUserEmail() ?: return
        val value = account?.let {
            listOf(it.phone, it.holderName, if (it.isPrimary) "1" else "0", if (it.isVerified) "1" else "0")
                .joinToString(WALLET_SEPARATOR)
        }.orEmpty()
        val key = preferenceKey(email, name)
        if (value.isBlank()) backendStore.preferences.remove(key) else backendStore.preferences[key] = value
        backendStore.persist()
        walletVersion++
    }
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
    // Visita que el agricultor abrió para registrar o ver su resultado.
    var selectedVisit: PlotVisit? = null
    // Método con el que el comerciante confirmó el último financiamiento.
    var fundingMethodLabel: String = ""
    // Vuelve al paso de pago después de registrar una tarjeta desde el financiamiento.
    var resumeFundingAtPayment: Boolean = false
    var resumeFundingMethod: String = ""

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

    private val retrofit = NetworkModule.provideRetrofit(backendStore)

    val authService: AuthService = retrofit.create(AuthService::class.java)
    private val mobileStateService: MobileStateService = retrofit.create(MobileStateService::class.java)

    private val authRepository = AuthRepositoryImpl(authService, backendStore, mobileStateService)
    private val plotRepository = PlotRepositoryImpl(backendStore, mobileStateService)

    init {
        backendStore.onPersist = { snapshot ->
            if (!backendStore.token.isNullOrBlank()) {
                val email = backendStore.currentUser?.email.orEmpty()
                networkScope.launch {
                    syncMutex.withLock { pushSnapshot(snapshot, email) }
                }
            }
        }
    }

    private suspend fun pushSnapshot(snapshot: BackendSnapshot, email: String) {
        val privateSnapshot = snapshot.copy(plots = emptyList(), visits = emptyList())
        pushRecord("Backend state") { mobileStateService.putState(privateSnapshot) }
        snapshot.plots.filter { it.isSyncableBy(email) }.forEach { plot ->
            pushRecord("Plot ${plot.id}") { mobileStateService.putPlot(plot.id, plot) }
        }
        snapshot.visits.filter { it.isSyncableBy(email) }.forEach { visit ->
            pushRecord("Visit ${visit.id}") { mobileStateService.putVisit(visit.id, visit) }
        }
    }

    /** A rejected or failed background sync must never close the app. */
    private suspend fun pushRecord(label: String, send: suspend () -> Response<Unit>) {
        try {
            val response = send()
            if (!response.isSuccessful) Log.w(SYNC_LOG_TAG, "$label sync failed: ${response.code()}")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(SYNC_LOG_TAG, "$label sync failed", e)
        }
    }

    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val getPlotsUseCase = GetPlotsUseCase(plotRepository)
    val createPlotUseCase = CreatePlotUseCase(plotRepository)
    val updatePlotUseCase = UpdatePlotUseCase(plotRepository)
    val fundPlotUseCase = FundPlotUseCase(plotRepository)
    val deletePlotUseCase = DeletePlotUseCase(plotRepository)
    val plotVisits: PlotVisitRepository = PlotVisitRepositoryImpl(backendStore)

    suspend fun syncPlot(plot: Plot): Result<Plot> = plotRepository.syncPlot(plot)

    suspend fun fetchRemotePlot(id: Int): Result<Plot?> = plotRepository.fetchRemotePlot(id)

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = runCatching {
        val response = authService.changePassword(ChangePasswordRequestDto(currentPassword, newPassword))
        if (!response.isSuccessful) {
            throw IllegalStateException(response.errorBody()?.string() ?: "Password change failed (${response.code()})")
        }
    }

    fun clearSession() {
        backendStore.endSession()
        selectedPlot = null
        selectedVisit = null
        // Limpia los destinos anteriores para no heredarlos en otra sesión.
        merchantEvidenceOrigin = MerchantEvidenceOrigin.Alerts
        merchantPlotReturn = MerchantPlotReturn.Catalog
        farmerPlotReturn = FarmerPlotReturn.Home
        selectedMilestoneKey = "fertilization"
        rejectionReason = ""
        rejectionCorrection = ""
        reviewCompletedAt = 0L
        // Visit requests model server-owned data and must survive account switching in the demo flow.
        plotDraft.clear()
        incidentDraft.clear()
    }
}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer was not provided")
}

private const val KEY_YAPE = "farmer_yape"
private const val KEY_PLIN = "farmer_plin"
private const val KEY_BANK_CARD = "merchant_bank_card"
private const val WALLET_SEPARATOR = "\u001F"

private fun preferenceKey(email: String, name: String): String = "${email.lowercase()}:$name"
