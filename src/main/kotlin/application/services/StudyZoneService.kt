package com.sylvara.application.services

import com.sylvara.domain.models.StudyZone
import com.sylvara.domain.ports.StudyZoneRepository
import io.ktor.server.plugins.*

class StudyZoneService(private val studyZoneRepository: StudyZoneRepository) {

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
}