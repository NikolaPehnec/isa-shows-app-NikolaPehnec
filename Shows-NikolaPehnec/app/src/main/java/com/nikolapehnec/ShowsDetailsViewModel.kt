package com.nikolapehnec

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.model.*
import com.nikolapehnec.networking.ApiModule
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ShowsDetailsViewModel : ViewModel() {

    private val reviewLiveData: MutableLiveData<List<Review>> by lazy {
        MutableLiveData<List<Review>>()
    }

    private val postReviewResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun getPostReviewResultLiveData(): LiveData<Boolean> {
        return postReviewResultLiveData
    }

    fun getReviewsLiveData(): LiveData<List<Review>> {
        return reviewLiveData
    }

    fun calculateAverageGrade(): Float? =
        getReviewsLiveData().value?.map { r -> r.rating }?.average()?.toFloat()

    fun getReviewsByShowId(id: Int) {
        ApiModule.retrofit.getReviewsForShow(id)
            .enqueue(object : Callback<ReviewResponse> {
                override fun onResponse(
                    call: Call<ReviewResponse>,
                    response: Response<ReviewResponse>
                ) {
                    reviewLiveData.value = response.body()?.review
                }

                override fun onFailure(call: Call<ReviewResponse>, t: Throwable) {
                }
            })
    }

    fun postReview(rating: Int, comment: String?, showId: String) {
        ApiModule.retrofit.postReview(ReviewRequest(rating, comment, showId)).enqueue(
            object : Callback<SingleReviewResponse> {
                override fun onResponse(
                    call: Call<SingleReviewResponse>,
                    response: Response<SingleReviewResponse>
                ) {
                    postReviewResultLiveData.value = response.isSuccessful
                }

                override fun onFailure(call: Call<SingleReviewResponse>, t: Throwable) {
                    postReviewResultLiveData.value = false
                }
            })
    }




}