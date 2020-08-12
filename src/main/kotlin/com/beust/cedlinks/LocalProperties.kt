package com.beust.cedlinks

import org.jboss.logging.Logger
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * Encapsulate read access to local.properties.
 */
class LocalProperties {
    private val log = Logger.getLogger(LocalProperties::class.java)

    private val DIRS = listOf(Paths.get("."),
            Paths.get(System.getProperty("user.home"), ".settings"))

    private val localProperties: Properties by lazy {
        log.warn("Warning here")
        val result = Properties()
        val lpPath = DIRS.map { Paths.get(it.toString(), "local.properties") }.firstOrNull { Files.exists(it) }
        if (lpPath != null) {
            lpPath.let { path ->
                if (path.toFile().exists()) {
                    Files.newInputStream(path).use {
                        result.load(it)
                    }
                }
            }
        } else {
            log.warn("Warning: couldn't find local.properties")
        }

        result
    }

    private fun getNoThrows(name: String): String? = localProperties.getProperty(name)

    fun get(name: String) : String = getNoThrows(name)
            ?: throw IllegalArgumentException("Couldn't find $name in local.properties")
}
