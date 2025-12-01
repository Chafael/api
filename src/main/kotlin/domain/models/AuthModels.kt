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
    val message: String = "Login exitoso"
)

@Serializable
data class RegisterRequest(
    val userName: String,
    val userLastname: String,
    @Serializable(with = LocalDateSerializer::class)
    val userBirthday: java.time.LocalDate,
    val userEmail: String,
    val userPassword: String,
    val biography: String? = null
)