package com.beust.cedlinks

import org.slf4j.LoggerFactory
import java.net.URI
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/")
class CedLinksService {
    private val log = LoggerFactory.getLogger(CedLinksService::class.java)
    private val dao = Dao()

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    fun list(@QueryParam("all") all: Boolean = false) = dao.listLinks(all)

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("insertLink")
    fun insertLink(@FormParam("url") url: String, @FormParam("title") title: String,
            @FormParam("comment") comment: String, @FormParam("imageUrl") imageUrl: String? = null,
            @FormParam("time") time: String): Response {
        val result = try {
            if (time != Config.time) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.insertLink(url, title, comment, imageUrl)
                Response.seeOther(URI(url))
            }
        } catch(ex: Exception) {
            log.error("Error: " + ex.message, ex)
            Response.serverError().entity(ex.message)
        }
        return result.build()
    }

    @GET
    @Path("publish")
    fun publish(@QueryParam("markPublished") markPublished: Boolean = true) = dao.publish(markPublished)

    @GET
    @Path("preview")
    @Produces(MediaType.TEXT_HTML)
    fun publish(): String = dao.preview()

    @GET
    @Path("submit")
    @Produces(MediaType.TEXT_HTML)
    fun submitLink(
            @QueryParam("url") url: String = "",
            @QueryParam("title") title: String? = null,
            @QueryParam("comment") comment: String? = null,
            @QueryParam("time") time: String? = null
        ): String {
        val result = Template.render("submitLink.mustache", mapOf(
                "url" to url,
                "comment" to comment,
                "title" to title,
                "host" to Config.host,
                "time" to time
        ))
        return result
    }

    @GET
    @Path("ping")
    fun ping() = "pong"
}

