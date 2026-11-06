package com.example.muyu_app.features.plot_financing.presentation.fund

import com.example.muyu_app.core.di.AppContainer

/** Pago que el procesador no confirmó ni rechazó a tiempo (US35). */
data class PendingFunding(
    val methodLabel: String,
    val startedAt: String,
)

/** Resultado de volver a consultar un pago en verificación. */
enum class FundingCheck { Funded, TakenByOther, NotPaid, StillUnknown }

private const val SEPARATOR = "\u001F"

private fun pendingKey(plotId: Int) = "funding_verification_$plotId"

fun AppContainer.pendingFunding(plotId: Int): PendingFunding? {
    val email = currentUserEmail() ?: return null
    val raw = userPreferences?.loadString(email, pendingKey(plotId), "").orEmpty()
    if (raw.isBlank()) return null
    val parts = raw.split(SEPARATOR)
    return PendingFunding(methodLabel = parts.getOrElse(0) { "" }, startedAt = parts.getOrElse(1) { "" })
}

fun AppContainer.savePendingFunding(plotId: Int, pending: PendingFunding) {
    val email = currentUserEmail() ?: return
    userPreferences?.saveString(
        email,
        pendingKey(plotId),
        listOf(pending.methodLabel, pending.startedAt).joinToString(SEPARATOR),
    )
}

fun AppContainer.clearPendingFunding(plotId: Int) {
    val email = currentUserEmail() ?: return
    userPreferences?.saveString(email, pendingKey(plotId), "")
}

/**
 * Pregunta al servidor si el pago se registró. No crea un nuevo intento de pago,
 * así se evita cobrar dos veces la misma orden.
 */
suspend fun AppContainer.checkPendingFunding(plotId: Int): FundingCheck {
    val me = currentUserEmail().orEmpty()
    val remote = fetchRemotePlot(plotId).getOrElse { return FundingCheck.StillUnknown }
        ?: return FundingCheck.NotPaid.also { clearPendingFunding(plotId) }
    val result = when {
        remote.status == "funded" && remote.fundedByEmail.equals(me, ignoreCase = true) -> FundingCheck.Funded
        remote.status == "funded" -> FundingCheck.TakenByOther
        else -> FundingCheck.NotPaid
    }
    clearPendingFunding(plotId)
    if (result == FundingCheck.Funded) selectedPlot = remote
    return result
}
