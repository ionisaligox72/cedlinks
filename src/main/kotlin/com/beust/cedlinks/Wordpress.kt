package com.beust.cedlinks

import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.IOException
import java.time.LocalDateTime

open class Link(open val url: String, open val title: String, open val comment: String?, open val imageUrl: String?)

class Wordpress {
    private val log = LoggerFactory.getLogger(Wordpress::class.java)
    private val dao = Dao()

    class BasicAuthInterceptor(user: String?, password: String?) : Interceptor {
        private val credentials: String = Credentials.basic(user!!, password!!)

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request: Request = chain.request()
            val authenticatedRequest: Request = request.newBuilder()
                    .header("Authorization", credentials).build()
            return chain.proceed(authenticatedRequest)
        }
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val basicAuthInterceptor = BasicAuthInterceptor(Config.wpUser, Config.wpPassword)
    private val gson = GsonBuilder().setLenient().create()
    private val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(basicAuthInterceptor)
            .build()
    private val retrofit = Retrofit.Builder()
            .baseUrl("http://beust.com/weblog/wp-json/wp/v2/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(WordpressService::class.java)

    interface WordpressService {
        @Headers("Content-Type: application/json; charset=UTF-8", "X-Accept: application/json")
        @POST("posts")
        fun post(@Body request: PostRequest): Call<PostResponse>
    }

    @Suppress("unused")
    class PostRequest(val title: String, val content: String, val excerpt: String, val status: String)
    @Suppress("unused")
    class PostResponse(val id: Long, val date: String)

    fun post(title: String, content: String, excerpt: String, draft: Boolean) {
        val status = if (draft) "draft" else "publish"
        val r = retrofit.post(PostRequest(title, content, excerpt, status)).execute()
        log.info("ID: " + r.body()?.id)
    }

    fun postNewArticle(links: List<Link>, draft: Boolean) {
        if (links.isNotEmpty()) {
            val date = Dates.formatShortDate(LocalDateTime.now())
            val title = "Interesting reads - $date"
            val postContent = dao.linksToHtml(links)
            post(title, postContent, title, draft)
        } else {
            log.info("No new links to post")
        }
    }
}

fun main() {
    Wordpress().post("from Kotlin", "content Kotlin", "excerpt Kotlin", draft = true)
}