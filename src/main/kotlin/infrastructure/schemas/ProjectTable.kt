package com.sylvara.data.postgres

import com.sylvara.infrastructure.schemas.UserTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object ProjectTable : Table("projects") {
    val id = integer("project_id").autoIncrement()
    val userId = integer("user_id").references(UserTable.id)
    val name = varchar("project_name", 255)
    val status = varchar("project_status", 50).default("activo")
    val description = text("project_description").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}