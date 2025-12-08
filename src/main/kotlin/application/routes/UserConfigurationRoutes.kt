package com.sylvara.application.routes

import com.sylvara.application.services.UserService
import com.sylvara.domain.models.UpdateUserProfileRequest
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.auth.* // ✅ Requerido para 'authenticate' y 'call.principal'
import io.ktor.server.auth.jwt.* // ✅ Requerido para 'JWTPrincipal'
import io.ktor.server.plugins.* // ✅ Requerido para 'BadRequestException' y 'NotFoundException'
import io.ktor.server.request.* // ✅ Requerido para 'call.receive'

fun Route.userConfigurationRoutes(userService: UserService) {
    authenticate("auth-jwt") {
        route("/user/profile") {

            // GET: Obtener perfil completo del usuario
            // 1.- Nombre completo de la persona
            // 2.- Biografía
            // 3.- Email
            // 4.- Edad
            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "El userId debe ser un número")
                    return@get
                }

                try {
                    val profile = userService.getUserProfile(userId)
                    call.respond(HttpStatusCode.OK, profile)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "Usuario no encontrado")
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Error al obtener perfil: ${e.message}")
                    )
                }
            }

            // PUT: Actualizar perfil de usuario
            // 1.- Nombre completo de la persona
            // 2.- Biografía
            // 3.- Email
            // 4.- Edad (Fecha de nacimiento)
            // 5.- Contraseña
            put("/{userId}") {
                val userId = call.parameters["userId"]?.toIntOrNull() ?: throw BadRequestException("ID de usuario inválido")
                val principal = call.principal<JWTPrincipal>()
                val jwtUserId = principal?.payload?.getClaim("userId")?.asInt()

                if (jwtUserId != userId) { // Validación de seguridad: solo puede editar su propio perfil
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Acceso denegado: Intento de modificar otro perfil"))
                    return@put
                }

                try {
                    val request = call.receive<UpdateUserProfileRequest>()
                    val updatedProfile = userService.updateProfile(userId, request)
                    call.respond(HttpStatusCode.OK, updatedProfile)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al actualizar el perfil: ${e.message}"))
                }
            }

            // DELETE: Eliminar cuenta de usuario
            delete("/{userId}/biography") {
                val userId = call.parameters["userId"]?.toIntOrNull() ?: throw BadRequestException("ID de usuario inválido")
                val principal = call.principal<JWTPrincipal>()
                val jwtUserId = principal?.payload?.getClaim("userId")?.asInt()

                if (jwtUserId != userId) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Acceso denegado: Intento de modificar otro perfil"))
                    return@delete
                }

                try {
                    val success = userService.deleteBiography(userId)
                    if (success) {
                        call.respond(HttpStatusCode.NoContent) // 204 No Content: Éxito sin cuerpo de respuesta
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "No se encontró el perfil para eliminar la biografía."))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al eliminar la biografía: ${e.message}"))
                }
            }
        }
    }
}