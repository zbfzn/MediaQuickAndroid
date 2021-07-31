package top.lyfzn.mediaquick.util

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuickKt
 *@package top.lyfzn.mediaquickkt.util
 *@date on 2021/5/22 3:33
 */
object TextUtil {
    /**
     * 将一串字符转为合法文件名
     */
    fun getLegalFileName(string: String) : String {
        val strReplaced = string.replace("(\\s*)([?*:\"<>\\\\/|#]*)".toRegex(), "")
        return if (strReplaced.length > 50) strReplaced.substring(0, 51) else strReplaced
    }
}