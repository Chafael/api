package com.sylvara.infrastructure

import com.sylvara.data.postgres.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val database = Database.connect(hikari())

        // ESTO ES ORO PARA ESTUDIANTES:
        // Crea la tabla automáticamente si no existe.
        // ¡Ya no necesitas ejecutar el script SQL manual!
        transaction(database) {
            SchemaUtils.create(UserTable)
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()

        // 1. CONFIGURACIÓN DE ACCESO (¡CAMBIA ESTO!)
        config.driverClassName = "org.postgresql.Driver"
        // Si tu BD se llama 'postgres' o 'sylvara_db', ponlo al final de la URL:
        config.jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"
        config.username = "postgres" // Tu usuario de Postgres
        config.password = "password" // Tu contraseña de Postgres

        // 2. CONFIGURACIÓN DEL POOL (Opcional, pero bueno tenerlo)
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()

        return HikariDataSource(config)
    }
}