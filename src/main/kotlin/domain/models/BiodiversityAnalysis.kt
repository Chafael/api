package com.sylvara.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class BiodiversityAnalysis(
    val zones: List<ZoneBiodiversity>
)

@Serializable
data class ZoneBiodiversity(
    val studyZoneId: Int,
    val studyZoneName: String,
    val squareArea: Double,
    val indices: BiodiversityIndices
)