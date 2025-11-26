package com.sylvara.domain.ports

import com.sylvara.domain.models.Species

interface SpeciesRepository {
    suspend fun save(species: Species): Species
    suspend fun findById(id: Int): Species?
    suspend fun findByProjectId(projectId: Int): List<Species>
    suspend fun findAll(): List<Species>
    suspend fun update(species: Species): Species
    suspend fun delete(id: Int)
}