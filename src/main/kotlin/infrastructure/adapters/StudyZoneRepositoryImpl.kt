package com.sylvara.infrastructure.adapters

import com.sylvara.data.postgres.StudyZoneTable
import com.sylvara.domain.models.StudyZone
import com.sylvara.domain.ports.StudyZoneRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class StudyZoneRepositoryImpl : StudyZoneRepository {

    private fun rowToStudyZone(row: ResultRow): StudyZone {
        return StudyZone(
            studyZoneId = row[StudyZoneTable.id],
            projectId = row[StudyZoneTable.projectId],
            studyZoneName = row[StudyZoneTable.name],
            studyZoneDescription = row[StudyZoneTable.description],
            squareArea = row[StudyZoneTable.squareArea].toDouble(),
            createdAt = row[StudyZoneTable.createdAt]
        )
    }

    override suspend fun save(studyZone: StudyZone): StudyZone {
        return transaction {
            val insertStatement = StudyZoneTable.insert {
                it[projectId] = studyZone.projectId
                it[name] = studyZone.studyZoneName
                it[description] = studyZone.studyZoneDescription
                it[squareArea] = studyZone.squareArea.toBigDecimal()
            }
            val newId = insertStatement[StudyZoneTable.id]
            studyZone.copy(studyZoneId = newId)
        }
    }

    override suspend fun findById(id: Int): StudyZone? {
        return transaction {
            StudyZoneTable.selectAll()
                .where { StudyZoneTable.id eq id }
                .map { rowToStudyZone(it) }
                .singleOrNull()
        }
    }

    override suspend fun findByProjectId(projectId: Int): List<StudyZone> {
        return transaction {
            StudyZoneTable.selectAll()
                .where { StudyZoneTable.projectId eq projectId }
                .map { rowToStudyZone(it) }
        }
    }

    override suspend fun findAll(): List<StudyZone> {
        return transaction {
            StudyZoneTable.selectAll()
                .map { rowToStudyZone(it) }
        }
    }

    override suspend fun update(studyZone: StudyZone): StudyZone {
        return transaction {
            StudyZoneTable.update({ StudyZoneTable.id eq studyZone.studyZoneId }) {
                it[projectId] = studyZone.projectId
                it[name] = studyZone.studyZoneName
                it[description] = studyZone.studyZoneDescription
                it[squareArea] = studyZone.squareArea.toBigDecimal()
            }
            studyZone
        }
    }

    override suspend fun delete(id: Int) {
        transaction {
            StudyZoneTable.deleteWhere { StudyZoneTable.id eq id }
        }
    }
}