package top.lyfzn.mediaquick.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import okhttp3.Call
import org.jetbrains.anko.toast
import top.lyfzn.mediaquick.App
import top.lyfzn.mediaquick.R
import top.lyfzn.mediaquick.api.ApiFactory
import top.lyfzn.mediaquick.exception.ParseException

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuickKt
 *@package top.lyfzn.mediaquickkt.service
 *@date on 2021/5/22 1:44
 */
class ParseReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION: String = "top.lyfzn.mediaquickkt.service.parse"
    }
    private var call: Call? = null
    override fun onReceive(p0: Context?, p1: Intent?) {
        val isCancel: Boolean = p1?.getBooleanExtra("cancel", false)?:false
        val shareText: String? = p1?.getStringExtra("shareText")
        // 剪切板还是手动输入，1为手动输入
        val mode: Int = p1?.getIntExtra("mode", 0)?:0
        if (isCancel) {
            // 收到取消广播停止解析
            call?.cancel()
            return
        }
        try {
            call = if (shareText?.isNotBlank() == true) ApiFactory.parse(shareText) else null
        } catch (pe: ParseException) {
            // 解析失败
            p0?.toast(pe.message.toString())
            return
        }
        if (call == null && mode == 1) {
            App.appContext?.toast(App.getString(R.string.share_info_not_supported))
        }
        if (call != null) {
            App.showProgressDialog {
                call?.cancel()
            }
        }
    }
}