package com.sylvara.infrastructure.adapters

import com.sylvara.data.postgres.FunctionalTypeTable
import com.sylvara.domain.models.FunctionalType
import com.sylvara.domain.ports.FunctionalTypeRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class FunctionalTypeRepositoryImpl : FunctionalTypeRepository {

    private fun rowToFunctionalType(row: ResultRow): FunctionalType {
        return FunctionalType(
            functionalTypeId = row[FunctionalTypeTable.id],
            functionalTypeName = row[FunctionalTypeTable.name]
        )
    }

    override suspend fun save(functionalType: FunctionalType): FunctionalType {
        return transaction {
            val insertStatement = FunctionalTypeTable.insert {
                it[name] = functionalType.functionalTypeName
            }
            val newId = insertStatement[FunctionalTypeTable.id]
            functionalType.copy(functionalTypeId = newId)
        }
    }

    override suspend fun findById(id: Int): FunctionalType? {
        return transaction {
            FunctionalTypeTable.selectAll()
                .where { FunctionalTypeTable.id eq id }
                .map { rowToFunctionalType(it) }
                .singleOrNull()
        }
    }

    override suspend fun findByName(name: String): FunctionalType? {
        return transaction {
            FunctionalTypeTable.selectAll()
                .where { FunctionalTypeTable.name eq name }
                .map { rowToFunctionalType(it) }
                .singleOrNull()
        }
    }

    override suspend fun findAll(): List<FunctionalType> {
        return transaction {
            FunctionalTypeTable.selectAll()
                .map { rowToFunctionalType(it) }
        }
    }

    override suspend fun update(functionalType: FunctionalType): FunctionalType {
        return transaction {
            FunctionalTypeTable.update({ FunctionalTypeTable.id eq functionalType.functionalTypeId }) {
                it[name] = functionalType.functionalTypeName
            }
            functionalType
        }
    }

    override suspend fun delete(id: Int) {
        transaction {
            FunctionalTypeTable.deleteWhere { FunctionalTypeTable.id eq id }
        }
    }
}