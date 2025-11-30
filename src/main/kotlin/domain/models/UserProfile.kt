package com.sylvara.domain.models

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UserProfile(
    val userId: Int,
    val fullName: String,
    val biography: String?,
    val email: String,
    val age: Int,
    @Serializable(with = LocalDateSerializer::class)
    val birthday: LocalDate
)

@Serializable
data class UpdateUserProfileRequest(
    val userName: String,
    val userLastname: String,
    val biography: String?,
    val userEmail: String,
    @Serializable(with = LocalDateSerializer::class)
    val userBirthday: LocalDate,
    val userPassword: String
)