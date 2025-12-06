package com.sylvara.application.routes


import com.sylvara.application.services.ProjectService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.homeRoutes(projectService: ProjectService) {
    route("/home") {
        get {
            try {
                val stats = projectService.getHomeStats()
                call.respond(HttpStatusCode.OK, stats)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al obtener estadísticas: ${e.message}")
                )
            }
        }
    }
}