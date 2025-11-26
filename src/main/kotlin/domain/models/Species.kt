package com.sylvara.domain.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Species(
    val speciesId: Int = 0,
    val projectId: Int,
    val speciesName: String,
    val speciesPhoto: String? = null,
    val functionalTypeId: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = null
) {
    init {
        require(speciesName.isNotBlank()) { "El nombre de la especie no puede estar vacío" }
    }
}