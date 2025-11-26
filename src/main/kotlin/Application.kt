package com.sylvara

import com.sylvara.application.services.UserService
import com.sylvara.infrastructure.DatabaseFactory // Importa el archivo que acabamos de crear
import com.sylvara.infrastructure.adapters.UserRepositoryImpl
import com.sylvara.infrastructure.plugins.configureSerialization
import com.sylvara.plugins.configureRouting
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()

    // 1. AQUÍ ENCHUFAMOS LA LÁMPARA
    DatabaseFactory.init()

    // 2. Luego creamos los repositorios y servicios
    val userRepository = UserRepositoryImpl()
    val userService = UserService(userRepository)

    configureRouting(userService)
}