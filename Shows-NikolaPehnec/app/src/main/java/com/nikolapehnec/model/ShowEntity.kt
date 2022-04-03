package com.nikolapehnec.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "show")
data class ShowEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "avgRating") val avgRating: Float?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "imgUrl") val imgUrl: String,
    @ColumnInfo(name = "numOfReviews") val numOfReviews: Int,
)

