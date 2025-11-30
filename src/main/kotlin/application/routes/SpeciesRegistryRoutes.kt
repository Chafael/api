package com.sylvara.application.routes

import com.sylvara.application.services.SpeciesService
import com.sylvara.domain.models.CreateSpeciesRequest
import com.sylvara.domain.models.UpdateSpeciesRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.speciesRegistryRoutes(speciesService: SpeciesService) {
    route("/species-registry/zone/{zoneId}") {

        // GET: Datos de cada especie (Nombre, unidad de muestreo, tipo funcional,
        get {
            val zoneId = call.parameters["zoneId"]?.toIntOrNull()
            if (zoneId == null) {
                call.respond(HttpStatusCode.BadRequest, "El zoneId debe ser un número")
                return@get
            }

            try {
                val speciesDetails = speciesService.getSpeciesDetailsByZone(zoneId)
                call.respond(HttpStatusCode.OK, speciesDetails)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al obtener especies: ${e.message}")
                )
            }
        }

        // POST: Registro de especie completo
        post {
            val zoneId = call.parameters["zoneId"]?.toIntOrNull()
            if (zoneId == null) {
                call.respond(HttpStatusCode.BadRequest, "El zoneId debe ser un número")
                return@post
            }

            try {
                val request = call.receive<CreateSpeciesRequest>()

                // Validar que el zoneId coincida
                if (request.studyZoneId != zoneId) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "El studyZoneId no coincide con la URL")
                    )
                    return@post
                }

                val (species, speciesZone) = speciesService.createCompleteSpecies(request)
                call.respond(
                    HttpStatusCode.Created,
                    mapOf("species" to species, "speciesZone" to speciesZone)
                )
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al crear especie: ${e.message}")
                )
            }
        }

        // PUT: Actualizar especie
        put("/{speciesId}") {
            val zoneId = call.parameters["zoneId"]?.toIntOrNull()
            val speciesId = call.parameters["speciesId"]?.toIntOrNull()

            if (zoneId == null || speciesId == null) {
                call.respond(HttpStatusCode.BadRequest, "IDs inválidos")
                return@put
            }

            try {
                val request = call.receive<UpdateSpeciesRequest>()
                val (species, speciesZone) = speciesService.updateCompleteSpecies(
                    speciesId,
                    zoneId,
                    request
                )
                call.respond(
                    HttpStatusCode.OK,
                    mapOf("species" to species, "speciesZone" to speciesZone)
                )
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al actualizar: ${e.message}")
                )
            }
        }

        // DELETE: Eliminar especie
        delete("/{speciesId}") {
            val zoneId = call.parameters["zoneId"]?.toIntOrNull()
            val speciesId = call.parameters["speciesId"]?.toIntOrNull()

            if (zoneId == null || speciesId == null) {
                call.respond(HttpStatusCode.BadRequest, "IDs inválidos")
                return@delete
            }

            try {
                speciesService.deleteSpecies(speciesId)
                call.respond(HttpStatusCode.NoContent)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al eliminar: ${e.message}")
                )
            }
        }
    }
}