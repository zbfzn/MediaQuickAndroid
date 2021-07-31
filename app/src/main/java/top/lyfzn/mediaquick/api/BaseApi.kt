package top.lyfzn.mediaquick.api

import okhttp3.Call
import okhttp3.Callback
import org.jetbrains.anko.toast
import top.lyfzn.mediaquick.App
import top.lyfzn.mediaquick.util.HttpUtil

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuickKt
 *@package top.lyfzn.mediaquickkt.api
 *@date on 2021/5/21 23:12
 */
interface BaseApi {
    fun canParse(shareText: String) : Boolean;
    fun parse(parseApi: String, shareText: String, callback: Callback) : Call {
        return parseApi.let { HttpUtil.doPost(it, shareText, callback) }
    }
}