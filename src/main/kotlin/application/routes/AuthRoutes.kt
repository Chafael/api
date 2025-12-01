package com.sylvara.application.routes

import com.sylvara.application.services.AuthService
import com.sylvara.domain.models.LoginRequest
import com.sylvara.domain.models.RegisterRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.authRoutes(authService: AuthService) {
    route("/auth") {

        post("/login") {
            try {
                val loginRequest = call.receive<LoginRequest>()
                val loginResponse = authService.login(loginRequest)
                call.respond(HttpStatusCode.OK, loginResponse)
            } catch (e: NotFoundException) {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Usuario no encontrado")
                )
            } catch (e: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Credenciales inválidas")
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error en el servidor: ${e.message}")
                )
            }
        }

        post("/register") {
            try {
                val registerRequest = call.receive<RegisterRequest>()
                val loginResponse = authService.register(registerRequest)
                call.respond(HttpStatusCode.Created, loginResponse)
            } catch (e: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to e.message)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error en el servidor: ${e.message}")
                )
            }
        }
    }
}