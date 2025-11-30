package com.sylvara.application.routes

import com.sylvara.application.services.UserService
import com.sylvara.domain.models.UpdateUserProfileRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.userConfigurationRoutes(userService: UserService) {
    route("/configuration/{userId}") {

        // GET: Obtener perfil completo del usuario
        // 1.- Nombre completo de la persona
        // 2.- Biografía
        // 3.- Email
        // 4.- Edad
        get {
            val userId = call.parameters["userId"]?.toIntOrNull()
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
        put {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "El userId debe ser un número")
                return@put
            }

            try {
                val request = call.receive<UpdateUserProfileRequest>()

                val existingUser = userService.getUserById(userId)

                val updatedUser = existingUser.copy(
                    userName = request.userName,
                    userLastname = request.userLastname,
                    biography = request.biography,
                    userEmail = request.userEmail,
                    userBirthday = request.userBirthday,
                    userPassword = request.userPassword
                )

                val result = userService.updateUser(userId, updatedUser)

                // Devolver el perfil actualizado
                val updatedProfile = userService.getUserProfile(userId)
                call.respond(HttpStatusCode.OK, updatedProfile)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Usuario no encontrado")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al actualizar perfil: ${e.message}")
                )
            }
        }

        // DELETE: Eliminar cuenta de usuario
        delete {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "El userId debe ser un número")
                return@delete
            }

            try {
                userService.deleteUser(userId)
                call.respond(
                    HttpStatusCode.OK,
                    mapOf("message" to "Cuenta eliminada exitosamente")
                )
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Usuario no encontrado")
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al eliminar cuenta: ${e.message}")
                )
            }
        }
    }
}