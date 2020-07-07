package com.beust.cedlinks

import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * Encapsulate read access to local.properties.
 */
class LocalProperties {
    private val localProperties: Properties by lazy {
        val result = Properties()
        val filePath = Paths.get("local.properties")
        filePath.let { path ->
            if (path.toFile().exists()) {
                Files.newInputStream(path).use {
                    result.load(it)
                }
            }
        }

        result
    }

    fun getNoThrows(name: String): String? = localProperties.getProperty(name)

    fun get(name: String) : String = getNoThrows(name)
                ?: throw IllegalArgumentException("Couldn't find $name in local.properties")
}
