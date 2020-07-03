package com.beust.cedlinks

import org.joda.time.DateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Dates {
    fun formatDate(ld: LocalDateTime): String {
        return ld.format(DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm"))
    }

    fun formatShortDate(ld: LocalDateTime): String {
        return ld.format(DateTimeFormatter.ofPattern("MM/dd/YYYY"))
    }
}