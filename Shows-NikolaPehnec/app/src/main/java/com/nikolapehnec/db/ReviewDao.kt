package com.nikolapehnec.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nikolapehnec.model.ReviewEntity

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review")
    fun getAllReviews(): LiveData<List<ReviewEntity>>

    @Query("SELECT * FROM review WHERE showId IS :id")
    fun getReviewById(id: Int): LiveData<List<ReviewEntity>>

    @Query("SELECT * FROM review WHERE showId IS :id")
    fun getReviewByIdNL(id: Int): List<ReviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReviews(review: List<ReviewEntity>)
}