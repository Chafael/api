package com.sylvara.domain.models

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UserProfile(
    val userId: Int,
    val userName: String,
    val userLastname: String,
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
    val userBiography: String? = null,
    val userEmail: String? = null,
    @Serializable(with = LocalDateSerializer::class)
    val userBirthday: LocalDate? = null,
    val userPassword: String? = null
)