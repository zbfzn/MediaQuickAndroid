package top.lyfzn.mediaquick.service

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.parse_result_dialog.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.longToast
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import top.lyfzn.mediaquick.App
import top.lyfzn.mediaquick.R
import top.lyfzn.mediaquick.util.FileDownloaderCallUtil
import top.lyfzn.mediaquick.util.LogUtil
import java.io.IOException
import java.lang.Exception

/**
 *
 *
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuickKt
 *@package top.lyfzn.mediaquickkt.service
 *@date on 2021/5/22 0:42
 */
class ParseCallBack : Callback {
    override fun onFailure(call: Call, e: IOException) {
        call.isCanceled().not().let {
            App.appContext?.runOnUiThread {
                App.hideProgressDialog()
                App.appContext?.toast("解析失败，请检查网络是否正常！")
            }
        }
        LogUtil.debug(App.appContext!!, e.message)
    }

    override fun onResponse(call: Call, response: Response) {
        val resText: String? = response.body?.string()
        App.appContext?.runOnUiThread {
            App.hideProgressDialog()
            try {
                LogUtil.debug(App.appContext!!, resText)
                val res: JSONObject = JSONObject.parseObject(resText)
                if (res.getJSONObject("head").getString("code") != "200") {
                    App.appContext?.longToast("解析失败【SERVER_ERROR】")
                    return@runOnUiThread
                }
                val data: JSONObject = res.getJSONObject("body").getJSONObject("data")
                val type: String = when(data.getString("mediaApiType")) {
                    "douyin" -> "抖音"
                    "kuaishou" -> "快手"
                    "weibo" -> "微博"
                    "wangyicloud" -> "网易云音乐"
                    else -> "未知平台"
                }
                val media: JSONObject = data.getJSONObject("media")
                if (media.getString("mediaType") !in arrayOf("video", "photos")) {
                    App.appContext?.longToast("未适配，请升级到最新版本！")
                    return@runOnUiThread
                }
                val mediaType: String = when(media.getString("mediaType")) {
                    "video" -> "视频"
                    "photos" -> "图集"
                    else -> "未知资源类型"
                }
                // 资源标题
                val mediaTitle: String = when(media.getString("mediaType")) {
                    "video" -> media.getString("title")
                    "photos" -> media.getString("description")
                    else -> "未知资源类型"
                }
                val user: JSONObject = data.getJSONObject("user")
                val message: String = "[${user.getString("name")}] - ${mediaTitle}"
                val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(App.appContext)
                val view: View = View.inflate(App.appContext, R.layout.parse_result_dialog, null)
                alertDialogBuilder.setCancelable(true)
                    .setTitle("解析成功 - $type - $mediaType")
                    .setView(view)
                view.findViewById<TextView>(R.id.parse_result_desc).text = message
                val imageView: ImageView = view.findViewById(R.id.parse_result_cover)
                // 设置cover图片
                when(media.getString("mediaType")) {
                    "video" -> {
                        Glide.with(view)
                            .load(media.getString("videoCover"))
                            .placeholder(R.drawable.placeholder_16_9)
                            .into(imageView)
                    }
                    "photos" -> {
                        Glide.with(view)
                            .load(media.getJSONArray("photoList").getJSONObject(0).getString("url"))
                            .placeholder(R.drawable.placeholder_16_9)
                            .into(imageView)
                    }
                }
                alertDialogBuilder.setPositiveButton("下载") { p0, p1 ->
                    run {
                        when (media.getString("mediaType")) {
                            "video" -> {
                                // 下载的单个视频
                                val urls: JSONArray = media.getJSONArray("urls")
                                if (urls.size > 0) {
                                    FileDownloaderCallUtil.downloadVideo(mediaTitle, user.getString("name"), urls.getString(urls.size - 1), type = type)
                                }
                            }
                            "photos" -> {
                                // 下载图集
                                val photos: JSONArray = media.getJSONArray("photoList")
                                val mutableList = mutableListOf<String>()
                                for (photo in photos) {
                                    mutableList.add((photo as JSONObject).getString("url"))
                                }
                                FileDownloaderCallUtil.downloadPhotos(mediaTitle, user.getString("name"), mutableList.toTypedArray(), type = type)
                            }
                        }
                    }
                }


                val alertDialog: AlertDialog = alertDialogBuilder.create()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //针对安卓8.0对全局弹窗适配
                    alertDialog.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                } else {
                    alertDialog.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                }
                alertDialog.show()

            } catch (e: Exception) {
                App.appContext?.longToast("解析失败【LOCAL_ERROR】")
                e.printStackTrace()
                LogUtil.error(App.appContext!!, e.getStackTraceString())
            }
        }
    }
}