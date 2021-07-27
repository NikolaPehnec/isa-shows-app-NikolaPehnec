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
                val acesstoken = preferences.getString("loginSuccessful", "0")
                val uid = preferences.getString("uid", "0")
                val client = preferences.getString("client", "0")

                if (acesstoken != null && acesstoken != "0" && uid != null && client != null) {
                    builder.header("token-type", "Bearer")
                    builder.header("access-token", acesstoken)
                    builder.header("client", client)
                    builder.header("uid", uid)
                }
                return@Interceptor chain.proceed(builder.build())
            })
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory((Json {
                ignoreUnknownKeys = true
            }).asConverterFactory("application/json".toMediaType()))
            .client(okhttp)
            .build()
            .create(ShowsApiService::class.java)
    }
}