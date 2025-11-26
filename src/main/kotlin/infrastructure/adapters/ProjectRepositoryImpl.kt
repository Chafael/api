package com.sylvara.infrastructure.adapters

import com.sylvara.data.postgres.ProjectTable
import com.sylvara.domain.models.Project
import com.sylvara.domain.ports.ProjectRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ProjectRepositoryImpl : ProjectRepository {

    private fun rowToProject(row: ResultRow): Project {
        return Project(
            projectId = row[ProjectTable.id],
            userId = row[ProjectTable.userId],
            projectName = row[ProjectTable.name],
            projectStatus = row[ProjectTable.status],
            projectDescription = row[ProjectTable.description],
            createdAt = row[ProjectTable.createdAt]
        )
    }

    override suspend fun save(project: Project): Project {
        return transaction {
            val insertStatement = ProjectTable.insert {
                it[userId] = project.userId
                it[name] = project.projectName
                it[status] = project.projectStatus
                it[description] = project.projectDescription
            }
            val newId = insertStatement[ProjectTable.id]
            project.copy(projectId = newId)
        }
    }

    override suspend fun findById(id: Int): Project? {
        return transaction {
            ProjectTable.selectAll()
                .where { ProjectTable.id eq id }
                .map { rowToProject(it) }
                .singleOrNull()
        }
    }

    override suspend fun findByUserId(userId: Int): List<Project> {
        return transaction {
            ProjectTable.selectAll()
                .where { ProjectTable.userId eq userId }
                .map { rowToProject(it) }
        }
    }

    override suspend fun findAll(): List<Project> {
        return transaction {
            ProjectTable.selectAll()
                .map { rowToProject(it) }
        }
    }

    override suspend fun update(project: Project): Project {
        return transaction {
            ProjectTable.update({ ProjectTable.id eq project.projectId }) {
                it[userId] = project.userId
                it[name] = project.projectName
                it[status] = project.projectStatus
                it[description] = project.projectDescription
            }
            project
        }
    }

    override suspend fun delete(id: Int) {
        transaction {
            ProjectTable.deleteWhere { ProjectTable.id eq id }
        }
    }
}