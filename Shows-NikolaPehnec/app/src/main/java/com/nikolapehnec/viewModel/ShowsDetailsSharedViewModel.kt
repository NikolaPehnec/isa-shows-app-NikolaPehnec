package com.nikolapehnec.viewModel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikolapehnec.Constants
import com.nikolapehnec.NetworkChecker
import com.nikolapehnec.db.ShowsDatabase
import com.nikolapehnec.model.*
import com.nikolapehnec.networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

 class ShowsDetailsSharedViewModel(
    val database: ShowsDatabase,
    val context: Context
) : ViewModel() {

    var showId: Int = -1
    var showTitle: String = ""
    var showDesc: String = ""
    var imgUrl: String = ""
    var dbReviewsCached: List<ReviewEntity>? = null
    val networkChecker = NetworkChecker(context)

    private var message: String? = ""

    fun getMessage(): String? {
        return message
    }

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

                checkForOfflineReviews()
                ApiModule.retrofit.getReviewsForShow(showId)
                    .enqueue(object : Callback<ReviewResponse> {
                        override fun onResponse(
                            call: Call<ReviewResponse>,
                            response: Response<ReviewResponse>
                        ) {
                            reviewLiveData.value = response.body()?.review?.map {
                                ReviewEntity(
                                    it.id.toInt(),
                                    it.comment,
                                    it.rating,
                                    it.show_id.toString(),
                                    it.user,
                                    "no"
                                )
                            }

                            Executors.newSingleThreadExecutor().execute {
                                val reviews = response.body()?.review
                                if (reviews != null) {
                                    database.reviewsDao().insertReviews(
                                        reviews.map {
                                            ReviewEntity(
                                                it.id.toInt(),
                                                it.comment,
                                                it.rating,
                                                it.show_id.toString(),
                                                it.user,
                                                "no"
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

    fun postReview(rating: Int, comment: String?, showId: String, sharedPref: SharedPreferences) {

        if (networkChecker.isOnline()) {
            ApiModule.retrofit.postReview(ReviewRequest(rating, comment, showId)).enqueue(
                object : Callback<SingleReviewResponse> {
                    override fun onResponse(
                        call: Call<SingleReviewResponse>,
                        response: Response<SingleReviewResponse>
                    ) {
                        postReviewResultLiveData.value = response.isSuccessful
                        if(!response.isSuccessful){
                            message = response.errorBody()?.string()?.substringAfter("errors\":[\"")
                        }
                    }

                    override fun onFailure(call: Call<SingleReviewResponse>, t: Throwable) {
                        postReviewResultLiveData.value = false
                    }
                })
        } else {
            Executors.newSingleThreadExecutor().execute {
                val userId: Int? = sharedPref.getString(Constants.USER_ID, "0")?.toInt()
                val email: String? = sharedPref.getString(Constants.EMAIL, "0")
                val imgUrl: String? = sharedPref.getString(Constants.IMG_URL, null)

                val review = ReviewEntity(
                    0,
                    comment,
                    rating,
                    showId,
                    User(userId!!, email!!, imgUrl),
                    "yes"
                )

                database.reviewsDao().insertReview(review)
            }
        }
    }

    private fun checkForOfflineReviews() {
        Executors.newSingleThreadExecutor().execute {
                val offlineReviews = database.reviewsDao().getOfflineReviews()

                offlineReviews.forEach {
                    database.reviewsDao().deleteOfflineReview(
                        ReviewEntity(
                            it.id,
                            it.comment,
                            it.rating,
                            it.showId,
                            it.user,
                            "yes"
                        )
                    )

                    ApiModule.retrofit.postReview(ReviewRequest(it.rating, it.comment, it.showId))
                        .enqueue(
                            object : Callback<SingleReviewResponse> {
                                override fun onResponse(
                                    call: Call<SingleReviewResponse>,
                                    response: Response<SingleReviewResponse>
                                ) {
                                    getReviewsByShowId()
                                }

                                override fun onFailure(
                                    call: Call<SingleReviewResponse>,
                                    t: Throwable
                                ) {
                                }
                            })
                }
        }
    }


}