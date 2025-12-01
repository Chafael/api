package com.sylvara.infrastructure.plugins

import com.sylvara.infrastructure.security.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {

    authentication {
        jwt("auth-jwt") {

            verifier(JwtConfig.verifier)

            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                val email = credential.payload.getClaim("email").asString()

                if (userId != null && email != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { defaultScheme, realm ->
                call.respond(
                    io.ktor.http.HttpStatusCode.Unauthorized,
                    mapOf("error" to "Token inválido o expirado")
                )
            }
        }
    }
}