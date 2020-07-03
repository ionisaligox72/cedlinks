package com.beust.cedlinks

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.lang.IllegalArgumentException


private val consumerKey = "92219-66fd3a57ea0c8d5e5746cd7b"
//private val accessToken = "82fb7d42-9386-f18c-5202-271a04"

class RequestRequest(val consumer_key: String, val redirect_uri: String)
class RequestResponse(val code: String)

class AuthorizeRequest(val consumer_key: String, val code: String)
class AuthorizeResponse(val access_token: String, username: String)

interface PocketAuthService {
    @Headers("Content-Type: application/json; charset=UTF-8", "X-Accept: application/json")
    @POST("request")
    fun request(@Body request: RequestRequest): Call<RequestResponse>

    @Headers("Content-Type: application/json; charset=UTF-8", "X-Accept: application/json")
    @POST("authorize")
    fun authorize(@Body request: AuthorizeRequest): Call<AuthorizeResponse>
}

class GetRequest(val consumer_key: String, val access_token: String, val tag: String,
        val detailType: String = "complete", val count: Int = 10)
class GetResponse(val status: Int, val complete: Int, val list: Map<String, Any>)
class GetResponseContent(val resolved_id: String, val item_id: String, val images: List<Image>)
class Image(val item_id: String, image_id: String)

class AddRequest(val consumer_key: String, val access_token: String,
        val url: String, val title: String, val tags: String)
class AddResponse(val item: Item, val status: Int)
class Item(val item_id: String)

class SendRequest(val consumer_key: String, val access_token: String, val actions: List<Action>)
class Action(val action: String, val item_id: String)
class SendResponse(val action_results: List<Boolean>, val status: Int)

interface PocketService {
    @Headers("Content-Type: application/json; charset=UTF-8", "X-Accept: application/json")
    @POST("get")
    fun get(@Body request: GetRequest): Call<GetResponse>

    @Headers("Content-Type: application/json; charset=UTF-8", "X-Accept: application/json")
    @POST("add")
    fun add(@Body request: AddRequest): Call<AddResponse>

    @Headers("Content-Type: application/json; charset=UTF-8", "X-Accept: application/json")
    @POST("send")
    fun modify(@Body request: SendRequest): Call<SendResponse>
}

fun main() {
    Pocket("https://beust.com").addAndArchive("https://beust.com", "My web site")
}

class Pocket(private val redirectUrl: String) {
    private val TAG = "twit"
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val gson = GsonBuilder().setLenient().create()
    private val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    private val retrofit = Retrofit.Builder()
            .baseUrl("https://getpocket.com/v3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(PocketService::class.java)
    private val authRetrofit = Retrofit.Builder()
            .baseUrl("https://getpocket.com/v3/oauth/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(PocketAuthService::class.java)
    private lateinit var accessToken: String

    init {
        authPocket(redirectUrl)
    }

    fun addAndArchive(url: String, title: String) {
        val a = retrofit.add(AddRequest(consumerKey, accessToken, url, title, tags = TAG)).execute()
        val response = a.body()
        if (response?.status == 1) {
            val itemId = response.item.item_id;
            val m = retrofit.modify(SendRequest(consumerKey, accessToken, listOf(Action("archive", itemId))))
                    .execute()
            println(m)
        } else {
            throw IllegalArgumentException("Couldn't add URL: " + response?.status)
        }
    }

    fun get() {
        val get = retrofit.get(GetRequest(consumerKey, accessToken, "twit")).execute()
        val response = get.body()
        println(response)
    }

    private fun authPocket(redirectUrl: String) {
        val result = authRetrofit.request(RequestRequest(consumerKey, redirectUrl)).execute()
        println(result.errorBody()?.string())
        val body = result.body()
        if (body != null) {
            accessToken = body.code

            println("Code: " + body.code)
            println("Now need to go to " +
                    "https://getpocket.com/auth/authorize?request_token=${accessToken}&redirect_uri=${redirectUrl}")
        } else {
            throw IllegalArgumentException("Couldn't authorize 1: " + result.errorBody()?.string())
        }
    }

    fun authorize() {
        if (accessToken != null) {
            val auth = authRetrofit.authorize(AuthorizeRequest(consumerKey, accessToken!!)).execute()
            val b = auth.body()
            if (b != null) {
                accessToken = b.access_token
            } else {
                throw IllegalArgumentException("Couldn't authorize 2: " + auth.errorBody()?.string())
            }
        } else {
            throw IllegalAccessError("Code was not set before calling authorize")
        }
    }
}