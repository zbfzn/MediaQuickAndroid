package top.lyfzn.mediaquick.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.jetbrains.anko.startService
import org.jetbrains.anko.toast

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuickKt
 *@package top.lyfzn.mediaquickkt.service
 *@date on 2021/5/21 21:36
 */
class ServiceExitRecevier : BroadcastReceiver() {
    companion object {
        const val ACTION: String = "top.lyfzn.mediaquickkt.service.service_exit"
        var IGNORE_EXIT: Boolean = false
    }
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (IGNORE_EXIT) {
            // 忽略退出通知，即正退出
            return
        }
        // 服务名
        val serviceId : String? = p1!!.getStringExtra("serviceId")
        // 是否正常退出
        val exitSafety : Boolean? = p1.getBooleanExtra("exitSafety", false)
        when(serviceId) {
            "MEDIA_PARSE_SERVICE" -> {
                p0?.startService<MediaParseService>()
                p0?.toast("重新启动解析服务")
            }
        }
    }
}