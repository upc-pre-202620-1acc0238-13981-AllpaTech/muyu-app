package com.example.muyu_app.core.di

import com.example.muyu_app.core.environment.Environment
import com.example.muyu_app.core.network.FakeApiInterceptor
import com.example.muyu_app.core.network.FakeApiStore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    fun provideRetrofit(store: FakeApiStore): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = store.token
                val email = store.currentUser?.email
                val builder = chain.request().newBuilder()
                if (!token.isNullOrBlank()) {
                    builder.header("Authorization", "Bearer $token")
                }
                // El fake-api pierde los tokens al reiniciarse en Render; el correo
                // identifica al usuario sin recurrir a una cuenta de demostración.
                if (!email.isNullOrBlank()) {
                    builder.header("X-User-Email", email)
                }
                chain.proceed(builder.build())
            }
            .addInterceptor(FakeApiInterceptor(store))
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(Environment.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
