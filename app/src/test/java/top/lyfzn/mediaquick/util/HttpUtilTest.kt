package top.lyfzn.mediaquick.util

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.junit.Test

import java.io.IOException

/**
 * @author zhangbo
 * @profile_url https://github.com/zbfzn
 * @project MediaQuickKt
 * @package top.lyfzn.mediaquickkt.util
 * @date on 2021/5/22 0:01
 */
class HttpUtilTest1 {

    @Test
    fun doGet() {
        HttpUtil.doGet("http://www.baidu.com", object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                print(response.body?.string())
            }

        })
        Thread.sleep(5000)
    }
}