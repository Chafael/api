package com.sylvara.application.services

import com.sylvara.domain.models.Species
import com.sylvara.domain.ports.SpeciesRepository
import io.ktor.server.plugins.*

class SpeciesService(private val speciesRepository: SpeciesRepository) {

    suspend fun createSpecies(species: Species): Species {
        if (species.speciesName.isBlank()) {
            throw IllegalArgumentException("El nombre de la especie es obligatorio.")
        }
        return speciesRepository.save(species)
    }

    suspend fun getAllSpecies(): List<Species> {
        return speciesRepository.findAll()
    }

    suspend fun getSpeciesById(id: Int): Species {
        return speciesRepository.findById(id)
            ?: throw NotFoundException("Especie con ID $id no encontrada")
    }

    suspend fun getSpeciesByProjectId(projectId: Int): List<Species> {
        return speciesRepository.findByProjectId(projectId)
    }

    suspend fun updateSpecies(id: Int, species: Species): Species {
        val existing = speciesRepository.findById(id)
            ?: throw NotFoundException("Especie con ID $id no encontrada")

        val speciesToUpdate = species.copy(speciesId = id)
        return speciesRepository.update(speciesToUpdate)
    }

    suspend fun deleteSpecies(id: Int) {
        val exists = speciesRepository.findById(id) != null
        if (!exists) {
            throw NotFoundException("Especie con ID $id no existe")
        }
        speciesRepository.delete(id)
    }
}