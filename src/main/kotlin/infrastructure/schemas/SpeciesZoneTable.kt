package com.sylvara.data.postgres

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object SpeciesZoneTable : Table("species_zone") {
    val id = integer("species_zone_id").autoIncrement()
    val speciesId = integer("species_id").references(SpeciesTable.id)
    val studyZoneId = integer("study_zone_id").references(StudyZoneTable.id)
    val samplingUnit = varchar("sampling_unit", 100).nullable()
    val individualCount = integer("individual_count").nullable()
    val heightStratum = varchar("height_stratum", 100).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}