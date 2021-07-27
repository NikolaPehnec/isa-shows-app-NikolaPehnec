package com.nikolapehnec.model

import androidx.annotation.DrawableRes

data class Review2(
    val user: String,
    val text: String,
    val grade: Int,
    @DrawableRes val imageResourceId: Int,
)

