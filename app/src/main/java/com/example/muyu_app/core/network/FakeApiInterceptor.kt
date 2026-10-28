package com.example.muyu_app.core.network

import com.example.muyu_app.core.environment.Environment
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class FakeApiInterceptor(
    private val store: FakeApiStore,
    private val gson: Gson = Gson(),
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!Environment.USE_FAKE_API) {
            return chain.proceed(request)
        }

        val path = request.url.encodedPath.removePrefix("/api/").trim('/')
        val method = request.method
        val body = request.body?.let { requestBody ->
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            buffer.readUtf8()
        }.orEmpty()

        val (code, json) = handle(method, path, body)
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code in 200..299) "OK" else "Error")
            .body(json.toResponseBody(JSON))
            .build()
    }

    private fun handle(method: String, path: String, body: String): Pair<Int, String> {
        return when {
            method == "POST" && path == "users/login" -> login(body)
            method == "POST" && path == "users/register" -> register(body)
            method == "GET" && path == "plots" -> 200 to gson.toJson(mapOf("plots" to store.plots))
            method == "POST" && path == "plots" -> createPlot(body)
            method == "PUT" && path.matches(Regex("plots/\\d+")) -> updatePlot(path, body)
            method == "POST" && path.matches(Regex("plots/\\d+/fund")) -> fundPlot(path)
            else -> 404 to gson.toJson(mapOf("message" to "Not found"))
        }
    }

    private fun login(body: String): Pair<Int, String> {
        val json = gson.fromJson(body, JsonObject::class.java)
        val email = json.get("email")?.asString.orEmpty()
        val password = json.get("password")?.asString.orEmpty()
        val user = store.users.find { it.email.equals(email, ignoreCase = true) && it.password == password }
            ?: return 401 to gson.toJson(mapOf("message" to "Invalid email or password"))
        val sessionToken = store.newToken()
        store.startSession(user, sessionToken)
        return 200 to gson.toJson(loginPayload(user, sessionToken))
    }

    private fun register(body: String): Pair<Int, String> {
        val json = gson.fromJson(body, JsonObject::class.java)
        val email = json.get("email")?.asString.orEmpty()
        if (email.isBlank() || store.users.any { it.email.equals(email, ignoreCase = true) }) {
            return 400 to gson.toJson(mapOf("message" to "Could not create the account"))
        }
        val fullName = json.get("name")?.asString.orEmpty().ifBlank { "New user" }
        val parts = fullName.trim().split(" ", limit = 2)
        val user = UserRecord(
            email = email,
            password = json.get("password")?.asString.orEmpty(),
            firstName = parts.first(),
            lastName = parts.getOrElse(1) { "" },
            role = json.get("role")?.asString ?: "farmer",
            phone = json.get("phone")?.asString.orEmpty(),
            birthdate = json.get("birthdate")?.asString.orEmpty(),
        )
        store.addUser(user)
        val sessionToken = store.newToken()
        store.startSession(user, sessionToken)
        return 201 to gson.toJson(loginPayload(user, sessionToken))
    }

    private fun createPlot(body: String): Pair<Int, String> {
        val json = gson.fromJson(body, JsonObject::class.java)
        val owner = store.currentUser
            ?: return 401 to gson.toJson(mapOf("message" to "Not authenticated"))
        val record = PlotRecord(
            id = store.nextPlotId(),
            name = json.get("name")?.asString.orEmpty(),
            crop = json.get("crop")?.asString.orEmpty(),
            area = json.get("area")?.asString.orEmpty(),
            harvestDate = json.get("harvestDate")?.asString.orEmpty(),
            place = json.get("place")?.asString.orEmpty(),
            stage = "Available",
            progress = 0,
            status = "available",
            amount = json.get("amount")?.asString.orEmpty(),
            cycle = json.get("cycle")?.asString.orEmpty(),
            milestones = json.get("milestones")?.asString.orEmpty(),
            imageUri = json.get("imageUri")?.asString,
            ownerEmail = owner.email,
            ownerName = owner.displayName,
            latitude = json.get("latitude")?.takeUnless { it.isJsonNull }?.asDouble,
            longitude = json.get("longitude")?.takeUnless { it.isJsonNull }?.asDouble,
            accuracyMeters = json.get("accuracyMeters")?.takeUnless { it.isJsonNull }?.asInt,
            publishedAt = json.get("publishedAt")?.asString.orEmpty().ifBlank {
                PlotFinance.publishedToday()
            },
        )
        store.addPlot(record)
        return 201 to gson.toJson(record)
    }

    private fun updatePlot(path: String, body: String): Pair<Int, String> {
        val id = path.substringAfter("plots/").toIntOrNull()
            ?: return 400 to gson.toJson(mapOf("message" to "Invalid plot"))
        val json = gson.fromJson(body, JsonObject::class.java)
        val owner = store.currentUser?.email
            ?: return 401 to gson.toJson(mapOf("message" to "Not authenticated"))
        val payload = PlotUpdatePayload(
            name = json.get("name")?.asString.orEmpty(),
            crop = json.get("crop")?.asString.orEmpty(),
            area = json.get("area")?.asString.orEmpty(),
            harvestDate = json.get("harvestDate")?.asString.orEmpty(),
            place = json.get("place")?.asString.orEmpty(),
            amount = json.get("amount")?.asString.orEmpty(),
            cycle = json.get("cycle")?.asString.orEmpty(),
            milestones = json.get("milestones")?.asString.orEmpty(),
            imageUri = json.get("imageUri")?.takeUnless { it.isJsonNull }?.asString,
            latitude = json.get("latitude")?.takeUnless { it.isJsonNull }?.asDouble,
            longitude = json.get("longitude")?.takeUnless { it.isJsonNull }?.asDouble,
            accuracyMeters = json.get("accuracyMeters")?.takeUnless { it.isJsonNull }?.asInt,
        )
        val updated = store.updatePlot(id, payload, owner)
            ?: return 404 to gson.toJson(mapOf("message" to "Plot not found or cannot be updated"))
        return 200 to gson.toJson(updated)
    }

    private fun fundPlot(path: String): Pair<Int, String> {
        val id = path.substringAfter("plots/").substringBefore("/fund").toIntOrNull()
            ?: return 400 to gson.toJson(mapOf("message" to "Invalid plot"))
        val merchant = store.currentUser?.email
            ?: return 401 to gson.toJson(mapOf("message" to "Not authenticated"))
        val updated = store.fundPlot(id, merchant)
            ?: return 404 to gson.toJson(mapOf("message" to "Plot not found"))
        return 200 to gson.toJson(updated)
    }

    private fun loginPayload(user: UserRecord, token: String) = mapOf(
        "token" to token,
        "email" to user.email,
        "firstName" to user.firstName,
        "lastName" to user.lastName,
        "role" to user.role,
    )

    private companion object {
        val JSON = "application/json".toMediaType()
    }
}
