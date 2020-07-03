package com.beust.cedlinks

fun initDb() {
    val dbUrl = "jdbc:postgresql:cedlinks"
    val user = "postgres"
    val password = "cedricbeust"
    org.jetbrains.exposed.sql.Database.connect(dbUrl, driver = "org.postgresql.Driver",
            user = user, password = password)
    DbMigration().execute("cedlinks")
}

fun main(args: Array<String>) {
    initDb()
    CedLinksApp().run(*args)
}
