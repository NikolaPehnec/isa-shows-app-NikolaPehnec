package com.nikolapehnec.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowsRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)