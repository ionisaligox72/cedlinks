package com.beust.cedlinks

import org.slf4j.LoggerFactory
import retrofit2.http.Query
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Path("/")
class CedLinksService {
    private val log = LoggerFactory.getLogger(CedLinksService::class.java)
    private lateinit var pocket: Pocket

    @POST
    @Path("postLink")
    fun hello(@Query("url") url: String): Response {
        pocket = Pocket("https://e233218ce38d.ngrok.io/redirect")
        return Response.ok().build()
    }

    @GET
    @Path("redirect")
    fun redirect(): Response {
        log.info("Redirect called")
        pocket.authorize()
        pocket.addAndArchive("https://beust.com", "My web site")
        return Response.ok().build()
    }

}

