package com.sylvara.data.postgres

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object SpeciesTable : Table("species") {
    val id = integer("species_id").autoIncrement()
    val projectId = integer("project_id").references(ProjectTable.id)
    val name = varchar("species_name", 255)
    val photo = varchar("species_photo", 500).nullable()
    val functionalTypeId = integer("functional_type_id").references(FunctionalTypeTable.id)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}