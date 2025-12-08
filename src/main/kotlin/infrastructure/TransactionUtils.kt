package com.sylvara.infrastructure

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.Transaction

// Función de utilidad para envolver las operaciones de base de datos
// en una Coroutine, asegurando que las transacciones se ejecuten
// en el pool de hilos correcto (Dispatchers.IO).
suspend fun <T> dbQuery(block: suspend Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }