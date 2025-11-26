package com.sylvara.infrastructure.adapters

import com.sylvara.data.postgres.SpeciesTable
import com.sylvara.domain.models.Species
import com.sylvara.domain.ports.SpeciesRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class SpeciesRepositoryImpl : SpeciesRepository {

    private fun rowToSpecies(row: ResultRow): Species {
        return Species(
            speciesId = row[SpeciesTable.id],
            projectId = row[SpeciesTable.projectId],
            speciesName = row[SpeciesTable.name],
            speciesPhoto = row[SpeciesTable.photo],
            functionalTypeId = row[SpeciesTable.functionalTypeId],
            createdAt = row[SpeciesTable.createdAt]
        )
    }

    override suspend fun save(species: Species): Species {
        return transaction {
            val insertStatement = SpeciesTable.insert {
                it[projectId] = species.projectId
                it[name] = species.speciesName
                it[photo] = species.speciesPhoto
                it[functionalTypeId] = species.functionalTypeId
            }
            val newId = insertStatement[SpeciesTable.id]
            species.copy(speciesId = newId)
        }
    }

    override suspend fun findById(id: Int): Species? {
        return transaction {
            SpeciesTable.selectAll()
                .where { SpeciesTable.id eq id }
                .map { rowToSpecies(it) }
                .singleOrNull()
        }
    }

    override suspend fun findByProjectId(projectId: Int): List<Species> {
        return transaction {
            SpeciesTable.selectAll()
                .where { SpeciesTable.projectId eq projectId }
                .map { rowToSpecies(it) }
        }
    }

    override suspend fun findAll(): List<Species> {
        return transaction {
            SpeciesTable.selectAll()
                .map { rowToSpecies(it) }
        }
    }

    override suspend fun update(species: Species): Species {
        return transaction {
            SpeciesTable.update({ SpeciesTable.id eq species.speciesId }) {
                it[projectId] = species.projectId
                it[name] = species.speciesName
                it[photo] = species.speciesPhoto
                it[functionalTypeId] = species.functionalTypeId
            }
            species
        }
    }

    override suspend fun delete(id: Int) {
        transaction {
            SpeciesTable.deleteWhere { SpeciesTable.id eq id }
        }
    }
}