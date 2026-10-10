package com.example.muyu_app.core.network

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import com.example.muyu_app.core.environment.Environment as MuyuEnvironment
import android.provider.MediaStore
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
        runCatching { savePublicCopy(json) }
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

    private fun savePublicCopy(json: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val existingUri = resolver.query(
                collection,
                arrayOf(MediaStore.Downloads._ID),
                "${MediaStore.Downloads.DISPLAY_NAME}=?",
                arrayOf(FILE_NAME),
                null,
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    ContentUris.withAppendedId(collection, cursor.getLong(0))
                } else {
                    null
                }
            }
            val uri = existingUri ?: resolver.insert(
                collection,
                ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, FILE_NAME)
                    put(MediaStore.Downloads.MIME_TYPE, "application/json")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                },
            ) ?: return
            resolver.openOutputStream(uri, "wt")?.use { stream ->
                stream.write(json.toByteArray(Charsets.UTF_8))
            }
        } else {
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloads.mkdirs()
            File(downloads, FILE_NAME).writeText(json)
        }
    }

    companion object {
        const val FILE_NAME = "muyu_fake_api.json"
        private const val PREFS_NAME = "muyu_fake_api_prefs"
        private const val KEY_PLOT_RESET_GENERATION = "plot_reset_generation"
    }
}
