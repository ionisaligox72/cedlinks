package com.beust.cedlinks

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import org.slf4j.LoggerFactory

import javax.inject.Inject

@Controller("/podcasts")
class PodcastController @Inject constructor(private val dao: Dao): BaseController() {
    private val log = LoggerFactory.getLogger(PodcastController::class.java)

    @Get("rss")
    @Produces(MediaType.APPLICATION_XML)
    fun rss(): String = dao.rss()

    @Get("submit")
    @Produces(MediaType.TEXT_HTML)
    fun submitPodcast(@QueryValue("time") time: String? = null): String = dao.submitPodcast(time)

    @Post("insert")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    fun insertPodcast(url: String, title: String, time: String): HttpResponse<String> =
        timeRequest(time) {
            dao.insertPodcast(url, title)
            HttpResponse.ok<String>("Podcast added")
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