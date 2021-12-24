package top.lyfzn.mediaquick.api

import java.util.regex.Pattern

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuick
 *@package top.lyfzn.mediaquick.api
 *@date on 2021/12/24 15:03
 */
class WangYiCloudApi: BaseApi {
    override fun canParse(shareText: String): Boolean {
        return Pattern.compile("(https?://st.music.163.com/mlog/mlog.html[\\S]*)").matcher(shareText).find();
    }
}