package com.beust.cedlinks

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Value
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Controller("/hello")
class HelloController @Inject constructor(private val db: Database) {
    @Get("/")
    @Produces(MediaType.TEXT_PLAIN)
    fun index(): String {
        val context = ApplicationContext.run()
        val environment = context.environment
        val port = environment.getProperty("database.port", String::class.java)
        return "Hello World: " + db.port
    }
}

@Singleton
class Database {
    @Value("\${database.port}")
    var port: Int = 0
}
