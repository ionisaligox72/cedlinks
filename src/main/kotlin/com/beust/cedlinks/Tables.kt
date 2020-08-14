package com.beust.cedlinks

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Links: Table("links") {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val url = text("url")
    val title = text("title")
    val comment = text("comment").nullable()
    val imageUrl = text("imageurl").nullable()
    val saved = text("saved")
    val published = text("published").nullable()
}

data class LinkFromDb(val id: Int, override val url: String, override val title: String, override val comment: String?,
        override val imageUrl: String?, val saved: String? = null, val published: String? = null)
    : Link(url, title, comment, imageUrl)

object Podcasts: Table("podcasts") {
    val id: Column<Int> = Podcasts.integer("id").autoIncrement().primaryKey()
    val url = Podcasts.text("url")
    val title = Podcasts.text("title")
    val saved = Podcasts.text("saved")
}

data class PodcastFromDb(val id: Int, val url: String, val title: String)