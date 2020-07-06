package com.beust.cedlinks

object Template {
    fun render(file: String, map: Map<String, Any?>): String {
        var result = String(this::class.java.classLoader.getResourceAsStream(file).readBytes())
        map.keys.forEach {
            result = result.replace("{{$it}}", map[it].toString() ?: "")
        }
        return result
    }
}