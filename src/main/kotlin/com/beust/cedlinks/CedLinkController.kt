package com.beust.cedlinks

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Value
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

@Controller("/")
class CedLinkController @Inject constructor(private val dao: Dao) {

    @Get("list")
    @Produces(MediaType.TEXT_PLAIN)
    fun list(@QueryValue("all", defaultValue = "false") all: Boolean) = dao.listLinks(all)


//    @Get("/")
//    @Produces(MediaType.TEXT_PLAIN)
//    fun index(@QueryValue("name", defaultValue = "Unknown") name: String): String {
//        val context = ApplicationContext.run()
//        val environment = context.environment
//        val port = environment.getProperty("database.port", String::class.java)
//        return "Hello $name: " + db.port
//    }
}
