package com.sylvara.domain.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class SpeciesZone(
    val speciesZoneId: Int = 0,
    val speciesId: Int,
    val studyZoneId: Int,
    val samplingUnit: String? = null,
    val individualCount: Int? = null,
    val heightStratum: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime? = null
) {
    init {
        if (individualCount != null) {
            require(individualCount >= 0) { "El conteo de individuos no puede ser negativo" }
        }
    }
}