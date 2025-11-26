package com.sylvara.data.postgres

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object StudyZoneTable : Table("study_zone") {
    val id = integer("study_zone_id").autoIncrement()
    val projectId = integer("project_id").references(ProjectTable.id)
    val name = varchar("study_zone_name", 255)
    val description = text("study_zone_description").nullable()
    val squareArea = decimal("square_area", 10, 2)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}