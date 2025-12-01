package com.sylvara.application.services

import com.sylvara.domain.models.User
import com.sylvara.domain.models.UserProfile
import com.sylvara.domain.ports.UserRepository
import com.sylvara.infrastructure.security.PasswordHasher
import io.ktor.server.plugins.*
import java.time.LocalDate
import java.time.Period

class UserService(private val userRepository: UserRepository) {

    suspend fun registerUser(user: User): User {
        if (user.userName.isBlank() || user.userEmail.isBlank()) {
            throw IllegalArgumentException("El nombre y el email son obligatorios.")
        }

        val existingUser = userRepository.findByEmail(user.userEmail)
        if (existingUser != null) {
            throw IllegalArgumentException("El correo electrónico '${user.userEmail}' ya está registrado.")
        }

        if (user.userPassword.length < 6) {
            throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.")
        }

        val hashedPassword = PasswordHasher.hashPassword(user.userPassword)
        val userWithHashedPassword = user.copy(userPassword = hashedPassword)

        return userRepository.save(userWithHashedPassword)
    }

    suspend fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    suspend fun getUserById(id: Int): User {
        return userRepository.findById(id)
            ?: throw NotFoundException("No se encontró el usuario con ID $id")
    }

    suspend fun updateUser(id: Int, user: User): User {
        val existingUser = userRepository.findById(id)
            ?: throw NotFoundException("Usuario con ID $id no encontrado.")

        val userToUpdate = if (user.userPassword != existingUser.userPassword) {
            user.copy(
                userId = id,
                userPassword = PasswordHasher.hashPassword(user.userPassword)
            )
        } else {
            user.copy(userId = id)
        }

        return userRepository.update(userToUpdate)
    }

    suspend fun deleteUser(id: Int) {
        val exists = userRepository.findById(id) != null
        if (!exists) {
            throw NotFoundException("No se puede borrar. Usuario con ID $id no existe.")
        }
        userRepository.delete(id)
    }

    suspend fun getUserProfile(userId: Int): UserProfile {
        val user = userRepository.findById(userId)
            ?: throw NotFoundException("Usuario con ID $userId no encontrado")

        val age = calculateAge(user.userBirthday)

        return UserProfile(
            userId = user.userId,
            fullName = "${user.userName} ${user.userLastname}",
            biography = user.biography,
            email = user.userEmail,
            age = age,
            birthday = user.userBirthday
        )
    }

    private fun calculateAge(birthDate: LocalDate): Int {
        return Period.between(birthDate, LocalDate.now()).years
    }
}