package com.example.muyu_app.core.network

import android.content.Context
import com.example.muyu_app.core.environment.Environment as MuyuEnvironment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

data class SyncHistoryRecord(
    val ownerEmail: String = "",
    val plotId: Int = 0,
    val plotName: String = "",
    val milestoneKey: String = "",
    val queuedAtMillis: Long = 0L,
    val syncedAtMillis: Long = 0L,
)

data class FakeApiSnapshot(
    val users: List<UserRecord> = emptyList(),
    val plots: List<PlotRecord> = emptyList(),
    val merchantNotifications: List<MerchantNotificationRecord> = emptyList(),
    val syncHistory: List<SyncHistoryRecord> = emptyList(),
)

class FakeApiPersistence(
    private val context: Context,
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create(),
) {
    private val runtimeFile = File(context.filesDir, FILE_NAME)

    fun load(): FakeApiSnapshot? {
        readFile(runtimeFile)?.let { return it }
        return readAssets()?.also { snapshot ->
            save(snapshot.users, snapshot.plots, snapshot.merchantNotifications, snapshot.syncHistory)
        }
    }

    fun shouldApplyPlotReset(): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val applied = prefs.getInt(KEY_PLOT_RESET_GENERATION, 0)
        return MuyuEnvironment.PLOT_RESET_GENERATION > applied
    }

    fun markPlotResetApplied() {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_PLOT_RESET_GENERATION, MuyuEnvironment.PLOT_RESET_GENERATION)
            .apply()
    }

    fun loadSession(): Pair<String, String>? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val email = prefs.getString(KEY_SESSION_EMAIL, null)?.takeIf { it.isNotBlank() } ?: return null
        val token = prefs.getString(KEY_SESSION_TOKEN, null)?.takeIf { it.isNotBlank() } ?: return null
        return email to token
    }

    fun saveSession(email: String, token: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SESSION_EMAIL, email)
            .putString(KEY_SESSION_TOKEN, token)
            .apply()
    }

    fun clearSession() {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_SESSION_EMAIL)
            .remove(KEY_SESSION_TOKEN)
            .apply()
    }

    fun save(
        users: List<UserRecord>,
        plots: List<PlotRecord>,
        merchantNotifications: List<MerchantNotificationRecord> = emptyList(),
        syncHistory: List<SyncHistoryRecord> = emptyList(),
    ) {
        val json = gson.toJson(FakeApiSnapshot(users, plots, merchantNotifications, syncHistory))
        runCatching {
            runtimeFile.parentFile?.mkdirs()
            runtimeFile.writeText(json)
        }
    }

    private fun readAssets(): FakeApiSnapshot? = runCatching {
        context.assets.open(FILE_NAME).bufferedReader().use { reader ->
            gson.fromJson(reader, FakeApiSnapshot::class.java)
        }
    }.getOrNull()

    private fun readFile(file: File): FakeApiSnapshot? {
        if (!file.exists()) return null
        return runCatching {
            gson.fromJson(file.readText(), FakeApiSnapshot::class.java)
        }.getOrNull()
    }

    companion object {
        const val FILE_NAME = "muyu_fake_api.json"
        private const val PREFS_NAME = "muyu_fake_api_prefs"
        private const val KEY_PLOT_RESET_GENERATION = "plot_reset_generation"
        private const val KEY_SESSION_EMAIL = "session_email"
        private const val KEY_SESSION_TOKEN = "session_token"
    }
}
