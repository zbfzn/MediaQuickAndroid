package top.lyfzn.mediaquick.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.jetbrains.anko.toast
import top.lyfzn.mediaquick.App
import top.lyfzn.mediaquick.R
import top.lyfzn.mediaquick.util.LogUtil

class MediaParseService : Service() {
    var parseReceiver: ParseReceiver? = null
    var localBroadcastManager: LocalBroadcastManager? = null
    companion object {
        val id: String = "MEDIA_PARSE_SERVICE"
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 适配安卓8以上前台服务需要Chanel
            val builder = Notification.Builder(this)
            val notificationChannel = NotificationChannel("22", "MediaQuick后台服务", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.setShowBadge(false) //是否显示角标
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            builder.setChannelId(notificationChannel.id)
            startForeground(1, builder.build())
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.info(this, App.getString(R.string.parse_service_started))
        toast(App.getString(R.string.parse_service_started))
        // 注册解析广播接收器
        parseReceiver = ParseReceiver()
        val intentFilter: IntentFilter = IntentFilter(ParseReceiver.ACTION)
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager!!.registerReceiver(parseReceiver!!, intentFilter)

        // 安卓10 对剪切板权限收紧，无法监听获取剪切板内容
        if (Build.VERSION.SDK_INT < 29) {
            var nowTime: Long = System.currentTimeMillis()
            val clipboardManager: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.addPrimaryClipChangedListener{
                if (System.currentTimeMillis() - nowTime > 400) {
                    nowTime = System.currentTimeMillis()
                    val data = clipboardManager.primaryClip
                    val item = data!!.getItemAt(0)
                    val share_info = item.text.toString()
                    val parseIntent = Intent(ParseReceiver.ACTION)
                    parseIntent.putExtra("shareText", share_info)
                    localBroadcastManager?.sendBroadcast(parseIntent)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        toast(App.getString(R.string.parse_service_stopped))
        parseReceiver?.let { localBroadcastManager?.unregisterReceiver(it) }
        // 发送退出广播
        val intent = Intent(ServiceExitRecevier.ACTION)
        sendBroadcast(intent)
    }
}