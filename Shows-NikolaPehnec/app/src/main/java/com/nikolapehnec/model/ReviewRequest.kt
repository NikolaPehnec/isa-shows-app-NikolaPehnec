package com.nikolapehnec.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewRequest(
    @SerialName("rating") val rating: Int,
    @SerialName("comment") val comment: String?,
    @SerialName("show_id") val showId: String
)