package com.beust.cedlinks

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory

open class BaseController {
    private val log = LoggerFactory.getLogger(BaseController::class.java)

    protected fun timeRequest(time: String, block: () -> HttpResponse<String>): HttpResponse<String> =
        try {
            if (time != Config.time) {
                HttpResponse.status<String>(HttpStatus.UNAUTHORIZED)
            } else {
                block()
            }
        } catch(ex: Exception) {
            log.error("Error: " + ex.message, ex)
            HttpResponse.serverError<String>().body(ex.message)
        }
}