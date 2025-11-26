package com.sylvara.application.routes

import com.sylvara.application.services.ProjectService
import com.sylvara.domain.models.Project
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.projectRoutes(projectService: ProjectService) {
    route("/projects") {

        // GET: Obtener todos los proyectos
        get {
            val projects = projectService.getAllProjects()
            call.respond(projects)
        }

        // GET: Obtener proyecto por ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "El ID debe ser un número")
                return@get
            }
            try {
                val project = projectService.getProjectById(id)
                call.respond(project)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Proyecto no encontrado")
            }
        }

        // GET: Obtener proyectos por usuario
        get("/user/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "El userId debe ser un número")
                return@get
            }
            val projects = projectService.getProjectsByUserId(userId)
            call.respond(projects)
        }

        // POST: Crear proyecto
        post {
            try {
                val projectParams = call.receive<Project>()
                val savedProject = projectService.createProject(projectParams)
                call.respond(HttpStatusCode.Created, savedProject)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
            }
        }

        // PUT: Actualizar proyecto
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@put
            }
            try {
                val projectParams = call.receive<Project>()
                val updatedProject = projectService.updateProject(id, projectParams)
                call.respond(HttpStatusCode.OK, updatedProject)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Proyecto no encontrado")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error al actualizar: ${e.message}")
            }
        }

        // DELETE: Borrar proyecto
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }
            try {
                projectService.deleteProject(id)
                call.respond(HttpStatusCode.NoContent)
            } catch (e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Proyecto no existe")
            }
        }
    }
}