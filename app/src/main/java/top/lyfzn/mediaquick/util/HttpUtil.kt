package top.lyfzn.mediaquick.util

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuickKt
 *@package top.lyfzn.mediaquickkt.util
 *@date on 2021/5/21 22:12
 */
object HttpUtil {
    private val okHttpClient : OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .proxy(Proxy.NO_PROXY)
        .build()
    private val defaultHeaders : Headers = Headers.Builder().add("User-Agent", "okhttp/${OkHttp.VERSION}" ).build()

    fun doGet(url: String, callback: Callback) : Call {
        val headers: Headers = defaultHeaders.newBuilder().build()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .headers(headers)
            .build()
        val call: Call = okHttpClient.newCall(request)
        call.enqueue(callback)
        return call
    }
    fun doGetSync(url: String) : Response {
        val headers: Headers = defaultHeaders.newBuilder().build()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .headers(headers)
            .build()
        val call: Call = okHttpClient.newCall(request)
        return call.execute()
    }

    fun doPost(url: String, content: String, callback: Callback) : Call {
        val headers: Headers = defaultHeaders.newBuilder().build()
        val requestBody: RequestBody = RequestBody.create("text/html".toMediaTypeOrNull(), content)
        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .headers(headers)
            .build()
        val call: Call = okHttpClient.newCall(request)
        call.enqueue(callback)
        return call
    }
}