package com.beust.cedlinks

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object Links: Table("links") {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val url = text("url")
}

fun insertLink(url: String) {
    transaction {
        Links.insert {
            it[Links.url] = url
        }
    }
}