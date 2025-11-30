package com.sylvara.application.routes

import com.sylvara.application.services.StudyZoneService
import com.sylvara.domain.models.UpdateStudyZoneRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.studyZoneDetailsRoutes(studyZoneService: StudyZoneService) {
    route("/study-zone-details/{zoneId}") {

        // GET 1: Datos de la zona de estudio (Nombre, descripción y extensión)
        get {
            val zoneId = call.parameters["zoneId"]?.toIntOrNull()
            if (zoneId == null) {
                call.respond(HttpStatusCode.BadRequest, "El zoneId debe ser un número")
                return@get
            }

            try {
                val zone = studyZoneService.getStudyZoneById(zoneId)
                call.respond(HttpStatusCode.OK, zone)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Zona no encontrada")
            }
        }

        // GET 2: Índices de biodiversidad (Shannon-Wiener, Simpson, Margalef, Pielou)
        get("/biodiversity") {
            val zoneId = call.parameters["zoneId"]?.toIntOrNull()
            if (zoneId == null) {
                call.respond(HttpStatusCode.BadRequest, "El zoneId debe ser un número")
                return@get
            }

            try {
                val details = studyZoneService.getStudyZoneDetails(zoneId)
                call.respond(HttpStatusCode.OK, details)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Zona no encontrada")
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al calcular índices: ${e.message}")
                )
            }
        }

        // PUT: Actualizar zona de estudio
        put {
            val zoneId = call.parameters["zoneId"]?.toIntOrNull()
            if (zoneId == null) {
                call.respond(HttpStatusCode.BadRequest, "El zoneId debe ser un número")
                return@put
            }

            try {
                val request = call.receive<UpdateStudyZoneRequest>()

                val existingZone = studyZoneService.getStudyZoneById(zoneId)
                val updatedZone = existingZone.copy(
                    studyZoneName = request.studyZoneName,
                    studyZoneDescription = request.studyZoneDescription,
                    squareArea = request.squareArea
                )

                val result = studyZoneService.updateStudyZone(zoneId, updatedZone)
                call.respond(HttpStatusCode.OK, result)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Zona no encontrada")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al actualizar: ${e.message}")
                )
            }
        }
    }
}