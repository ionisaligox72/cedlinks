package com.beust.cedlinks

import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.sql.Statement

class DbMigration {
    private val log = LoggerFactory.getLogger(DbMigration::class.java)

    companion object {
        private const val SCHEMA = "cedlinks"
        private const val TABLE = "cedlinksmeta"
        private const val COLUMN = "version"
        private const val ID = 0
    }

    private val tableExists: Boolean
        get() = transaction {
            val result = statement.executeQuery("""
                SELECT 1 FROM information_schema.tables WHERE table_catalog='$SCHEMA' AND table_name='$TABLE';
            """.trimIndent())
            result.next()
        }

    private val statement: Statement
        get() = TransactionManager.current().connection.createStatement()

    private var version: Int
        get() = transaction {
            val result = statement.executeQuery("SELECT $COLUMN FROM $TABLE WHERE id=$ID")
            result.next()
            result.getInt(result.findColumn(COLUMN))
        }
        set(newVersion) = transaction {
            statement.execute("UPDATE $TABLE SET $COLUMN=$newVersion where id=$ID")
        }

    private fun upgrade(from: Int, to: Int, block: () -> Boolean) {
        log.info("Current database version is $from, upgrading to version $to")
        try {
            transaction {
                block()
                version = to
                log.info("... successfully upgraded to version 2")
            }
        } catch(ex: Throwable) {
            log.info("... failed to upgrade to version 2: " + ex.message)
            throw ex
        }
    }

    fun execute() {
        init()
        transaction {
            statement.execute("CREATE TABLE IF NOT EXISTS links" +
                    " (id serial, url TEXT, imageUrl TEXT, title TEXT, comment TEXT, saved TEXT, published TEXT)")
        }
        if (version == 1) {
            upgrade(1, 2) {
                statement.execute("CREATE TABLE IF NOT EXISTS podcasts (id serial, url TEXT, title TEXT, saved TEXT)")
            }
        }
//        transaction {
//            statement.execute("CREATE DATABASE $databaseName")
//        }
    }

    private fun init() {
        transaction {
            if (!tableExists) {
                statement.execute(
                        "CREATE TABLE IF NOT EXISTS $TABLE (id integer NOT NULL, version INTEGER DEFAULT 1)")
                statement.execute("INSERT INTO $TABLE VALUES($ID, 1)")
            }
        }
    }
}