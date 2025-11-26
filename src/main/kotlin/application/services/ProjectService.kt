package com.sylvara.application.services

import com.sylvara.domain.models.Project
import com.sylvara.domain.ports.ProjectRepository
import io.ktor.server.plugins.*

class ProjectService(private val projectRepository: ProjectRepository) {

    suspend fun createProject(project: Project): Project {
        if (project.projectName.isBlank()) {
            throw IllegalArgumentException("El nombre del proyecto es obligatorio.")
        }
        return projectRepository.save(project)
    }

    suspend fun getAllProjects(): List<Project> {
        return projectRepository.findAll()
    }

    suspend fun getProjectById(id: Int): Project {
        return projectRepository.findById(id)
            ?: throw NotFoundException("Proyecto con ID $id no encontrado")
    }

    suspend fun getProjectsByUserId(userId: Int): List<Project> {
        return projectRepository.findByUserId(userId)
    }

    suspend fun updateProject(id: Int, project: Project): Project {
        val existingProject = projectRepository.findById(id)
            ?: throw NotFoundException("Proyecto con ID $id no encontrado")

        val projectToUpdate = project.copy(projectId = id)
        return projectRepository.update(projectToUpdate)
    }

    suspend fun deleteProject(id: Int) {
        val exists = projectRepository.findById(id) != null
        if (!exists) {
            throw NotFoundException("Proyecto con ID $id no existe")
        }
        projectRepository.delete(id)
    }
}