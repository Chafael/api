package com.sylvara.infrastructure

import com.sylvara.data.postgres.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val database = Database.connect(hikari())

        transaction(database) {
            SchemaUtils.create(
                UserTable,
                ProjectTable,
                StudyZoneTable,
                FunctionalTypeTable,
                SpeciesTable,
                SpeciesZoneTable
            )
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = "jdbc:postgresql://database-1.crkhrdughkyc.us-east-1.rds.amazonaws.com:5432/sylvara"
        config.username = "postgres"
        config.password = "josecalvo2006"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }
}