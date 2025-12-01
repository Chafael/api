package com.sylvara.application.services

import com.sylvara.domain.models.LoginRequest
import com.sylvara.domain.models.LoginResponse
import com.sylvara.domain.models.RegisterRequest
import com.sylvara.domain.models.User
import com.sylvara.domain.ports.UserRepository
import com.sylvara.infrastructure.security.JwtConfig
import com.sylvara.infrastructure.security.PasswordHasher  // ← AÑADE ESTE IMPORT
import io.ktor.server.plugins.*

class AuthService(private val userRepository: UserRepository) {

    suspend fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw NotFoundException("Email o contraseña incorrecta")

        if (!PasswordHasher.checkPassword(request.password, user.userPassword)) {
            throw IllegalArgumentException("Email o contraseña incorrecta")
        }

        val token = JwtConfig.generateToken(user.userId, user.userEmail)

        return LoginResponse(
            token = token,
            userId = user.userId,
            email = user.userEmail
        )
    }

    suspend fun register(request: RegisterRequest): LoginResponse {
        val existingUser = userRepository.findByEmail(request.userEmail)
        if (existingUser != null) {
            throw IllegalArgumentException("El email ya está registrado")
        }

        val hashedPassword = PasswordHasher.hashPassword(request.userPassword)

        val newUser = User(
            userName = request.userName,
            userLastname = request.userLastname,
            userBirthday = request.userBirthday,
            userEmail = request.userEmail,
            userPassword = hashedPassword,  // ← Guardar hash, no texto plano
            biography = request.biography
        )

        val savedUser = userRepository.save(newUser)

        val token = JwtConfig.generateToken(savedUser.userId, savedUser.userEmail)

        return LoginResponse(
            token = token,
            userId = savedUser.userId,
            email = savedUser.userEmail,
            message = "Usuario registrado exitosamente"
        )
    }
}