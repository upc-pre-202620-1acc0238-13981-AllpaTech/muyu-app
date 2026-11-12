package com.example.muyu_app.core.environment

/**
 * Same idea as the professor's NetworkModule baseUrl, but isolated so you can
 * switch local development and deployed backend URLs without touching callers.
 */
object Environment {
    const val BACKEND_BASE_URL = "https://muyu-backend-production.up.railway.app/api/v1/"

    val baseUrl: String
        get() = BACKEND_BASE_URL

    object DemoCredentials {
        const val FARMER_EMAIL = "maria.quispe@muyu.pe"
        const val MERCHANT_EMAIL = "jose.arturo@muyu.pe"
        const val PASSWORD = "Muyu1234"
    }

}
