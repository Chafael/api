package com.sylvara.domain.ports

import com.sylvara.domain.models.User

interface UserRepository {
    suspend fun save(user: User): User
    suspend fun findById(id: Int): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findAll(): List<User>
    suspend fun update(user: User): User
    suspend fun delete(id: Int)
}