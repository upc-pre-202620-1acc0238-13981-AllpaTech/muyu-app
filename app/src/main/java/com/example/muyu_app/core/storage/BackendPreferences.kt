package com.example.muyu_app.core.storage

import com.example.muyu_app.core.network.BackendStateStore

/**
 * Preference-shaped adapter backed by the authenticated server state.
 * It intentionally performs no Android file or SharedPreferences writes.
 */
class BackendPreferences(private val store: BackendStateStore) {
    fun loadString(email: String, key: String, defaultValue: String): String =
        store.preferences[compoundKey(email, key)] ?: defaultValue

    fun saveString(email: String, key: String, value: String) {
        val storageKey = compoundKey(email, key)
        if (value.isBlank()) store.preferences.remove(storageKey) else store.preferences[storageKey] = value
        store.persist()
    }

    fun loadBoolean(email: String, key: String, defaultValue: Boolean): Boolean =
        store.preferences[compoundKey(email, key)]?.toBooleanStrictOrNull() ?: defaultValue

    fun saveBoolean(email: String, key: String, value: Boolean) {
        store.preferences[compoundKey(email, key)] = value.toString()
        store.persist()
    }

    private fun compoundKey(email: String, key: String): String = "${email.trim().lowercase()}:$key"

    companion object {
        const val KEY_ALERT_CHANNEL = "alert_channel"
        const val KEY_SYNC_WIFI_ONLY = "sync_wifi_only"
        const val KEY_NEW_PLOT_ALERTS = "new_plot_alerts"
        const val KEY_LAST_ROLE = "last_role"
    }
}
