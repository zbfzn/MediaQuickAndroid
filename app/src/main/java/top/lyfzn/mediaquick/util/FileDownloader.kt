package top.lyfzn.mediaquick.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import top.lyfzn.mediaquick.App
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*

/**
 * 适配安卓10以上分区储存解决方案（仅适配适配和图片的下载）
 *
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuick
 *@package top.lyfzn.mediaquick.util
 *@date on 2021/5/31 15:59
 */
class FileDownloader {
    private val fileDownloadQueue: FileDownloadQueue = FileDownloadQueue()

    /**
     * 队列下载文件
     */
    fun enqueue(fileDownloadRequest: FileDownloadRequest) {
        fileDownloadQueue.addRequest(fileDownloadRequest)
    }
}

class FileDownloadQueue {
    var context: Context? = App.appContext
    val retryTimes = 2
    companion object {
        var queue: MutableList<FileDownloadRequest> = arrayListOf()
        lateinit var downloadThread: Thread
    }
    constructor() {
        downloadThread = Thread {
            while (queue.isNotEmpty()) {
                val request = queue[0]
                queue.removeAt(0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // 安卓Q使用ContextResolver
                    val contentResolver = context!!.contentResolver
                    val contentValues = ContentValues()
                    var insertUri: Uri? = null
                    when(request.fileType) {
                        FileType.VIDEO_MP4 -> {
                            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, request.fileName)
                            contentValues.put(MediaStore.Video.Media.DESCRIPTION, request.fileName)
                            contentValues.put(MediaStore.Video.Media.MIME_TYPE, request.fileType.toString())
                            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, request.fileType!!.typeFileDir + request.fileSubDir)
                            val external = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            insertUri = contentResolver.insert(external, contentValues)
                        }
                        FileType.IMAGE_JPEG, FileType.IMAGE_WEBP -> {
                            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, request.fileName)
                            contentValues.put(MediaStore.Images.Media.DESCRIPTION, request.fileName)
                            contentValues.put(MediaStore.Images.Media.MIME_TYPE, request.fileType.toString())
                            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, request.fileType!!.typeFileDir + request.fileSubDir)
                            val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            insertUri = contentResolver.insert(external, contentValues)
                        }
                    }
                    if (insertUri == null) {
                        continue
                    }
                    var retryT = retryTimes
                    while (retryT > 0) {
                        val response = HttpUtil.doGetSync(request.uri.toString())
                        var outs: OutputStream? = null
                        var ins: InputStream? = null
                        try {
                            outs = contentResolver.openOutputStream(insertUri!!)
                            ins = response.body?.byteStream()
                            var read: Int
                            var hasRead: Int = 0
                            var buffer: ByteArray = ByteArray(1024)
                            do {
                                read = ins?.read(buffer)!!
                                if (read == -1)
                                    break
                                outs?.write(buffer, 0, read)
                                hasRead += read
                            } while (true)
                            retryT = 0
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            retryT --
                        } finally {
                            outs?.close()
                            ins?.close()
                        }
                    }
                }
            }
        }
    }
    fun addRequest(fileDownloadRequest: FileDownloadRequest) {
        queue.add(fileDownloadRequest)
        if (!downloadThread.isAlive) {
            downloadThread.start()
        }
    }
    fun cancel(fileDownloadRequest: FileDownloadRequest): Boolean {
        if (queue.contains(fileDownloadRequest)) {
            queue.remove(fileDownloadRequest)
            return true
        }
        return false
    }

}
enum class FileType {
    /**
     * 视频
     */
    VIDEO_MP4("video/mp4", Environment.DIRECTORY_MOVIES),

    /**
     * 图片
     */
    IMAGE_JPEG("image/jpeg", Environment.DIRECTORY_PICTURES),
    IMAGE_WEBP("image/webp",Environment.DIRECTORY_PICTURES);

    /**
     * 文件类型
     */
    private var typeName: String

    /**
     * 文件子路径
     */
    var typeFileDir: String

    constructor(typeName: String, typeFileDir: String) {
        this.typeName = typeName
        this.typeFileDir = typeFileDir
    }

    override fun toString(): String {
        return typeName
    }
}
class FileDownloadRequest {
    var uri: Uri? = null
    var fileName: String? = null
    var fileSubDir: String? = null
    var fileType: FileType? = null
    var id: String = UUID.randomUUID().toString()
}