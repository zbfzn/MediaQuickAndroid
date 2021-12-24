package top.lyfzn.mediaquick.activity

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.startService
import org.jetbrains.anko.stopService
import org.jetbrains.anko.toast
import top.lyfzn.mediaquick.App
import top.lyfzn.mediaquick.R
import top.lyfzn.mediaquick.api.*
import top.lyfzn.mediaquick.service.MediaParseService
import top.lyfzn.mediaquick.service.ParseReceiver
import top.lyfzn.mediaquick.service.ServiceExitRecevier
import top.lyfzn.mediaquick.util.ParseApiUtil
import java.util.*

class MainActivity : AppCompatActivity() {
    /**
     * 广播管理器
     */
    private lateinit var localBroadcastManager: LocalBroadcastManager

    /**
     * 服务退出广播接收器
     */
    private lateinit var serviceExitRecevier: ServiceExitRecevier


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        permissionCheck()
    }

    private fun init() {
        if (ParseApiUtil.getParseApi(this).isNullOrBlank().and(App.MEDIA_QUICK_API.isNotBlank())) {
            // 如果当前未设置解析接口且默认解析接口不为空，则初次启动保存API
            ParseApiUtil.saveParseApi(this, App.MEDIA_QUICK_API)
        }
        // 检查解析地址
        val parseApi = ParseApiUtil.getParseApi(this)
        if (parseApi.isNullOrBlank()) {
            // 启动接口设置Activity
            openSettingActivity()
        } else {
            ApiFactory.initParseApi(parseApi)
        }
        // 初始化广播管理等
        // 获取服务退出广播接收器，所有发出此广播的服务会被再次启动
        serviceExitRecevier = ServiceExitRecevier()
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        // 注册广播接收器
        val intentFilter = IntentFilter()
        intentFilter.addAction(ServiceExitRecevier.ACTION)
        localBroadcastManager.registerReceiver(serviceExitRecevier, intentFilter)

    }
    private fun initEvent() {
        parse_share_text.setOnClickListener {
            if (share_text_input.text.isNotBlank()) {
                val intent = Intent(ParseReceiver.ACTION)
                intent.putExtra("shareText", share_text_input.text.toString())
                // 手动解析，不能解析则提示
                intent.putExtra("mode", 1)
                localBroadcastManager.sendBroadcast(intent)
            } else {
                toast(App.getString(R.string.please_input_kuaishou_douyin_url))
            }
        }

        stop_service.setOnClickListener {
            stopService()
            finish()
            System.exit(0)
        }

        about.setOnClickListener {
            val ab: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
            ab.setTitle("关于")
                    .setMessage("""
                        作者：zbfzn
                        GitHub:https://github.com/zbfzn
                        使用帮助：请赋予相关权限，对于安卓10以下设备视频/相册下载在MediaQuick文件夹下；
                        对于安卓10以上设备视频下载在Movies/MediaQuick/videos下，相册下载在Pictures//MediaQuick/photos下 。
                        """.trimIndent())
                    .setCancelable(true)
            val ad: AlertDialog = ab.create()
            ad.show()
        }

        open_douyin_app.setOnClickListener {
            openApp("抖音","com.ss.android.ugc.aweme")
        }
        open_kuaishou_app.setOnClickListener {
            openApp("快手", "com.smile.gifmaker")
        }

        open_setting.setOnClickListener {
            // 启动设置
            openSettingActivity()
        }
    }
    private fun startService() {
        startService<MediaParseService>()
    }

    private fun stopService() {
        ServiceExitRecevier.IGNORE_EXIT = true
        stopService<MediaParseService>()
    }
    private fun openSettingActivity() {
        val intent = Intent(this@MainActivity, SettingActivity::class.java)
        startActivityForResult<SettingActivity>(1)
    }

    /**
     * 启动第三方APP
     */
    private fun openApp(name: String, appPackageName: String) {
        try {
            val packageManager = packageManager
            val intent: Intent? = packageManager.getLaunchIntentForPackage(appPackageName)
            startActivity(intent)
        } catch (e: Exception) {
            toast("启动${name}APP出错，请允许打开APP或安装APP")
        }
    }

    /**
     * 判断服务是否运行
     */
    private fun isServiceRunning(className: String): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = activityManager.getRunningServices(Int.MAX_VALUE)
        if (info == null || info.size == 0) return false
        for (aInfo in info) {
            if (className == aInfo.service.className) return true
        }
        return false
    }

    private fun permissionCheck() {
        /**
         * 权限检查、声明
         */
        /**
         * 权限检查、声明
         */
        val permisions: MutableList<String> = ArrayList()

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            permisions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            permisions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_PHONE_STATE) !== PackageManager.PERMISSION_GRANTED) {
            permisions.add(Manifest.permission.READ_PHONE_STATE)
        }

        if (!permisions.isEmpty()) {
            val permisions_s = permisions.toTypedArray()
            ActivityCompat.requestPermissions(this@MainActivity, permisions_s, 1)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    checkPass()
                } else {
                    //若没有权限，提示获取.
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    Toast.makeText(this@MainActivity, "需要取得权限以使用悬浮窗", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()
                }
            } else {
                //SDK在23以下，不用管.
                checkPass()
            }
        }

    }

    private fun checkPass() {
        startService()
        // 添加API
        ApiFactory.addApi(DouyinApi())
        ApiFactory.addApi(KuaiShouApi())
        ApiFactory.addApi(WeiBoApi())
        ApiFactory.addApi(WangYiCloudApi())
        initEvent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == 1) {
            if (intent.getBooleanExtra("success", false).not()) {
                stopService()
                finish()
                System.exit(0)
            } else {
                ApiFactory.initParseApi(ParseApiUtil.getParseApi(this)!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
        localBroadcastManager.unregisterReceiver(serviceExitRecevier)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.size > 0) {
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this@MainActivity, "必须同意所有权限才能使用本软件【读写储存】【获取手机信息】", Toast.LENGTH_SHORT).show()
                        finish()
                        return
                    }
                }
                /////
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this@MainActivity)) {
                        checkPass()
                    } else {
                        //若没有权限，提示获取.
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        Toast.makeText(this@MainActivity, "需要取得权限以使用悬浮窗", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                        finish()
                    }
                } else {
                    //SDK在23以下，不用管.
                    checkPass()
                }
            } else {
                Toast.makeText(this@MainActivity, "即将退出", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}