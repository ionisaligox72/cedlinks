package com.beust.cedlinks

fun initDb() {
    val dbUrl = Config.jdbcUrl
    org.jetbrains.exposed.sql.Database.connect(dbUrl, driver = "org.postgresql.Driver",
            user = Config.jdbcUser, password = Config.jdbcPassword)
    DbMigration().execute()
}

fun main(args: Array<String>) {
    initDb()
    CedLinksApp().run(*args)
}
