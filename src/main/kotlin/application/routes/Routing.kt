package com.sylvara.plugins

import com.sylvara.application.services.*
import com.sylvara.domain.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userService: UserService,
    projectService: ProjectService,
    studyZoneService: StudyZoneService,
    functionalTypeService: FunctionalTypeService,
    speciesService: SpeciesService,
    speciesZoneService: SpeciesZoneService
) {
    routing {

        // ==================== USERS ====================
        route("/users") {
            get {
                val users = userService.getAllUsers()
                call.respond(users)
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "El ID debe ser un número")
                    return@get
                }
                try {
                    val user = userService.getUserById(id)
                    call.respond(user)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "Usuario no encontrado")
                }
            }

            post {
                try {
                    val userParams = call.receive<User>()
                    val savedUser = userService.registerUser(userParams)
                    call.respond(HttpStatusCode.Created, savedUser)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error interno: ${e.message}")
                }
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@put
                }
                try {
                    val userParams = call.receive<User>()
                    val updatedUser = userService.updateUser(id, userParams)
                    call.respond(HttpStatusCode.OK, updatedUser)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "Usuario no encontrado")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error al actualizar: ${e.message}")
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@delete
                }
                try {
                    userService.deleteUser(id)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "Usuario no existe")
                }
            }
        }

        // ==================== PROJECTS ====================
        route("/projects") {
            get {
                val projects = projectService.getAllProjects()
                call.respond(projects)
            }

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

            // Obtener proyectos por usuario
            get("/user/{userId}") {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest, "El userId debe ser un número")
                    return@get
                }
                val projects = projectService.getProjectsByUserId(userId)
                call.respond(projects)
            }

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

        // ==================== STUDY ZONES ====================
        route("/study-zones") {
            get {
                val studyZones = studyZoneService.getAllStudyZones()
                call.respond(studyZones)
            }

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

            // Obtener zonas por proyecto
            get("/project/{projectId}") {
                val projectId = call.parameters["projectId"]?.toIntOrNull()
                if (projectId == null) {
                    call.respond(HttpStatusCode.BadRequest, "El projectId debe ser un número")
                    return@get
                }
                val zones = studyZoneService.getStudyZonesByProjectId(projectId)
                call.respond(zones)
            }

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

        // ==================== FUNCTIONAL TYPES ====================
        route("/functional-types") {
            get {
                val types = functionalTypeService.getAllFunctionalTypes()
                call.respond(types)
            }

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

        // ==================== SPECIES ====================
        route("/species") {
            get {
                val species = speciesService.getAllSpecies()
                call.respond(species)
            }

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

            // Obtener especies por proyecto
            get("/project/{projectId}") {
                val projectId = call.parameters["projectId"]?.toIntOrNull()
                if (projectId == null) {
                    call.respond(HttpStatusCode.BadRequest, "El projectId debe ser un número")
                    return@get
                }
                val species = speciesService.getSpeciesByProjectId(projectId)
                call.respond(species)
            }

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

        // ==================== SPECIES ZONES ====================
        route("/species-zones") {
            get {
                val zones = speciesZoneService.getAllSpeciesZones()
                call.respond(zones)
            }

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

            // Obtener por especie
            get("/species/{speciesId}") {
                val speciesId = call.parameters["speciesId"]?.toIntOrNull()
                if (speciesId == null) {
                    call.respond(HttpStatusCode.BadRequest, "El speciesId debe ser un número")
                    return@get
                }
                val zones = speciesZoneService.getSpeciesZonesBySpeciesId(speciesId)
                call.respond(zones)
            }

            // Obtener por zona de estudio
            get("/study-zone/{studyZoneId}") {
                val studyZoneId = call.parameters["studyZoneId"]?.toIntOrNull()
                if (studyZoneId == null) {
                    call.respond(HttpStatusCode.BadRequest, "El studyZoneId debe ser un número")
                    return@get
                }
                val zones = speciesZoneService.getSpeciesZonesByStudyZoneId(studyZoneId)
                call.respond(zones)
            }

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
}