package com.beust.cedlinks

import java.time.LocalDateTime
import javax.inject.Singleton

@Singleton
class Rss {
    data class Feed(val date: String, val items: List<Item>)
    data class Item(val title: String, val link: String, val description: String = "")
    private val items = listOf(Item("Item 1", "Link 1"), Item("Item 2", "Link 2"))
    private val currentFeed = Feed(Dates.formatDate(LocalDateTime.now()), items)

    val feed: String
        get() {
            val result = Template.render("rss.mustache", currentFeed)
            return result
        }
}