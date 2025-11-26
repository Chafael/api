package com.sylvara.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class FunctionalType(
    val functionalTypeId: Int = 0,
    val functionalTypeName: String
) {
    init {
        require(functionalTypeName.isNotBlank()) { "El nombre del tipo funcional no puede estar vacío" }
    }
}