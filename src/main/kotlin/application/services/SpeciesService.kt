package com.sylvara.application.services

import com.sylvara.domain.models.*
import com.sylvara.domain.ports.FunctionalTypeRepository
import com.sylvara.domain.ports.SpeciesRepository
import com.sylvara.domain.ports.SpeciesZoneRepository
import io.ktor.server.plugins.*
import java.time.LocalDateTime

class SpeciesService(
    private val speciesRepository: SpeciesRepository,
    private val speciesZoneRepository: SpeciesZoneRepository,
    private val functionalTypeRepository: FunctionalTypeRepository
) {

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

        val speciesZones = speciesZoneRepository.findBySpeciesId(id)
        speciesZones.forEach { speciesZone ->
            speciesZoneRepository.delete(speciesZone.speciesZoneId)
        }

        speciesRepository.delete(id)
    }

    suspend fun createCompleteSpecies(request: CreateSpeciesRequest): Pair<Species, SpeciesZone> {
        val functionalType = functionalTypeRepository.findById(request.functionalTypeId)
            ?: throw NotFoundException("Tipo funcional con ID ${request.functionalTypeId} no encontrado")

        val newSpecies = Species(
            projectId = request.projectId,
            speciesName = request.speciesName, // ← CAMBIO AQUÍ
            speciesPhoto = request.speciesPhoto,
            functionalTypeId = request.functionalTypeId
        )
        val savedSpecies = speciesRepository.save(newSpecies)

        val speciesZone = SpeciesZone(
            speciesId = savedSpecies.speciesId,
            studyZoneId = request.studyZoneId,
            samplingUnit = request.samplingUnit,
            individualCount = request.individualCount,
            heightStratum = request.heightStratum
        )
        val savedZone = speciesZoneRepository.save(speciesZone)

        return Pair(savedSpecies, savedZone)
    }

    suspend fun getSpeciesDetailsByZone(studyZoneId: Int): List<SpeciesDetail> {
        val speciesZones = speciesZoneRepository.findByStudyZoneId(studyZoneId)

        return speciesZones.map { sz ->
            val species = speciesRepository.findById(sz.speciesId)
                ?: throw NotFoundException("Especie con ID ${sz.speciesId} no encontrada")

            val functionalType = functionalTypeRepository.findById(species.functionalTypeId)
                ?: throw NotFoundException("Tipo funcional no encontrado")

            SpeciesDetail(
                speciesId = species.speciesId,
                speciesName = species.speciesName,
                speciesPhoto = species.speciesPhoto,
                functionalTypeName = functionalType.functionalTypeName,
                samplingUnit = sz.samplingUnit,
                individualCount = sz.individualCount,
                heightStratum = sz.heightStratum
            )
        }
    }

    suspend fun updateCompleteSpecies(
        speciesId: Int,
        studyZoneId: Int,
        request: UpdateSpeciesRequest
    ): Pair<Species, SpeciesZone> {
        val existingSpecies = speciesRepository.findById(speciesId)
            ?: throw NotFoundException("Especie con ID $speciesId no encontrada")

        val updatedSpecies = existingSpecies.copy(
            speciesName = request.speciesName,
            speciesPhoto = request.speciesPhoto,
            functionalTypeId = request.functionalTypeId
        )
        val savedSpecies = speciesRepository.update(updatedSpecies)

        val speciesZones = speciesZoneRepository.findBySpeciesId(speciesId)
        val existingZone = speciesZones.find { it.studyZoneId == studyZoneId }
            ?: throw NotFoundException("Relación especie-zona no encontrada")

        val updatedZone = existingZone.copy(
            samplingUnit = request.samplingUnit,
            individualCount = request.individualCount,
            heightStratum = request.heightStratum
        )
        val savedZone = speciesZoneRepository.update(updatedZone)

        return Pair(savedSpecies, savedZone)
    }
}