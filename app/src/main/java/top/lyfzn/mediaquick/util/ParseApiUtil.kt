package top.lyfzn.mediaquick.util

import android.content.Context
import android.content.SharedPreferences

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuick
 *@package top.lyfzn.mediaquick.util
 *@date on 2021/7/22 23:48
 */
object ParseApiUtil {
    fun getParseApi(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("config", 0)
        return sharedPreferences.getString("parseApi", null)
    }

    fun saveParseApi(context: Context, parseApi: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("config", 0)
        val spEdit = sharedPreferences.edit()
        spEdit.putString("parseApi", parseApi)
        // 保存
        spEdit.apply()
    }
}