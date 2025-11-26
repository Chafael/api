package com.sylvara.infrastructure.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    // ESTO es lo que faltaba: Instalar el Negociador de Contenido
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true       // Para que el JSON se vea bonito
            isLenient = true         // Para ser flexible con comillas
            ignoreUnknownKeys = true // IMPORTANTE: Evita errores si mandas campos extra

            // Opcional: Si usas serializadores personalizados muy complejos,
            // a veces aquí se registran módulos, pero con @Serializable en tu clase basta.
        })
    }
}