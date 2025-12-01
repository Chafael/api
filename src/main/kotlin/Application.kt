package com.sylvara

import com.sylvara.application.services.*
import com.sylvara.infrastructure.DatabaseFactory
import com.sylvara.infrastructure.adapters.*
import com.sylvara.infrastructure.plugins.configureSecurity
import com.sylvara.infrastructure.plugins.configureSerialization
import com.sylvara.plugins.configureRouting
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureSecurity()

    // --- CORS (permitir cualquier puerto de frontend) ---
    install(CORS) {
        anyHost()  // Permitir cualquier origen (solo en desarrollo)

        allowCredentials = true

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.Accept)

        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }
    // ----------------------------------------------------

    DatabaseFactory.init()

    val userRepository = UserRepositoryImpl()
    val projectRepository = ProjectRepositoryImpl()
    val studyZoneRepository = StudyZoneRepositoryImpl()
    val functionalTypeRepository = FunctionalTypeRepositoryImpl()
    val speciesRepository = SpeciesRepositoryImpl()
    val speciesZoneRepository = SpeciesZoneRepositoryImpl()

    val authService = AuthService(userRepository)
    val userService = UserService(userRepository)
    val projectService = ProjectService(projectRepository, studyZoneRepository)
    val studyZoneService = StudyZoneService(studyZoneRepository, speciesZoneRepository)
    val functionalTypeService = FunctionalTypeService(functionalTypeRepository)
    val speciesService = SpeciesService(speciesRepository, speciesZoneRepository, functionalTypeRepository)
    val speciesZoneService = SpeciesZoneService(speciesZoneRepository)

    configureRouting(
        authService,
        userService,
        projectService,
        studyZoneService,
        functionalTypeService,
        speciesService,
        speciesZoneService
    )
}