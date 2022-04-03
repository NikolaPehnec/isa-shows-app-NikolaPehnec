package com.nikolapehnec.networking


import com.nikolapehnec.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ShowsApiService {

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("users/sign_in")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("/shows")
    fun getShows(): Call<ShowResponse>

    @GET("/shows/top_rated")
    fun getTopRatedShows(): Call<ShowResponse>

    @GET("/shows/{show_id}/reviews")
    fun getReviewsForShow(@Path("show_id") id: Int): Call<ReviewResponse>

    @POST("/reviews")
    fun postReview(@Body request: ReviewRequest): Call<SingleReviewResponse>

    @Multipart
    @PUT("/users")
    fun updateImage(
       // @Part("id") id: RequestBody,
       // @Part("email") email: RequestBody,
        @Part image_url: MultipartBody.Part
    ): Call<LoginResponse>
}
