package top.lyfzn.mediaquick.api

import java.util.regex.Pattern

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuickKt
 *@package top.lyfzn.mediaquickkt.api
 *@date on 2021/5/21 23:12
 */
class DouyinApi : BaseApi{
    override fun canParse(shareText: String): Boolean {
        return Pattern.compile("(https?://v.douyin.com/[\\S]*)|(https?://www.iesdouyin.com/[\\S]*)").matcher(shareText).find()
    }
}