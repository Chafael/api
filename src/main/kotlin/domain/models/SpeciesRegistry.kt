package com.sylvara.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateSpeciesRequest(
    val projectId: Int,
    val speciesName: String,
    val speciesPhoto: String?,
    val functionalTypeId: Int,
    val studyZoneId: Int,
    val samplingUnit: String?,
    val individualCount: Int?,
    val heightStratum: String?
)

@Serializable
data class UpdateSpeciesRequest(
    val speciesName: String,
    val speciesPhoto: String?,
    val functionalTypeId: Int,
    val samplingUnit: String?,
    val individualCount: Int?,
    val heightStratum: String?
)

@Serializable
data class SpeciesDetail(
    val speciesId: Int,
    val speciesName: String,
    val speciesPhoto: String?,
    val functionalTypeName: String,
    val samplingUnit: String?,
    val individualCount: Int?,
    val heightStratum: String?
)