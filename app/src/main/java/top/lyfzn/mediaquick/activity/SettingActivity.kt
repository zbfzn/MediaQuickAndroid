package top.lyfzn.mediaquick.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.stopService
import org.jetbrains.anko.toast
import top.lyfzn.mediaquick.R
import top.lyfzn.mediaquick.api.ApiFactory
import top.lyfzn.mediaquick.service.MediaParseService
import top.lyfzn.mediaquick.service.ServiceExitRecevier
import top.lyfzn.mediaquick.util.ParseApiUtil
import java.util.regex.Pattern

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val parseApi = ParseApiUtil.getParseApi(this)
        // 展示取出的值
        parse_api_input.setText(parseApi ?: "")
        save_parse_api.setOnClickListener {
            val inputApi = parse_api_input.text.toString().trim()
            val matcher = Pattern.compile("^https?://.*").matcher(inputApi)
            if (matcher.find()) {
                // 保存
                ParseApiUtil.saveParseApi(this@SettingActivity, inputApi)
                // 刷新
                ApiFactory.initParseApi(inputApi)
                toast("保存成功")
                this@SettingActivity.finish()
            } else {
                toast("Api非HTTP(S)链接!!!")
            }
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == 4) {
            // 禁用返回键
            if (ParseApiUtil.getParseApi(this).isNullOrBlank()) {
                val intent = Intent()
                intent.putExtra("success", false)
                setResult(1, intent)
                this.finish()
            } else {
                toast("保存后自动退出界面！")
            }
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}