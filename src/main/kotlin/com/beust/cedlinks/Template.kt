package com.beust.cedlinks

import com.github.mustachejava.DefaultMustacheFactory
import java.io.StringWriter

object Template {
    private val mf = DefaultMustacheFactory()

    fun render(file: String, map: Any?): String {
        mf.compile(file).let { mustache ->
            StringWriter().let { writer ->
                mustache.execute(writer, map).flush()
                return writer.toString()
            }
        }
    }
}