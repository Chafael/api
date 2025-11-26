package com.sylvara.domain.ports

import com.sylvara.domain.models.FunctionalType

interface FunctionalTypeRepository {
    suspend fun save(functionalType: FunctionalType): FunctionalType
    suspend fun findById(id: Int): FunctionalType?
    suspend fun findByName(name: String): FunctionalType?
    suspend fun findAll(): List<FunctionalType>
    suspend fun update(functionalType: FunctionalType): FunctionalType
    suspend fun delete(id: Int)
}