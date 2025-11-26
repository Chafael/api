package com.sylvara.application.routes

import com.sylvara.application.services.StudyZoneService
import com.sylvara.domain.models.StudyZone
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.studyZoneRoutes(studyZoneService: StudyZoneService) {
    route("/study-zones") {

        // GET: Obtener todas las zonas de estudio
        get {
            val studyZones = studyZoneService.getAllStudyZones()
            call.respond(studyZones)
        }

        // GET: Obtener zona de estudio por ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "El ID debe ser un número")
                return@get
            }
            try {
                val studyZone = studyZoneService.getStudyZoneById(id)
                call.respond(studyZone)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Zona de estudio no encontrada")
            }
        }

        // GET: Obtener zonas por proyecto
        get("/project/{projectId}") {
            val projectId = call.parameters["projectId"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "El projectId debe ser un número")
                return@get
            }
            val zones = studyZoneService.getStudyZonesByProjectId(projectId)
            call.respond(zones)
        }

        // POST: Crear zona de estudio
        post {
            try {
                val zoneParams = call.receive<StudyZone>()
                val savedZone = studyZoneService.createStudyZone(zoneParams)
                call.respond(HttpStatusCode.Created, savedZone)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
            }
        }

        // PUT: Actualizar zona de estudio
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@put
            }
            try {
                val zoneParams = call.receive<StudyZone>()
                val updatedZone = studyZoneService.updateStudyZone(id, zoneParams)
                call.respond(HttpStatusCode.OK, updatedZone)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Zona de estudio no encontrada")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error al actualizar: ${e.message}")
            }
        }

        // DELETE: Borrar zona de estudio
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }
            try {
                studyZoneService.deleteStudyZone(id)
                call.respond(HttpStatusCode.NoContent)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Zona de estudio no existe")
            }
        }
    }
}