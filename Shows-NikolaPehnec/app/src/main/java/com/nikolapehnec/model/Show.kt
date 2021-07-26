package com.nikolapehnec.model

import androidx.annotation.DrawableRes

data class Show(
    val id: String,
    val name: String,
    val description: String,
    val longDescription: String,
    var reviews: List<Review>,
    @DrawableRes val imageResourceId: Int
)

