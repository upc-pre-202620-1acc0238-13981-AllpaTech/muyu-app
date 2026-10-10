package com.example.muyu_app.core.environment

/**
 * Same idea as the professor's NetworkModule baseUrl, but isolated so you can
 * switch fake local API vs a real remote API without touching Retrofit callers.
 *
 * The deployed fake API is used through Retrofit. Set [USE_FAKE_API] to true
 * only when the app must run with the in-device interceptor and no network.
 */
object Environment {
    const val USE_FAKE_API = false

    const val FAKE_BASE_URL = "https://muyu.local/api/"
    const val REMOTE_BASE_URL = "https://muyu-fake-api.onrender.com/api/"

    val baseUrl: String
        get() = if (USE_FAKE_API) FAKE_BASE_URL else REMOTE_BASE_URL

    object DemoCredentials {
        const val FARMER_EMAIL = "maria.quispe@muyu.pe"
        const val MERCHANT_EMAIL = "jose.arturo@muyu.pe"
        const val PASSWORD = "Muyu1234"
    }

    /** Bump to wipe persisted plots/notifications once (users unchanged). */
    const val PLOT_RESET_GENERATION = 1
}
