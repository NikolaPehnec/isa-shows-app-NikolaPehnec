package com.nikolapehnec.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_review") val id: Int,
    @ColumnInfo(name = "comment") val comment: String?,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "showId") val showId: String,
    @Embedded val user: User,
    @ColumnInfo(name = "offline") val offline: String,
)

