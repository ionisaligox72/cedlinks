package com.beust.cedlinks

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

object Links: Table("links") {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val url = text("url")
    val title = text("title")
    val comment = text("comment")
    val imageUrl = text("imageurl").nullable()
    val saved = text("saved")
    val published = text("published").nullable()
}

class Dao {
    private val log = LoggerFactory.getLogger(Dao::class.java)

    fun insertLink(url: String, title: String, comment: String, imageUrl: String? = null) {
        transaction {
            Links.insert {
                it[Links.url] = url
                it[Links.title] = title
                it[Links.comment] = comment
                if (imageUrl != null) it[Links.imageUrl] = imageUrl
                it[Links.saved] = Dates.formatDate(LocalDateTime.now())
            }
        }
    }

    fun publish() {
        val links = arrayListOf<Link>()
        val ids = arrayListOf<Int>()
        transaction {
            Links.select {
                Links.published.isNull()
            }.forEach {
                links.add(Link(it[Links.url], it[Links.title], it[Links.comment], it[Links.imageUrl]))
                ids.add(it[Links.id])
                println("not published: $it")
            }
        }

        try {
            Wordpress().postNewArticle(links)
            transaction {
                val date = Dates.formatDate(LocalDateTime.now())
                Links.update({ Links.id.inList(ids) }) {
                    log.info("Updating ids to $date")
                    it[Links.published] = date
                }
            }
        } catch(ex: Exception) {
            log.error("Error posting", ex)
        }
    }
}