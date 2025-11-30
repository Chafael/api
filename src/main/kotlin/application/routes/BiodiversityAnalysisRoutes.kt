package com.sylvara.application.routes

import com.sylvara.application.services.StudyZoneService
import com.sylvara.domain.models.BiodiversityAnalysis
import com.sylvara.domain.models.ZoneBiodiversity
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.biodiversityAnalysisRoutes(studyZoneService: StudyZoneService) {
    route("/biodiversity-analysis/{projectId}") {

        // GET: Datos de cada zona de estudio con sus índices de biodiversidad
        get {
            val projectId = call.parameters["projectId"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "El projectId debe ser un número")
                return@get
            }

            try {
                val zones = studyZoneService.getStudyZonesByProjectId(projectId)

                val zoneBiodiversity = zones.map { zone ->
                    val details = studyZoneService.getStudyZoneDetails(zone.studyZoneId)
                    ZoneBiodiversity(
                        studyZoneId = zone.studyZoneId,
                        studyZoneName = zone.studyZoneName,
                        squareArea = zone.squareArea,
                        indices = details.biodiversityIndices
                    )
                }

                val analysis = BiodiversityAnalysis(zones = zoneBiodiversity)
                call.respond(HttpStatusCode.OK, analysis)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al generar análisis: ${e.message}")
                )
            }
        }
    }
}