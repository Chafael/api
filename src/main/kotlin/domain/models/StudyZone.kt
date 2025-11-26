package com.sylvara.domain.models

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class StudyZone(
    val studyZoneId: Int = 0,
    val projectId: Int,
    val studyZoneName: String,
    val studyZoneDescription: String? = null,
    val squareArea: Double, // Cambiamos BigDecimal a Double para serialización
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = null
) {
    init {
        require(studyZoneName.isNotBlank()) { "El nombre de la zona no puede estar vacío" }
        require(squareArea > 0) { "El área debe ser mayor a 0" }
    }
}