package com.beust.cedlinks

fun testDb() {
    val dbUrl = "jdbc:postgresql:cedlinks"
    val user = "postgres"
    val password = "cedricbeust"
    org.jetbrains.exposed.sql.Database.connect(dbUrl, driver = "org.postgresql.Driver",
            user = user, password = password)
    DbMigration().execute("cedlinks")
    insertLink("https://beust.com")
}

fun main(args: Array<String>) {
//    testDb()
    CedLinksApp().run(*args)
}
