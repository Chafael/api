package com.sylvara.domain.models

import io.ktor.http.ContentType
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)


@Serializable
data class LoginResponse(
    val token: String,
    val userId: Int,
    val email: String,
    val messages: String = "Login exitoso"
)