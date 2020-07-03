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


data class Link(val url: String, val title: String, val comment: String, val imageUrl: String?)

class Wordpress {
    private val log = LoggerFactory.getLogger(Wordpress::class.java)

    class BasicAuthInterceptor(user: String?, password: String?) : Interceptor {
        private val credentials: String

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request: Request = chain.request()
            val authenticatedRequest: Request = request.newBuilder()
                    .header("Authorization", credentials).build()
            return chain.proceed(authenticatedRequest)
        }

        init {
            credentials = Credentials.basic(user, password)
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

    class PostRequest(val title: String, val content: String, val excerpt: String)
    class PostResponse(val id: Long, val date: String)

    fun post(title: String, content: String, excerpt: String) {
        val r = retrofit.post(PostRequest(title, content, excerpt)).execute()
        println("ID: " + r.body()?.id)
    }

    fun postNewArticle(links: List<Link>) {
        if (links.isNotEmpty()) {
            val date = Dates.formatShortDate(LocalDateTime.now())
            val title = "Links for " + date
            val content = StringBuffer("<ul>\n")
            links.forEach {
                content.append("  <li>")
                        .append("""  <a href="${it.url}">${it.title}</a>""")
                        .append("""  <br>${it.comment}""")
                        .append("  </li>")
                        .append("\n")
            }
            content.append("</ul>\n")
            log.info("Posting new article:\n$title\n${content}")
            post(title, content.toString(), title)
        } else {
            log.info("No new links to post")
        }
    }
}

fun main() {
    Wordpress().post("from Kotlin", "content Kotlin", "excerpt Kotlin")
}