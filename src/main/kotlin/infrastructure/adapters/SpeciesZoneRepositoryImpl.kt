package com.sylvara.infrastructure.adapters

import com.sylvara.data.postgres.SpeciesZoneTable
import com.sylvara.domain.models.SpeciesZone
import com.sylvara.domain.ports.SpeciesZoneRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class SpeciesZoneRepositoryImpl : SpeciesZoneRepository {

    private fun rowToSpeciesZone(row: ResultRow): SpeciesZone {
        return SpeciesZone(
            speciesZoneId = row[SpeciesZoneTable.id],
            speciesId = row[SpeciesZoneTable.speciesId],
            studyZoneId = row[SpeciesZoneTable.studyZoneId],
            samplingUnit = row[SpeciesZoneTable.samplingUnit],
            individualCount = row[SpeciesZoneTable.individualCount],
            heightStratum = row[SpeciesZoneTable.heightStratum],
            createdAt = row[SpeciesZoneTable.createdAt],
            updatedAt = row[SpeciesZoneTable.updatedAt]
        )
    }

    override suspend fun save(speciesZone: SpeciesZone): SpeciesZone {
        return transaction {
            val insertStatement = SpeciesZoneTable.insert {
                it[speciesId] = speciesZone.speciesId
                it[studyZoneId] = speciesZone.studyZoneId
                it[samplingUnit] = speciesZone.samplingUnit
                it[individualCount] = speciesZone.individualCount
                it[heightStratum] = speciesZone.heightStratum
            }
            val newId = insertStatement[SpeciesZoneTable.id]
            speciesZone.copy(speciesZoneId = newId)
        }
    }

    override suspend fun findById(id: Int): SpeciesZone? {
        return transaction {
            SpeciesZoneTable.selectAll()
                .where { SpeciesZoneTable.id eq id }
                .map { rowToSpeciesZone(it) }
                .singleOrNull()
        }
    }

    override suspend fun findBySpeciesId(speciesId: Int): List<SpeciesZone> {
        return transaction {
            SpeciesZoneTable.selectAll()
                .where { SpeciesZoneTable.speciesId eq speciesId }
                .map { rowToSpeciesZone(it) }
        }
    }

    override suspend fun findByStudyZoneId(studyZoneId: Int): List<SpeciesZone> {
        return transaction {
            SpeciesZoneTable.selectAll()
                .where { SpeciesZoneTable.studyZoneId eq studyZoneId }
                .map { rowToSpeciesZone(it) }
        }
    }

    override suspend fun findAll(): List<SpeciesZone> {
        return transaction {
            SpeciesZoneTable.selectAll()
                .map { rowToSpeciesZone(it) }
        }
    }

    override suspend fun update(speciesZone: SpeciesZone): SpeciesZone {
        return transaction {
            SpeciesZoneTable.update({ SpeciesZoneTable.id eq speciesZone.speciesZoneId }) {
                it[speciesId] = speciesZone.speciesId
                it[studyZoneId] = speciesZone.studyZoneId
                it[samplingUnit] = speciesZone.samplingUnit
                it[individualCount] = speciesZone.individualCount
                it[heightStratum] = speciesZone.heightStratum
            }
            speciesZone
        }
    }

    override suspend fun delete(id: Int) {
        transaction {
            SpeciesZoneTable.deleteWhere { SpeciesZoneTable.id eq id }
        }
    }
}