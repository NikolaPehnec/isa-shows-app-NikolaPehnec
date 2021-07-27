package com.nikolapehnec.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SingleReviewResponse(
    @SerialName("review") val review: Review
)





