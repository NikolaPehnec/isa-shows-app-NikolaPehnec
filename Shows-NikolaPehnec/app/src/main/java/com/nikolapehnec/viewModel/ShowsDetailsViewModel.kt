package com.nikolapehnec.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.NetworkChecker
import com.nikolapehnec.db.ShowsDatabase
import com.nikolapehnec.model.ReviewEntity
import com.nikolapehnec.model.ReviewRequest
import com.nikolapehnec.model.ReviewResponse
import com.nikolapehnec.model.SingleReviewResponse
import com.nikolapehnec.networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class ShowsDetailsViewModel(
    val database: ShowsDatabase,
    val context: Context
) : ViewModel() {

    var showId: Int = -1
    var showTitle: String = ""
    var showDesc: String = ""
    var imgUrl: String = ""
    var dbReviewsCached: List<ReviewEntity>? = null
    val networkChecker = NetworkChecker(context)

    private val reviewLiveData: MutableLiveData<List<ReviewEntity>> by lazy {
        MutableLiveData<List<ReviewEntity>>()
    }

    private val postReviewResultLiveData: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun getPostReviewResultLiveData(): LiveData<Boolean> {
        return postReviewResultLiveData
    }

    fun getReviewsLiveData(): LiveData<List<ReviewEntity>> {
        return if (networkChecker.isOnline()) {
            Log.v("load_iz", "API")
            reviewLiveData
        } else {
            Log.v("load_iz", "BAZA")
            Executors.newSingleThreadExecutor().execute {
                dbReviewsCached = database.reviewsDao().getReviewByIdNL(showId)
            }
            database.reviewsDao().getReviewById(showId)
        }
    }

    fun calculateAverageGrade(): Float? =
        if (networkChecker.isOnline()) {
            getReviewsLiveData().value?.map { r -> r.rating }?.average()?.toFloat()
        } else {
            dbReviewsCached?.map { r -> r.rating }?.average()?.toFloat()
        }

    fun getReviewsByShowId() {
        if (networkChecker.isOnline()) {

            ApiModule.retrofit.getReviewsForShow(showId)
                .enqueue(object : Callback<ReviewResponse> {
                    override fun onResponse(
                        call: Call<ReviewResponse>,
                        response: Response<ReviewResponse>
                    ) {
                        reviewLiveData.value = response.body()?.review?.map {
                            ReviewEntity(it.id, it.comment, it.rating, it.show_id, it.user)
                        }

                        Executors.newSingleThreadExecutor().execute {
                            val reviews = response.body()?.review
                            if (reviews != null) {
                                database.reviewsDao().insertReviews(
                                    reviews.map {
                                        ReviewEntity(
                                            it.id,
                                            it.comment,
                                            it.rating,
                                            it.show_id,
                                            it.user
                                        )
                                    }
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<ReviewResponse>, t: Throwable) {
                    }
                })
        }
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