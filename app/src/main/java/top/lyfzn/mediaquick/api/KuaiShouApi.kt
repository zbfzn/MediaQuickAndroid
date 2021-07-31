package top.lyfzn.mediaquick.api

import java.util.regex.Pattern

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuickKt
 *@package top.lyfzn.mediaquickkt.api
 *@date on 2021/5/22 1:00
 */
class KuaiShouApi : BaseApi {
    override fun canParse(shareText: String): Boolean {
        return Pattern.compile("(https?://v.kuaishou.com/[\\S]*)").matcher(shareText).find()
    }
}