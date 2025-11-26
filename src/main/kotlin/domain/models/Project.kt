package com.sylvara.domain.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Project(
    val projectId: Int = 0,
    val userId: Int,
    val projectName: String,
    val projectStatus: String = "activo",
    val projectDescription: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = null
) {
    init {
        require(projectName.isNotBlank()) { "El nombre del proyecto no puede estar vacío" }
        require(projectStatus in listOf("activo", "inactivo", "completado")) {
            "Estado inválido. Debe ser: activo, inactivo o completado"
        }
    }
}