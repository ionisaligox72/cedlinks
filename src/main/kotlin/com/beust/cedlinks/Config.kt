package com.beust.cedlinks

object Config {
    private val lp = LocalProperties()

    private fun envOrLocal(name: String): String = System.getenv(name) ?: lp.get(name)

    val jdbcUrl = envOrLocal("JDBC_URL")
    val jdbcUser = envOrLocal("JDBC_USER")
    val jdbcPassword = envOrLocal("JDBC_PASSWORD")
    val wpUser = envOrLocal("WP_USER")
    val wpPassword = envOrLocal("WP_PASSWORD")
    val host = envOrLocal("HOST")
    val time = envOrLocal("TIME")
}