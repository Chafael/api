package com.sylvara.application.routes

import com.sylvara.application.services.SpeciesZoneService
import com.sylvara.domain.models.SpeciesZone
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.speciesZoneRoutes(speciesZoneService: SpeciesZoneService) {
    route("/species-zones") {

        // GET: Obtener todas las relaciones especies-zonas
        get {
            val zones = speciesZoneService.getAllSpeciesZones()
            call.respond(zones)
        }

        // GET: Obtener relación por ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "El ID debe ser un número")
                return@get
            }
            try {
                val zone = speciesZoneService.getSpeciesZoneById(id)
                call.respond(zone)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "SpeciesZone no encontrada")
            }
        }

        // GET: Obtener relaciones por especie
        get("/species/{speciesId}") {
            val speciesId = call.parameters["speciesId"]?.toIntOrNull()
            if (speciesId == null) {
                call.respond(HttpStatusCode.BadRequest, "El speciesId debe ser un número")
                return@get
            }
            val zones = speciesZoneService.getSpeciesZonesBySpeciesId(speciesId)
            call.respond(zones)
        }

        // GET: Obtener relaciones por zona de estudio
        get("/study-zone/{studyZoneId}") {
            val studyZoneId = call.parameters["studyZoneId"]?.toIntOrNull()
            if (studyZoneId == null) {
                call.respond(HttpStatusCode.BadRequest, "El studyZoneId debe ser un número")
                return@get
            }
            val zones = speciesZoneService.getSpeciesZonesByStudyZoneId(studyZoneId)
            call.respond(zones)
        }

        // POST: Crear relación especie-zona
        post {
            try {
                val zoneParams = call.receive<SpeciesZone>()
                val savedZone = speciesZoneService.createSpeciesZone(zoneParams)
                call.respond(HttpStatusCode.Created, savedZone)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
            }
        }

        // PUT: Actualizar relación especie-zona
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@put
            }
            try {
                val zoneParams = call.receive<SpeciesZone>()
                val updatedZone = speciesZoneService.updateSpeciesZone(id, zoneParams)
                call.respond(HttpStatusCode.OK, updatedZone)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "SpeciesZone no encontrada")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error al actualizar: ${e.message}")
            }
        }

        // DELETE: Borrar relación especie-zona
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }
            try {
                speciesZoneService.deleteSpeciesZone(id)
                call.respond(HttpStatusCode.NoContent)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "SpeciesZone no existe")
            }
        }
    }
}
