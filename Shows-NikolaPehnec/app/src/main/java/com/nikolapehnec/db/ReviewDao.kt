package com.nikolapehnec.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nikolapehnec.model.ReviewEntity

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review")
    fun getAllReviews(): LiveData<List<ReviewEntity>>

    @Query("SELECT * FROM review WHERE showId IS :id")
    fun getReviewById(id: Int): LiveData<List<ReviewEntity>>

    @Query("SELECT * FROM review WHERE showId IS :id")
    fun getReviewByIdNL(id: Int): List<ReviewEntity>

    @Query("SELECT * FROM review WHERE offline = 'yes'")
    fun getOfflineReviews(): List<ReviewEntity>

    @Delete
    fun deleteOfflineReview(review:ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReviews(review: List<ReviewEntity>)
}