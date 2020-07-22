package com.beust.cedlinks

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.annotation.Produces
import io.micronaut.http.multipart.StreamingFileUpload
import io.reactivex.Single
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URI
import javax.inject.Inject

@Controller("/")
class CedLinkController @Inject constructor(private val dao: Dao) {
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
            time: String): HttpResponse<String> {
        val result = try {
            if (time != Config.time) {
                HttpResponse.status<String>(HttpStatus.UNAUTHORIZED)
            } else {
                dao.insertLink(url, title, comment, imageUrl)
                HttpResponse.redirect<String>(URI(url))
            }
        } catch(ex: Exception) {
            log.error("Error: " + ex.message, ex)
            HttpResponse.serverError<String>().body(ex.message)
        }
        return result
    }

    @Get("preview")
    @Produces(MediaType.TEXT_HTML)
    fun publish(): String = dao.preview()

    @Get("rss")
    @Produces(MediaType.APPLICATION_XML)
    fun rss(): String = dao.rss()

    @Get("podcast")
    @Produces(MediaType.TEXT_HTML)
    fun podcast(): String = dao.podcast()

    @Post("insertPodcast")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    fun insertPodcast(url: String, title: String, time: String): HttpResponse<String> {
        val result = try {
//            if (time != Config.time) {
//                HttpResponse.status<String>(HttpStatus.UNAUTHORIZED)
//            } else {
                dao.insertPodcast(url, title)
                HttpResponse.ok<String>("Podcast added")
//            }
        } catch(ex: Exception) {
            log.error("Error: " + ex.message, ex)
            HttpResponse.serverError<String>().body(ex.message)
        }
        return result
    }


//    @Post(value = "insertPodcast", consumes = [MediaType.MULTIPART_FORM_DATA], produces = [MediaType.TEXT_PLAIN])
//    fun upload(file: StreamingFileUpload): Single<HttpResponse<String>> {
//        val tempFile = File.createTempFile(file.filename, "temp")
//        val uploadPublisher = file.transferTo(tempFile)
//        return Single.fromPublisher(uploadPublisher)
//                .map { success ->
//                    if (success) {
//                        HttpResponse.ok("Uploaded")
//                    } else {
//                        HttpResponse.status<String>(HttpStatus.CONFLICT)
//                                .body("Upload Failed")
//                    }
//                }
//    }
}
