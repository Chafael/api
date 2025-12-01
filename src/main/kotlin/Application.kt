package com.sylvara

import com.sylvara.application.services.*
import com.sylvara.infrastructure.DatabaseFactory
import com.sylvara.infrastructure.adapters.*
import com.sylvara.infrastructure.plugins.configureSerialization
import com.sylvara.plugins.configureRouting
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()

    // 1. Inicializar base de datos
    DatabaseFactory.init()

    // 2. Crear TODOS los repositorios
    val userRepository = UserRepositoryImpl()
    val projectRepository = ProjectRepositoryImpl()
    val studyZoneRepository = StudyZoneRepositoryImpl()
    val functionalTypeRepository = FunctionalTypeRepositoryImpl()
    val speciesRepository = SpeciesRepositoryImpl()
    val speciesZoneRepository = SpeciesZoneRepositoryImpl()

    // 3. Crear TODOS los servicios (ACTUALIZADOS con dependencias)
    val authService = AuthService(userRepository) // ← NUEVO
    val userService = UserService(userRepository)
    val projectService = ProjectService(projectRepository, studyZoneRepository)
    val studyZoneService = StudyZoneService(studyZoneRepository, speciesZoneRepository)
    val functionalTypeService = FunctionalTypeService(functionalTypeRepository)
    val speciesService = SpeciesService(speciesRepository, speciesZoneRepository, functionalTypeRepository)
    val speciesZoneService = SpeciesZoneService(speciesZoneRepository)

    // 4. Configurar rutas
    configureRouting(
        authService,  // ← NUEVO (añadir como primer parámetro)
        userService,
        projectService,
        studyZoneService,
        functionalTypeService,
        speciesService,
        speciesZoneService
    )
}