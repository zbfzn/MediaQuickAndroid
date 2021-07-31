package top.lyfzn.mediaquick.util

import android.content.Context
import android.util.Log

/**
 * 日志打印
 * @author zhangbo
 * @profile_url https://github.com/zbfzn
 * @project MediaQuick
 * @package top.lyfzn.mediaquick.util
 * @date on 2021/5/21 17:07
 */
object LogUtil {
    /**
     * 打印信息
     * @param context 上下文
     * @param msg 消息
     */
    @JvmStatic
    fun info(context: Context, msg: String?) {
        log(context, LogType.INFO, msg)
    }

    /**
     * 打印调试信息
     * @param context 上下文
     * @param msg 消息
     */
    fun debug(context: Context, msg: String?) {
        log(context, LogType.DEBUG, msg)
    }

    /**
     * 打印错误信息
     * @param context 上下文
     * @param msg 消息
     */
    fun error(context: Context, msg: String?) {
        log(context, LogType.ERROR, msg)
    }

    /**
     * 打印日志
     * @param context 上下文
     * @param logType 日志类型
     * @param msg 消息
     */
    fun log(context: Context, logType: LogType?, msg: String?) {
        val className = context.javaClass.name
        when (logType) {
            LogType.INFO -> Log.i(className, msg!!)
            LogType.DEBUG -> Log.d(className, msg!!)
            LogType.ERROR -> Log.e(className, msg!!)
        }
    }

    enum class LogType {
        /**
         * 信息
         */
        INFO,

        /**
         * 调试内容
         */
        DEBUG,

        /**
         * 错误信息
         */
        ERROR
    }
}