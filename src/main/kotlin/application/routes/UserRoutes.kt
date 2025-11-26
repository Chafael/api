package com.sylvara.application.routes

import com.sylvara.application.services.UserService
import com.sylvara.domain.models.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.userRoutes(userService: UserService) {
    route("/users") {

        // GET: Obtener todos los usuarios
        get {
            val users = userService.getAllUsers()
            call.respond(users)
        }

        // GET: Obtener usuario por ID
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
        post {
            try {
                val userParams = call.receive<User>()
                val savedUser = userService.registerUser(userParams)
                call.respond(HttpStatusCode.Created, savedUser)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
            }
        }

        // PUT: Actualizar usuario
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
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }
            try {
                userService.deleteUser(id)
                call.respond(HttpStatusCode.NoContent)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Usuario no existe")
            }
        }
    }
}