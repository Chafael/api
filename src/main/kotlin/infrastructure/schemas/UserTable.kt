package com.sylvara.data.postgres

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.CurrentDateTime

object UserTable : Table("users") {
    val id = integer("user_id").autoIncrement()
    val name = varchar("user_name", 100)
    val lastname = varchar("user_lastname", 100)
    val birthday = date("user_birthday")
    val email = varchar("user_email", 255).uniqueIndex()
    val password = varchar("user_password", 255)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}
