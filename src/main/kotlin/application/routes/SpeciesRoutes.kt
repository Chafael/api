package com.sylvara.application.routes

import com.sylvara.application.services.SpeciesService
import com.sylvara.domain.models.Species
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.speciesRoutes(speciesService: SpeciesService) {
    route("/species") {

        // GET: Obtener todas las especies
        get {
            val species = speciesService.getAllSpecies()
            call.respond(species)
        }

        // GET: Obtener especie por ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "El ID debe ser un número")
                return@get
            }
            try {
                val species = speciesService.getSpeciesById(id)
                call.respond(species)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Especie no encontrada")
            }
        }

        // GET: Obtener especies por proyecto
        get("/project/{projectId}") {
            val projectId = call.parameters["projectId"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "El projectId debe ser un número")
                return@get
            }
            val species = speciesService.getSpeciesByProjectId(projectId)
            call.respond(species)
        }

        // POST: Crear especie
        post {
            try {
                val speciesParams = call.receive<Species>()
                val savedSpecies = speciesService.createSpecies(speciesParams)
                call.respond(HttpStatusCode.Created, savedSpecies)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
            }
        }

        // PUT: Actualizar especie
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@put
            }
            try {
                val speciesParams = call.receive<Species>()
                val updatedSpecies = speciesService.updateSpecies(id, speciesParams)
                call.respond(HttpStatusCode.OK, updatedSpecies)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Especie no encontrada")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error al actualizar: ${e.message}")
            }
        }

        // DELETE: Borrar especie
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }
            try {
                speciesService.deleteSpecies(id)
                call.respond(HttpStatusCode.NoContent)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Especie no existe")
            }
        }
    }
}