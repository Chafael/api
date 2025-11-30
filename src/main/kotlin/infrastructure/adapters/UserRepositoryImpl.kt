package com.sylvara.infrastructure.adapters

import com.sylvara.data.postgres.UserTable
import com.sylvara.domain.models.User
import com.sylvara.domain.ports.UserRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl : UserRepository {
    // Mapper
    private fun rowToUser(row: ResultRow): User {
        return User(
            userId = row[UserTable.id],
            userName = row[UserTable.name],
            userLastname = row[UserTable.lastname],
            userBirthday = row[UserTable.birthday],
            userEmail = row[UserTable.email],
            userPassword = row[UserTable.password],
            biography = row[UserTable.biography], // NUEVO CAMPO
            createdAt = row[UserTable.createdAt]
        )
    }

    override suspend fun save(user: User): User {
        return transaction {
            val insertStatement = UserTable.insert {
                it[UserTable.name] = user.userName
                it[UserTable.lastname] = user.userLastname
                it[UserTable.birthday] = user.userBirthday
                it[UserTable.email] = user.userEmail
                it[UserTable.password] = user.userPassword
                it[UserTable.biography] = user.biography // NUEVO CAMPO
            }

            val newId = insertStatement[UserTable.id]
            user.copy(userId = newId)
        }
    }

    override suspend fun findById(id: Int): User? {
        return transaction {
            UserTable.selectAll()
                .where { UserTable.id eq id }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    override suspend fun findByEmail(email: String): User? {
        return transaction {
            UserTable.selectAll()
                .where { UserTable.email eq email }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    override suspend fun findAll(): List<User> {
        return transaction {
            UserTable.selectAll()
                .map { rowToUser(it) }
        }
    }

    override suspend fun update(user: User): User {
        return transaction {
            UserTable.update({ UserTable.id eq user.userId }) {
                it[UserTable.name] = user.userName
                it[UserTable.lastname] = user.userLastname
                it[UserTable.birthday] = user.userBirthday
                it[UserTable.email] = user.userEmail
                it[UserTable.password] = user.userPassword
                it[UserTable.biography] = user.biography // NUEVO CAMPO
            }

            user.copy(userId = user.userId)
        }
    }

    override suspend fun delete(id: Int) {
        return transaction {
            UserTable.deleteWhere { UserTable.id eq id }
        }
    }
}