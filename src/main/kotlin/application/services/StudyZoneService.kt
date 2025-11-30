package com.sylvara.application.services

import com.sylvara.domain.models.BiodiversityIndices
import com.sylvara.domain.models.StudyZone
import com.sylvara.domain.models.StudyZoneDetails
import com.sylvara.domain.ports.SpeciesZoneRepository
import com.sylvara.domain.ports.StudyZoneRepository
import io.ktor.server.plugins.*
import kotlin.math.ln

class StudyZoneService(
    private val studyZoneRepository: StudyZoneRepository,
    private val speciesZoneRepository: SpeciesZoneRepository
) {

    suspend fun createStudyZone(studyZone: StudyZone): StudyZone {
        if (studyZone.studyZoneName.isBlank()) {
            throw IllegalArgumentException("El nombre de la zona es obligatorio.")
        }
        if (studyZone.squareArea <= 0) {
            throw IllegalArgumentException("El área debe ser mayor a 0.")
        }
        return studyZoneRepository.save(studyZone)
    }

    suspend fun getAllStudyZones(): List<StudyZone> {
        return studyZoneRepository.findAll()
    }

    suspend fun getStudyZoneById(id: Int): StudyZone {
        return studyZoneRepository.findById(id)
            ?: throw NotFoundException("Zona de estudio con ID $id no encontrada")
    }

    suspend fun getStudyZonesByProjectId(projectId: Int): List<StudyZone> {
        return studyZoneRepository.findByProjectId(projectId)
    }

    suspend fun updateStudyZone(id: Int, studyZone: StudyZone): StudyZone {
        val existingZone = studyZoneRepository.findById(id)
            ?: throw NotFoundException("Zona de estudio con ID $id no encontrada")

        val zoneToUpdate = studyZone.copy(studyZoneId = id)
        return studyZoneRepository.update(zoneToUpdate)
    }

    suspend fun deleteStudyZone(id: Int) {
        val exists = studyZoneRepository.findById(id) != null
        if (!exists) {
            throw NotFoundException("Zona de estudio con ID $id no existe")
        }
        studyZoneRepository.delete(id)
    }

    // NUEVO: Obtener detalles de la zona con índices de biodiversidad
    suspend fun getStudyZoneDetails(zoneId: Int): StudyZoneDetails {
        val zone = studyZoneRepository.findById(zoneId)
            ?: throw NotFoundException("Zona de estudio con ID $zoneId no encontrada")

        val indices = calculateBiodiversityIndices(zoneId)

        return StudyZoneDetails(
            studyZoneId = zone.studyZoneId,
            projectId = zone.projectId,
            studyZoneName = zone.studyZoneName,
            studyZoneDescription = zone.studyZoneDescription,
            squareArea = zone.squareArea,
            createdAt = zone.createdAt,
            biodiversityIndices = indices
        )
    }

    // NUEVO: Calcular índices de biodiversidad
    private suspend fun calculateBiodiversityIndices(zoneId: Int): BiodiversityIndices {
        val speciesInZone = speciesZoneRepository.findByStudyZoneId(zoneId)

        if (speciesInZone.isEmpty()) {
            return BiodiversityIndices(
                shannonWiener = null,
                simpson = null,
                margalef = null,
                pielou = null
            )
        }

        // Obtener conteos de individuos por especie
        val individualCounts = speciesInZone.mapNotNull { it.individualCount }
        if (individualCounts.isEmpty()) {
            return BiodiversityIndices(null, null, null, null)
        }

        val totalIndividuals = individualCounts.sum().toDouble()
        val numberOfSpecies = speciesInZone.size.toDouble()

        // Índice de Shannon-Wiener (H')
        val shannonWiener = calculateShannonWiener(individualCounts, totalIndividuals)

        // Índice de Simpson (D)
        val simpson = calculateSimpson(individualCounts, totalIndividuals)

        // Índice de Margalef (DMg)
        val margalef = if (totalIndividuals > 1) {
            (numberOfSpecies - 1) / ln(totalIndividuals)
        } else null

        // Índice de Pielou (J')
        val pielou = if (numberOfSpecies > 1 && shannonWiener != null) {
            shannonWiener / ln(numberOfSpecies)
        } else null

        return BiodiversityIndices(
            shannonWiener = shannonWiener,
            simpson = simpson,
            margalef = margalef,
            pielou = pielou
        )
    }

    private fun calculateShannonWiener(counts: List<Int>, total: Double): Double? {
        if (total == 0.0) return null

        return -counts.sumOf { count ->
            val proportion = count / total
            if (proportion > 0) proportion * ln(proportion) else 0.0
        }
    }

    private fun calculateSimpson(counts: List<Int>, total: Double): Double? {
        if (total == 0.0) return null

        val sumSquares = counts.sumOf { count ->
            val proportion = count / total
            proportion * proportion
        }

        return 1 - sumSquares
    }
}