package com.nikolapehnec.networking


import com.nikolapehnec.model.LoginRequest
import com.nikolapehnec.model.LoginResponse
import com.nikolapehnec.model.RegisterRequest
import com.nikolapehnec.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ShowsApiService {

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("users/sign_in")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("/shows")
    fun getShows(
        @Header("token-type") tokenType: String,
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    )
}
