package top.lyfzn.mediaquick.util

import android.app.DownloadManager
import android.content.Context
import android.net.Network
import android.net.Uri
import android.os.Build
import android.os.Environment
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import top.lyfzn.mediaquick.App
import top.lyfzn.mediaquick.R
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuickKt
 *@package top.lyfzn.mediaquickkt.util
 *@date on 2021/5/22 3:30
 */
object FileDownloaderCallUtil {
    var videoPath: String = "/MediaQuick/video/"
    var photosPath: String = "/MediaQuick/photos/"

    fun downloadFile(fileName: String, fileDirInExternalStorage: String, url: String, type: String="") {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 安卓10分区储存临时方案
            App.appContext?.toast(App.getString(R.string.android_q_and_higher_not_support_download_such_file))
            return
        }
        // 获取下载管理器
        val downloadManager: DownloadManager = App.appContext?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        // 构建下载请求
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
        //设置文件夹和文件名
        request.setDestinationInExternalPublicDir("$fileDirInExternalStorage${if (type.isBlank()) "" else "${type}/"}", fileName)
        // 加入队列等待下载
        downloadManager.enqueue(request)
        App.appContext?.longToast("${App.getString(R.string.start_download)}：${fileName})")
    }

    fun downloadVideo(title: String, userName: String, url: String, type: String="") {
        // 获取下载管理器
        val downloadManager: DownloadManager = App.appContext?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        // 构建下载请求
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE + DownloadManager.Request.NETWORK_WIFI)
        val fileName: String = TextUtil.getLegalFileName("${userName}-${title}") + ".mp4"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 适配安卓10分区储存
            val fileDownloadRequest = FileDownloadRequest()
            fileDownloadRequest.fileType = FileType.VIDEO_MP4
            fileDownloadRequest.fileName = fileName
            fileDownloadRequest.fileSubDir = "$videoPath${if (type.isBlank()) "" else "${type}/"}"
            fileDownloadRequest.uri = Uri.parse(url)
            FileDownloader().enqueue(fileDownloadRequest)
        } else {
            //设置文件夹和文件名
            request.setDestinationInExternalPublicDir("$videoPath${if (type.isBlank()) "" else "${type}/"}"  ,  TextUtil.getLegalFileName("${userName}-${title}") + ".mp4")
            // 加入队列等待下载
            downloadManager.enqueue(request)
        }
        App.appContext?.longToast("${App.getString(R.string.start_download)}：${TextUtil.getLegalFileName(title) + ".mp4"}")
    }

    fun downloadPhotos(title: String, userName: String, urls: Array<String>, type: String="") {
        // 获取下载管理器
        val downloadManager: DownloadManager = App.appContext?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        for(url in urls) {
            // 构建下载请求
            val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
            var fileName = "${TextUtil.getLegalFileName(title)}.jpg"
            val matcher: Matcher = Pattern.compile("([^/]*\\.(jpg|webp))").matcher(url)
            if (matcher.find()) {
                // 将Webp格式名称改为jpg，设备能直接扫描到图片。
                fileName = TextUtil.getLegalFileName(matcher.group(1)!!).replace("webp", "jpg")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 适配安卓10分区储存
                val fileDownloadRequest = FileDownloadRequest()
                fileDownloadRequest.fileType = FileType.IMAGE_JPEG
                fileDownloadRequest.fileName = fileName
                fileDownloadRequest.fileSubDir = photosPath + (if (type.isBlank()) "" else "${type}/") + "${userName}/"
                fileDownloadRequest.uri = Uri.parse(url)
                FileDownloader().enqueue(fileDownloadRequest)
            } else {
                //设置文件夹和文件名
                request.setDestinationInExternalPublicDir(photosPath + (if (type.isBlank()) "" else "${type}/") + "${userName}/", fileName)
                // 加入队列等待下载
                downloadManager.enqueue(request)
            }
        }

        App.appContext?.longToast("${App.getString(R.string.start_download)}：${title}   ${urls.size}张图片")
    }
}