package com.sylvara.domain.ports

import com.sylvara.domain.models.SpeciesZone

interface SpeciesZoneRepository {
    suspend fun save(speciesZone: SpeciesZone): SpeciesZone
    suspend fun findById(id: Int): SpeciesZone?
    suspend fun findBySpeciesId(speciesId: Int): List<SpeciesZone>
    suspend fun findByStudyZoneId(studyZoneId: Int): List<SpeciesZone>
    suspend fun findAll(): List<SpeciesZone>
    suspend fun update(speciesZone: SpeciesZone): SpeciesZone
    suspend fun delete(id: Int)
}