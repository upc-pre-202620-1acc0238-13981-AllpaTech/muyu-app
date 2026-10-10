package com.example.muyu_app.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

data class ResolvedPlace(
    val region: String,
    val province: String,
    val district: String,
    val address: String,
)

fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
}

@SuppressLint("MissingPermission")
fun bestLastKnownLocation(context: Context): Location? {
    if (!hasLocationPermission(context)) return null
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        .filter { runCatching { manager.isProviderEnabled(it) }.getOrDefault(false) }
        .mapNotNull { runCatching { manager.getLastKnownLocation(it) }.getOrNull() }
        .maxByOrNull { it.time }
}

fun requestCurrentLocation(
    context: Context,
    onResult: (Location) -> Unit,
    onError: () -> Unit,
) {
    requestCurrentLocation(
        context = context,
        preferCachedMaxAgeMs = 60_000L,
        timeoutMs = 10_000L,
        onResult = onResult,
        onError = onError,
    )
}

fun requestCurrentLocationQuick(
    context: Context,
    onResult: (Location) -> Unit,
    onError: () -> Unit,
) {
    val last = bestLastKnownLocation(context)
    if (last != null && System.currentTimeMillis() - last.time < 120_000L) {
        deliverOnMain { onResult(last) }
        return
    }
    requestCurrentLocation(
        context = context,
        preferCachedMaxAgeMs = Long.MAX_VALUE,
        timeoutMs = 4_000L,
        onResult = onResult,
        onError = onError,
    )
}

@SuppressLint("MissingPermission")
fun requestCurrentLocation(
    context: Context,
    preferCachedMaxAgeMs: Long,
    timeoutMs: Long,
    onResult: (Location) -> Unit,
    onError: () -> Unit,
) {
    if (!hasLocationPermission(context)) {
        deliverOnMain(onError)
        return
    }
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val last = bestLastKnownLocation(context)
    if (last != null && System.currentTimeMillis() - last.time < preferCachedMaxAgeMs) {
        deliverOnMain { onResult(last) }
        return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && last == null) {
        val provider = LocationManager.FUSED_PROVIDER
        if (runCatching { manager.isProviderEnabled(provider) }.getOrDefault(false)) {
            val delivered = AtomicBoolean(false)
            val handler = Handler(Looper.getMainLooper())
            val timeout = Runnable {
                if (delivered.compareAndSet(false, true)) {
                    deliverOnMain {
                        bestLastKnownLocation(context)?.let(onResult) ?: onError()
                    }
                }
            }
            handler.postDelayed(timeout, timeoutMs)
            runCatching {
                manager.getCurrentLocation(
                    provider,
                    null,
                    context.mainExecutor,
                ) { location ->
                    if (location != null && delivered.compareAndSet(false, true)) {
                        handler.removeCallbacks(timeout)
                        deliverOnMain { onResult(location) }
                    }
                }
            }.onFailure {
                handler.removeCallbacks(timeout)
                deliverOnMain {
                    bestLastKnownLocation(context)?.let(onResult) ?: onError()
                }
            }
            return
        }
    }

    val delivered = AtomicBoolean(false)
    val handler = Handler(Looper.getMainLooper())
    lateinit var timeoutRunnable: Runnable

    val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (delivered.compareAndSet(false, true)) {
                handler.removeCallbacks(timeoutRunnable)
                runCatching { manager.removeUpdates(this) }
                deliverOnMain { onResult(location) }
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        override fun onProviderEnabled(provider: String) = Unit
        override fun onProviderDisabled(provider: String) = Unit
    }

    timeoutRunnable = Runnable {
        if (delivered.compareAndSet(false, true)) {
            runCatching { manager.removeUpdates(listener) }
            deliverOnMain {
                bestLastKnownLocation(context)?.let(onResult) ?: onError()
            }
        }
    }

    handler.postDelayed(timeoutRunnable, timeoutMs)
    val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        .filter { runCatching { manager.isProviderEnabled(it) }.getOrDefault(false) }

    if (providers.isEmpty()) {
        handler.removeCallbacks(timeoutRunnable)
        deliverOnMain {
            bestLastKnownLocation(context)?.let(onResult) ?: onError()
        }
        return
    }

    providers.forEach { provider ->
        runCatching {
            manager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
        }
    }
}

private val mainHandler = Handler(Looper.getMainLooper())

private fun deliverOnMain(block: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        block()
    } else {
        mainHandler.post(block)
    }
}

fun reverseGeocode(context: Context, latitude: Double, longitude: Double): ResolvedPlace? {
    if (!Geocoder.isPresent()) return null
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Locale.Builder().setLanguage("es").setRegion("PE").build()
    } else {
        @Suppress("DEPRECATION")
        Locale("es", "PE")
    }
    val geocoder = Geocoder(context, locale)
    val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val latch = CountDownLatch(1)
        val found = AtomicReference<android.location.Address?>(null)
        geocoder.getFromLocation(
            latitude,
            longitude,
            1,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<android.location.Address>) {
                    found.set(addresses.firstOrNull())
                    latch.countDown()
                }

                override fun onError(errorMessage: String?) {
                    latch.countDown()
                }
            },
        )
        latch.await(4, TimeUnit.SECONDS)
        found.get()
    } else {
        @Suppress("DEPRECATION")
        geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
    }
    val resolved = address ?: return null
    val region = resolved.adminArea.orEmpty()
    val province = resolved.subAdminArea.orEmpty().ifBlank { region }
    val district = resolved.locality.orEmpty()
        .ifBlank { resolved.subLocality.orEmpty() }
        .ifBlank { province }
    val street = listOfNotNull(resolved.thoroughfare, resolved.featureName)
        .firstOrNull { it.isNotBlank() }
        .orEmpty()
    return ResolvedPlace(region = region, province = province, district = district, address = street)
}