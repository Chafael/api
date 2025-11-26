package com.sylvara.domain.ports

import com.sylvara.domain.models.StudyZone

interface StudyZoneRepository {
    suspend fun save(studyZone: StudyZone): StudyZone
    suspend fun findById(id: Int): StudyZone?
    suspend fun findByProjectId(projectId: Int): List<StudyZone>
    suspend fun findAll(): List<StudyZone>
    suspend fun update(studyZone: StudyZone): StudyZone
    suspend fun delete(id: Int)
}