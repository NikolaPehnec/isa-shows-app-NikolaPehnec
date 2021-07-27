package com.nikolapehnec

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.model.Review
import com.nikolapehnec.model.ReviewRequest
import com.nikolapehnec.model.ReviewResponse
import com.nikolapehnec.model.SingleReviewResponse
import com.nikolapehnec.networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    fun addReview(review: Review) {
//        reviewLiveData.value?. = showLiveData.value?.reviews?.plus(review)!!
//        //da se pozove observer
//        showLiveData.value = showLiveData.value*/
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
                    error("wot")
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
            }
        )
    }


}