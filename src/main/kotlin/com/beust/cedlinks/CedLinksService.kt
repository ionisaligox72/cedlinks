package com.beust.cedlinks

import org.slf4j.LoggerFactory
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/")
class CedLinksService {
    private val log = LoggerFactory.getLogger(CedLinksService::class.java)

    @GET
    @Path("hello")
    @Produces(MediaType.APPLICATION_JSON)
    fun hello():  String {
        return """
            {
            "value": "hello"
            }
        """.trimIndent()
    }
}

