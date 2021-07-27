package com.nikolapehnec

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.model.Review
import com.nikolapehnec.model.ReviewResponse
import com.nikolapehnec.networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowsDetailsViewModel : ViewModel() {

    private val reviewLiveData: MutableLiveData<List<Review>> by lazy {
        MutableLiveData<List<Review>>()
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


}