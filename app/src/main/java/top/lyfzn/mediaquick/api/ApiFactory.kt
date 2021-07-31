package top.lyfzn.mediaquick.api

import okhttp3.Call
import top.lyfzn.mediaquick.exception.ParseException
import top.lyfzn.mediaquick.service.ParseCallBack

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuickKt
 *@package top.lyfzn.mediaquickkt.api
 *@date on 2021/5/22 0:31
 */
object ApiFactory {
    private val apiMutableList: MutableList<BaseApi> = mutableListOf()
    private var parseApi: String? = null

    /**
     * 使用前必须初始化
     */
    fun initParseApi(parseAPi: String) {
        this.parseApi = parseAPi
    }

    fun <T: BaseApi> addApi(api: T) {
        apiMutableList.add(api)
    }
    fun parse(shareText: String) : Call? {
        if (parseApi.isNullOrBlank()) {
            throw ParseException("解析Api不能为空！！！")
        }
        for (api in apiMutableList) {
            if (api.canParse(shareText))
                return api.parse(parseApi!!, shareText, ParseCallBack())
        }
        return null
    }
}