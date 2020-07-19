package com.beust.cedlinks

import io.micronaut.runtime.Micronaut.*

fun initDb() {
    val dbUrl = Config.jdbcUrl
    org.jetbrains.exposed.sql.Database.connect(dbUrl, driver = "org.postgresql.Driver",
            user = Config.jdbcUser, password = Config.jdbcPassword)
    DbMigration().execute()
}


fun main(args: Array<String>) {
    initDb()
    build()
        .args(*args)
        .packages("com.beust.cedlinks")
        .start()
}

//fun main(args: Array<String>) {
//    initDb()
//    CedLinksApp().run(*args)
//}
