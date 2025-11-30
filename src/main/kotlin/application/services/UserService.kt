package com.sylvara.application.services

import com.sylvara.domain.models.User
import com.sylvara.domain.models.UserProfile
import com.sylvara.domain.ports.UserRepository
import io.ktor.server.plugins.*
import java.time.LocalDate
import java.time.Period

class UserService(private val userRepository: UserRepository) {

    // 1. REGISTRAR USUARIO (Con validaciones)
    suspend fun registerUser(user: User): User {
        // Validación A: Datos obligatorios
        if (user.userName.isBlank() || user.userEmail.isBlank()) {
            throw IllegalArgumentException("El nombre y el email son obligatorios.")
        }

        // Validación B: Verificar si el email ya existe (Regla de negocio crítica)
        val existingUser = userRepository.findByEmail(user.userEmail)
        if (existingUser != null) {
            throw IllegalArgumentException("El correo electrónico '${user.userEmail}' ya está registrado.")
        }

        // Validación C: Password segura (simplificado)
        if (user.userPassword.length < 6) {
            throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.")
        }

        // Si todo pasa, guardamos
        return userRepository.save(user)
    }

    // 2. OBTENER TODOS
    suspend fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    // 3. OBTENER POR ID (Manejo de nulidad)
    suspend fun getUserById(id: Int): User {
        return userRepository.findById(id)
            ?: throw NotFoundException("No se encontró el usuario con ID $id")
    }

    // 4. ACTUALIZAR (Validando existencia)
    suspend fun updateUser(id: Int, user: User): User {
        // Verificar que el usuario exista antes de intentar actualizar
        val existingUser = userRepository.findById(id)
            ?: throw NotFoundException("Usuario con ID $id no encontrado.")

        // Asegurarnos que el objeto a actualizar tenga el ID correcto
        // (Por si el JSON traía otro ID diferente al de la URL)
        val userToUpdate = user.copy(userId = id)

        return userRepository.update(userToUpdate)
    }

    // 5. BORRAR
    suspend fun deleteUser(id: Int) {
        // Opcional: Verificar si existe antes de borrar
        val exists = userRepository.findById(id) != null
        if (!exists) {
            throw NotFoundException("No se puede borrar. Usuario con ID $id no existe.")
        }
        userRepository.delete(id)
    }
    // NUEVO: Obtener perfil de usuario
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

    // NUEVO: Calcular edad
    private fun calculateAge(birthDate: LocalDate): Int {
        return Period.between(birthDate, LocalDate.now()).years
    }
}
