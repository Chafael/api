package com.sylvara.plugins

import com.sylvara.application.services.UserService
import com.sylvara.domain.models.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.* // Para manejar las excepciones
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(userService: UserService) {
    routing {

        // Agrupamos todo bajo la ruta /users
        route("/users") {

            // GET: Obtener todos los usuarios
            // URL: http://localhost:8080/users
            get {
                val users = userService.getAllUsers()
                call.respond(users)
            }

            // GET: Obtener usuario por ID
            // URL: http://localhost:8080/users/5
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "El ID debe ser un número")
                    return@get
                }

                try {
                    val user = userService.getUserById(id)
                    call.respond(user)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "Usuario no encontrado")
                }
            }

            // POST: Crear usuario
            // URL: http://localhost:8080/users
            // Body: JSON con los datos
            post {
                try {
                    val userParams = call.receive<User>() // Ktor convierte el JSON a Objeto User automáticamente
                    val savedUser = userService.registerUser(userParams)
                    // Retornamos 201 Created
                    call.respond(HttpStatusCode.Created, savedUser)
                } catch (e: IllegalArgumentException) {
                    // Capturamos validaciones de negocio (Email duplicado, pass corta)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
                }
            }

            // PUT: Actualizar usuario
            // URL: http://localhost:8080/users/5
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@put
                }

                try {
                    val userParams = call.receive<User>()
                    val updatedUser = userService.updateUser(id, userParams)
                    call.respond(HttpStatusCode.OK, updatedUser)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "Usuario no encontrado")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error al actualizar: ${e.message}")
                }
            }

            // DELETE: Borrar usuario
            // URL: http://localhost:8080/users/5
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@delete
                }

                try {
                    userService.deleteUser(id)
                    call.respond(HttpStatusCode.NoContent) // 204: Éxito pero sin contenido
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "Usuario no existe")
                }
            }
        }
    }
}