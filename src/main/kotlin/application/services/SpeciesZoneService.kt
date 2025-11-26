package com.sylvara.application.services

import com.sylvara.domain.models.SpeciesZone
import com.sylvara.domain.ports.SpeciesZoneRepository
import io.ktor.server.plugins.*

class SpeciesZoneService(private val speciesZoneRepository: SpeciesZoneRepository) {

    suspend fun createSpeciesZone(speciesZone: SpeciesZone): SpeciesZone {
        return speciesZoneRepository.save(speciesZone)
    }

    suspend fun getAllSpeciesZones(): List<SpeciesZone> {
        return speciesZoneRepository.findAll()
    }

    suspend fun getSpeciesZoneById(id: Int): SpeciesZone {
        return speciesZoneRepository.findById(id)
            ?: throw NotFoundException("SpeciesZone con ID $id no encontrada")
    }

    suspend fun getSpeciesZonesBySpeciesId(speciesId: Int): List<SpeciesZone> {
        return speciesZoneRepository.findBySpeciesId(speciesId)
    }

    suspend fun getSpeciesZonesByStudyZoneId(studyZoneId: Int): List<SpeciesZone> {
        return speciesZoneRepository.findByStudyZoneId(studyZoneId)
    }

    suspend fun updateSpeciesZone(id: Int, speciesZone: SpeciesZone): SpeciesZone {
        val existing = speciesZoneRepository.findById(id)
            ?: throw NotFoundException("SpeciesZone con ID $id no encontrada")

        val zoneToUpdate = speciesZone.copy(speciesZoneId = id)
        return speciesZoneRepository.update(zoneToUpdate)
    }

    suspend fun deleteSpeciesZone(id: Int) {
        val exists = speciesZoneRepository.findById(id) != null
        if (!exists) {
            throw NotFoundException("SpeciesZone con ID $id no existe")
        }
        speciesZoneRepository.delete(id)
    }
}