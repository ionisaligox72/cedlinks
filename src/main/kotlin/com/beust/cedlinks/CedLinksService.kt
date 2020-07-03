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
    private lateinit var pocket: Pocket

    private fun run(body: () -> Any): Response {
        return try {
            body()
            Response.ok()
        } catch(ex: Exception) {
            log.error("Error", ex)
            Response.serverError()
        }.build()
    }

    @POST
    @Path("postLink")
    @Produces(MediaType.APPLICATION_JSON)
    fun postLink(@QueryParam("url") url: String, @QueryParam("title") title: String,
            @QueryParam("tags") tags: String): Any {
        log.info("postLink called: $url - $title")
        this.url = url
        this.title = title
        this.tags = tags
        try {
            this.pocket = Pocket("https://8e33b9d3da51.ngrok.io/redirect")
            val result = pocket.getRedirectUrl()
            return """
                {
                   "redirectUrl": "$result"
                }
            """.trimIndent()
        } catch(ex: Exception) {
            return Response.serverError().build()
        }
    }

    @GET
    @Path("redirect")
    fun redirect(): Response {
        log.info("Redirect called")
        return run {
            pocket.authorize()
            pocket.addAndArchive(url, title, tags)
        }
    }

}

