package com.sylvara.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class HomeStats(
    val activeProjects: List<ActiveProjectInfo>,
    val totalProjects: Int,
    val monthlyProjects: Int,
    val totalAnalysis: Int = 0,
    val analysisThisMonth: Int = 0
)

@Serializable
data class ActiveProjectInfo(
    val projectName: String,
    val projectStatus: String,
    val totalStudyZones: Int
)