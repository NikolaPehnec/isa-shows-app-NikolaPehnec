package com.nikolapehnec.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowResponse(
    @SerialName("shows") val show: List<Show>
)

@Serializable
data class Show(
    @SerialName("id")
    val id: String,
    @SerialName("average_rating")
    val avgRating: Float?,
    @SerialName("description")
    var description: String?,
    @SerialName("image_url")
    val imgUrl: String,
    @SerialName("no_of_reviews")
    val numOfReviews: Int,
    @SerialName("title")
    val title: String
)



