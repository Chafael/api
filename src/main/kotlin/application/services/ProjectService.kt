package com.sylvara.application.services

import com.sylvara.domain.models.*
import com.sylvara.domain.ports.ProjectRepository
import com.sylvara.domain.ports.StudyZoneRepository
import io.ktor.server.plugins.*

class ProjectService(
    private val projectRepository: ProjectRepository,
    private val studyZoneRepository: StudyZoneRepository
) {

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

    // MÉTODO PARA HOME
    suspend fun getHomeStats(): HomeStats {
        val activeProjects = projectRepository.findActiveProjects()

        val activeProjectsInfo = activeProjects.map { project ->
            val studyZones = studyZoneRepository.findByProjectId(project.projectId)
            ActiveProjectInfo(
                projectName = project.projectName,
                projectStatus = project.projectStatus,
                totalStudyZones = studyZones.size
            )
        }

        return HomeStats(
            activeProjects = activeProjectsInfo,
            totalProjects = projectRepository.countAll(),
            monthlyProjects = projectRepository.countThisMonth()
        )
    }

    // NUEVO MÉTODO PARA DETALLES DEL PROYECTO
    suspend fun getProjectDetails(projectId: Int): ProjectDetails {
        val project = projectRepository.findById(projectId)
            ?: throw NotFoundException("Proyecto con ID $projectId no encontrado")

        val studyZones = studyZoneRepository.findByProjectId(projectId)

        val studyZonesInfo = studyZones.map { zone ->
            StudyZoneInfo(
                studyZoneId = zone.studyZoneId,
                studyZoneName = zone.studyZoneName,
                squareArea = zone.squareArea
            )
        }

        return ProjectDetails(
            projectId = project.projectId,
            projectName = project.projectName,
            projectDescription = project.projectDescription,
            projectStatus = project.projectStatus,
            createdAt = project.createdAt,
            studyZones = studyZonesInfo
        )
    }

    // Metodo para actualizar el nombre y la descripcion del proyecto
    suspend fun updateProjectBasicInfo(id: Int, name: String, description: String?): Project {
        val existingProject = projectRepository.findById(id)
            ?: throw NotFoundException("Proyecto con ID $id no encontrado")

        if (name.isBlank()) {
            throw IllegalArgumentException("El nombre del proyecto no puede estar vacío")
        }

        val projectToUpdate = existingProject.copy(
            projectName = name,
            projectDescription = description
        )

        return projectRepository.update(projectToUpdate)
    }
}