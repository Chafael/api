package com.sylvara.infrastructure.plugins

import com.sylvara.infrastructure.security.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    println("🔒 CONFIGURANDO SEGURIDAD JWT")

    authentication {
        jwt("auth-jwt") {

            verifier(JwtConfig.verifier)

            validate { credential ->
                println("🔍 VALIDATE LLAMADO")
                val userId = credential.payload.getClaim("userId").asInt()
                val email = credential.payload.getClaim("email").asString()

                if (userId != null && email != null) {
                    println("✅ Token válido: userId=$userId")
                    JWTPrincipal(credential.payload)
                } else {
                    println("❌ Token inválido")
                    null
                }
            }

            challenge { defaultScheme, realm ->
                println("⛔ CHALLENGE EJECUTADO - Sin token o token inválido")
                call.respond(
                    io.ktor.http.HttpStatusCode.Unauthorized,
                    mapOf("error" to "Token inválido o expirado")
                )
            }
        }
    }

    println("✅ SEGURIDAD JWT CONFIGURADA")
}