package com.nikolapehnec.networking


import com.nikolapehnec.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ShowsApiService {

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("users/sign_in")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("/shows")
    fun getShows(): Call<ShowResponse>

    @GET("/shows/{show_id}/reviews")
    fun getReviewsForShow(@Path("show_id") id: Int): Call<ReviewResponse>

    @POST("/reviews")
    fun postReview(@Body request: ReviewRequest): Call<SingleReviewResponse>
}
