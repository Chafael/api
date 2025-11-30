package com.sylvara.domain.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ProjectDetails(
    val projectId: Int,
    val projectName: String,
    val projectDescription: String?,
    val projectStatus: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime?,
    val studyZones: List<StudyZoneInfo>
)

@Serializable
data class StudyZoneInfo(
    val studyZoneId: Int,
    val studyZoneName: String,
    val squareArea: Double
)