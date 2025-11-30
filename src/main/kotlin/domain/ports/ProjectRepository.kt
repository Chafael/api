package com.sylvara.domain.ports

import com.sylvara.domain.models.Project

interface ProjectRepository {
    suspend fun save(project: Project): Project
    suspend fun findById(id: Int): Project?
    suspend fun findByUserId(userId: Int): List<Project>
    suspend fun findAll(): List<Project>
    suspend fun update(project: Project): Project
    suspend fun delete(id: Int)
    suspend fun findActiveProjects(): List<Project>
    suspend fun countAll(): Int
    suspend fun countThisMonth(): Int
}