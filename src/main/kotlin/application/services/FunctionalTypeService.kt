package com.sylvara.application.services

import com.sylvara.domain.models.FunctionalType
import com.sylvara.domain.ports.FunctionalTypeRepository
import io.ktor.server.plugins.*

class FunctionalTypeService(private val functionalTypeRepository: FunctionalTypeRepository) {

    suspend fun createFunctionalType(functionalType: FunctionalType): FunctionalType {
        if (functionalType.functionalTypeName.isBlank()) {
            throw IllegalArgumentException("El nombre del tipo funcional es obligatorio.")
        }

        val existing = functionalTypeRepository.findByName(functionalType.functionalTypeName)
        if (existing != null) {
            throw IllegalArgumentException("El tipo funcional '${functionalType.functionalTypeName}' ya existe.")
        }

        return functionalTypeRepository.save(functionalType)
    }

    suspend fun getAllFunctionalTypes(): List<FunctionalType> {
        return functionalTypeRepository.findAll()
    }

    suspend fun getFunctionalTypeById(id: Int): FunctionalType {
        return functionalTypeRepository.findById(id)
            ?: throw NotFoundException("Tipo funcional con ID $id no encontrado")
    }

    suspend fun updateFunctionalType(id: Int, functionalType: FunctionalType): FunctionalType {
        val existing = functionalTypeRepository.findById(id)
            ?: throw NotFoundException("Tipo funcional con ID $id no encontrado")

        val typeToUpdate = functionalType.copy(functionalTypeId = id)
        return functionalTypeRepository.update(typeToUpdate)
    }

    suspend fun deleteFunctionalType(id: Int) {
        val exists = functionalTypeRepository.findById(id) != null
        if (!exists) {
            throw NotFoundException("Tipo funcional con ID $id no existe")
        }
        functionalTypeRepository.delete(id)
    }
}