package com.beust.cedlinks

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.annotation.Produces
import org.slf4j.LoggerFactory
import java.net.URI
import javax.inject.Inject

@Controller("/")
class CedLinkController @Inject constructor(private val dao: Dao): BaseController() {
    private val log = LoggerFactory.getLogger(CedLinkController::class.java)

    @Get("list")
    @Produces
    fun list(@QueryValue("all", defaultValue = "false") all: Boolean) = dao.listLinks(all)

    @Get("submit")
    @Produces(MediaType.TEXT_HTML)
    fun submitLink(
            @QueryValue("url") url: String = "",
            @QueryValue("title") title: String? = null,
            @QueryValue("comment") comment: String? = null,
            @QueryValue("time") time: String? = null
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

    @Post("insertLink")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    fun insertLink(url: String, title: String,
            comment: String, imageUrl: String? = null,
            time: String): HttpResponse<String> =
        timeRequest(time) {
            dao.insertLink(url, title, comment, imageUrl)
            HttpResponse.redirect<String>(URI(url))
        }

    @Get("preview")
    @Produces(MediaType.TEXT_HTML)
    fun publish(): String = dao.preview()
}
