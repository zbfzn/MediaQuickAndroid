package top.lyfzn.mediaquick

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.WindowManager

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuickKt
 *@package top.lyfzn.mediaquickkt
 *@date on 2021/5/21 20:11
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // 赋值给伴生对象上下文
        appContext = applicationContext

        progressDialog = ProgressDialog(appContext)
        progressDialog!!.setMessage("解析中.....")
        progressDialog!!.setCancelable(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //针对安卓8.0对全局弹窗适配
            progressDialog!!.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            progressDialog!!.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        }
    }

    companion object {
        const val APP_DIR_NAME = "MediaQuick"

        /**
         * 后端解析API地址，如果为空，初次启动需要手动输入
         */
        const val MEDIA_QUICK_API = ""
        var appContext: Context? = null
        private set
        var progressDialog: ProgressDialog? = null
        fun showProgressDialog(onCancel: DialogInterface.OnCancelListener) {
            progressDialog?.show()
            progressDialog?.setOnCancelListener(onCancel)
        }

        fun hideProgressDialog() {
            progressDialog?.hide()
        }
        fun getString(stringId: Int): String {
            return appContext!!.getString(stringId)
        }
    }



}
