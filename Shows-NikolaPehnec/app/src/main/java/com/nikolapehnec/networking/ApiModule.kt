package com.nikolapehnec.networking

import android.content.SharedPreferences
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ApiModule {
    private const val BASE_URL = "https://tv-shows.infinum.academy"

    lateinit var retrofit: ShowsApiService

    fun initRetrofit(preferences: SharedPreferences) {
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(Interceptor { chain ->
                val builder = chain.request().newBuilder()
                val acesstoken = preferences.getString("loginSuccesful", "0")
                if (acesstoken != null) {
                    builder.header("access-token", acesstoken)
                }

                return@Interceptor chain.proceed(builder.build())
            })
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(okhttp)
            .build()
            .create(ShowsApiService::class.java)
    }
}