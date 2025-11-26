package com.sylvara.data.postgres

import org.jetbrains.exposed.sql.Table

object FunctionalTypeTable : Table("functional_types") {
    val id = integer("functional_type_id").autoIncrement()
    val name = varchar("functional_type_name", 100).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}