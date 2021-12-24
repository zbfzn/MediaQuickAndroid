package top.lyfzn.mediaquick.api

import java.util.regex.Pattern

/**
 * @author zhangbo
 * @profile_url https://github.com/zbfzn
 * @project MediaQuick
 * @package top.lyfzn.mediaquick.api
 * @date on 2021/8/14 13:59
 */
class WeiBoApi : BaseApi {
    override fun canParse(shareText: String): Boolean {
        return Pattern.compile("(https?://[\\S^.]*?video.weibo.com/[\\S]*)").matcher(shareText).find()
    }
}