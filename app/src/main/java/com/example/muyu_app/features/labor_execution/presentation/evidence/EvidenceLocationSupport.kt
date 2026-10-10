package com.example.muyu_app.features.labor_execution.presentation.evidence

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import com.example.muyu_app.features.plot_approval.domain.Plot
import java.io.File
import java.io.FileOutputStream

/** Maximum distance from plot GPS for evidence to count as on-plot. */
const val PLOT_MATCH_METERS = 100f

fun plotHasReferenceGps(plot: Plot): Boolean =
    plot.latitude != null && plot.longitude != null

fun distanceToPlotMeters(
    latitude: Double,
    longitude: Double,
    plot: Plot,
): Float? {
    val plotLat = plot.latitude ?: return null
    val plotLng = plot.longitude ?: return null
    val result = FloatArray(1)
    Location.distanceBetween(latitude, longitude, plotLat, plotLng, result)
    return result[0]
}

/** True when the device fix is close enough to the plot GPS (100 m + GPS accuracy buffer). */
fun isEvidenceLocationValid(
    userLat: Double,
    userLng: Double,
    plot: Plot,
    userAccuracyMeters: Int? = null,
): Boolean {
    if (!plotHasReferenceGps(plot)) return true
    val distance = distanceToPlotMeters(userLat, userLng, plot) ?: return true
    val accuracyBuffer = (userAccuracyMeters ?: plot.accuracyMeters ?: 0)
        .coerceIn(0, 100)
        .toFloat()
    return distance <= PLOT_MATCH_METERS + accuracyBuffer
}

fun saveEvidenceBitmap(context: Context, bitmap: Bitmap): String {
    val folder = File(context.filesDir, "evidence").apply { mkdirs() }
    val file = File(folder, "evidence_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    }
    return file.toURI().toString()
}
