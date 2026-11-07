package com.example.muyu_app.core.di

import com.example.muyu_app.core.environment.Environment
import com.example.muyu_app.core.network.BackendStateStore
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    fun provideRetrofit(store: BackendStateStore): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = store.token
                val builder = chain.request().newBuilder()
                if (!token.isNullOrBlank()) {
                    builder.header("Authorization", "Bearer $token")
                }
                chain.proceed(builder.build())
            }
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
