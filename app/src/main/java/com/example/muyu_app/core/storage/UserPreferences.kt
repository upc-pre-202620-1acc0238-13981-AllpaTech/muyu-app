package com.example.muyu_app.core.storage

import android.content.Context
import com.example.muyu_app.features.user_management.presentation.profile.SavedBankCard
import com.google.gson.Gson

/**
 * Preferencias que pertenecen a cada usuario del teléfono. Se guardan por correo
 * para que dos cuentas en el mismo dispositivo no compartan tarjeta ni avisos.
 */
class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun loadBankCard(email: String): SavedBankCard? =
        prefs.getString(key(email, KEY_BANK_CARD), null)
            ?.let { runCatching { gson.fromJson(it, SavedBankCard::class.java) }.getOrNull() }

    fun saveBankCard(email: String, card: SavedBankCard?) {
        prefs.edit().apply {
            if (card == null) remove(key(email, KEY_BANK_CARD)) else putString(key(email, KEY_BANK_CARD), gson.toJson(card))
        }.apply()
    }

    fun loadString(email: String, name: String, default: String): String =
        prefs.getString(key(email, name), null) ?: default

    fun saveString(email: String, name: String, value: String) {
        prefs.edit().putString(key(email, name), value).apply()
    }

    fun loadBoolean(email: String, name: String, default: Boolean): Boolean =
        prefs.getBoolean(key(email, name), default)

    fun saveBoolean(email: String, name: String, value: Boolean) {
        prefs.edit().putBoolean(key(email, name), value).apply()
    }

    private fun key(email: String, name: String) = "${email.trim().lowercase()}::$name"

    companion object {
        private const val PREFS_NAME = "muyu_user_preferences"
        private const val KEY_BANK_CARD = "bank_card"
        const val KEY_ALERT_CHANNEL = "alert_channel"
        const val KEY_SYNC_WIFI_ONLY = "sync_wifi_only"
        const val KEY_NEW_PLOT_ALERTS = "new_plot_alerts"
        const val KEY_LAST_ROLE = "last_role"
    }
}
