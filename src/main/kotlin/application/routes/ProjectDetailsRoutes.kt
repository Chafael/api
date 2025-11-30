package com.sylvara.application.routes

import com.sylvara.application.services.ProjectService
import com.sylvara.application.services.StudyZoneService
import com.sylvara.domain.models.StudyZone
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProjectBasicInfoRequest(
    val projectName: String,
    val projectDescription: String?
)

fun Routing.projectDetailsRoutes(
    projectService: ProjectService,
    studyZoneService: StudyZoneService
) {
    route("/project-details/{projectId}") {

        // GET 1: Obtener datos del proyecto (Nombre, descripción, estatus)
        get {
            val projectId = call.parameters["projectId"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "El projectId debe ser un número")
                return@get
            }

            try {
                val projectDetails = projectService.getProjectDetails(projectId)
                call.respond(HttpStatusCode.OK, projectDetails)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Proyecto no encontrado")
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al obtener detalles: ${e.message}")
                )
            }
        }

        // GET 2: Obtener datos de las zonas de estudio (Nombre y extensión)
        get("/study-zones") {
            val projectId = call.parameters["projectId"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "El projectId debe ser un número")
                return@get
            }

            try {
                val studyZones = studyZoneService.getStudyZonesByProjectId(projectId)
                call.respond(HttpStatusCode.OK, studyZones)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al obtener zonas: ${e.message}")
                )
            }
        }

        // POST: Crear zona de estudio
        post("/study-zones") {
            val projectId = call.parameters["projectId"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "El projectId debe ser un número")
                return@post
            }

            try {
                val studyZoneParams = call.receive<StudyZone>()

                // Validar que el projectId de la zona coincida con el de la URL
                if (studyZoneParams.projectId != projectId) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "El projectId de la zona no coincide con la URL")
                    )
                    return@post
                }

                val savedZone = studyZoneService.createStudyZone(studyZoneParams)
                call.respond(HttpStatusCode.Created, savedZone)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al crear zona: ${e.message}")
                )
            }
        }

        // PUT: Actualizar proyecto (Nombre y descripción)
        put {
            val projectId = call.parameters["projectId"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "El projectId debe ser un número")
                return@put
            }

            try {
                val request = call.receive<UpdateProjectBasicInfoRequest>()
                val updatedProject = projectService.updateProjectBasicInfo(
                    projectId,
                    request.projectName,
                    request.projectDescription
                )
                call.respond(HttpStatusCode.OK, updatedProject)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Proyecto no encontrado")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Error al actualizar: ${e.message}")
                )
            }
        }

        // DELETE: Eliminar zona de estudio
        delete("/study-zones/{zoneId}") {
            val projectId = call.parameters["projectId"]?.toIntOrNull()
            val zoneId = call.parameters["zoneId"]?.toIntOrNull()

            if (projectId == null || zoneId == null) {
                call.respond(HttpStatusCode.BadRequest, "IDs inválidos")
                return@delete
            }

            try {
                // Verificar que la zona pertenece al proyecto
                val zone = studyZoneService.getStudyZoneById(zoneId)
                if (zone.projectId != projectId) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        mapOf("error" to "La zona no pertenece a este proyecto")
                    )
                    return@delete
                }

                studyZoneService.deleteStudyZone(zoneId)
                call.respond(HttpStatusCode.NoContent)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Zona no encontrada")
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al eliminar: ${e.message}")
                )
            }
        }
    }
}