package com.beust.cedlinks

import org.slf4j.LoggerFactory
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/")
class CedLinksService {
    private val log = LoggerFactory.getLogger(CedLinksService::class.java)
    private lateinit var url: String
    private lateinit var title: String
    private lateinit var tags: String

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    fun list() = Dao().listLinks()

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("insertLink")
    fun insertLink(@FormParam("url") url: String, @FormParam("title") title: String,
            @FormParam("comment") comment: String, @FormParam("imageUrl") imageUrl: String? = null): Response {
        try {
            Dao().insertLink(url, title, comment, imageUrl)
            return Response.ok().build()
        } catch(ex: Exception) {
            log.error("Error: " + ex.message, ex)
            return Response.serverError().build()
        }
    }

    @POST
    @Path("publish")
    fun publish(): Response {
        Dao().publish()
        return Response.ok().build()
    }

    @GET
    @Path("ping")
    fun ping() = "pong"
}

