package com.sylvara.domain.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class StudyZoneDetails(
    val studyZoneId: Int,
    val projectId: Int,
    val studyZoneName: String,
    val studyZoneDescription: String?,
    val squareArea: Double,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime?,
    val biodiversityIndices: BiodiversityIndices
)

@Serializable
data class BiodiversityIndices(
    val shannonWiener: Double?,
    val simpson: Double?,
    val margalef: Double?,
    val pielou: Double?
)

@Serializable
data class UpdateStudyZoneRequest(
    val studyZoneName: String,
    val studyZoneDescription: String?,
    val squareArea: Double
)