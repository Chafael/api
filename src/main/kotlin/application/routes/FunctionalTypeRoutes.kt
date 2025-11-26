package com.sylvara.application.routes

import com.sylvara.application.services.FunctionalTypeService
import com.sylvara.domain.models.FunctionalType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.functionalTypeRoutes(functionalTypeService: FunctionalTypeService) {
    route("/functional-types") {

        // GET: Obtener todos los tipos funcionales
        get {
            val types = functionalTypeService.getAllFunctionalTypes()
            call.respond(types)
        }

        // GET: Obtener tipo funcional por ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "El ID debe ser un número")
                return@get
            }
            try {
                val type = functionalTypeService.getFunctionalTypeById(id)
                call.respond(type)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Tipo funcional no encontrado")
            }
        }

        // POST: Crear tipo funcional
        post {
            try {
                val typeParams = call.receive<FunctionalType>()
                val savedType = functionalTypeService.createFunctionalType(typeParams)
                call.respond(HttpStatusCode.Created, savedType)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
            }
        }

        // PUT: Actualizar tipo funcional
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@put
            }
            try {
                val typeParams = call.receive<FunctionalType>()
                val updatedType = functionalTypeService.updateFunctionalType(id, typeParams)
                call.respond(HttpStatusCode.OK, updatedType)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Tipo funcional no encontrado")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error al actualizar: ${e.message}")
            }
        }

        // DELETE: Borrar tipo funcional
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }
            try {
                functionalTypeService.deleteFunctionalType(id)
                call.respond(HttpStatusCode.NoContent)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Tipo funcional no existe")
            }
        }
    }
}