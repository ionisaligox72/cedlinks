package com.beust.cedlinks

import org.slf4j.LoggerFactory
import retrofit2.http.Query
import java.lang.IllegalArgumentException
import javax.annotation.PostConstruct
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/")
class CedLinksService {
    private val log = LoggerFactory.getLogger(CedLinksService::class.java)
    private val consumerKey = "92219-66fd3a57ea0c8d5e5746cd7b"
    private lateinit var pocket: Pocket

    @POST
    @Path("postLink")
    fun hello(@Query("url") url: String): Response {
        pocket = Pocket("https://localhost:9000/redirect")
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

