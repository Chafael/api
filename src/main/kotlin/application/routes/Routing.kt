package com.sylvara.plugins

import com.sylvara.application.routes.*
import com.sylvara.application.services.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

/**
 * Configuración central de todas las rutas de la aplicación.
 * Este archivo actúa como orquestador que delega a cada módulo de rutas.
 */
fun Application.configureRouting(
    userService: UserService,
    projectService: ProjectService,
    studyZoneService: StudyZoneService,
    functionalTypeService: FunctionalTypeService,
    speciesService: SpeciesService,
    speciesZoneService: SpeciesZoneService
) {
    routing {
        homeRoutes(projectService)
        userRoutes(userService)
        projectRoutes(projectService)
        studyZoneRoutes(studyZoneService)
        functionalTypeRoutes(functionalTypeService)
        speciesRoutes(speciesService)
        speciesZoneRoutes(speciesZoneService)
    }
}