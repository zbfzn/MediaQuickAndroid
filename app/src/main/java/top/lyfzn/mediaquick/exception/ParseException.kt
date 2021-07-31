package top.lyfzn.mediaquick.exception

import android.os.Parcel
import android.os.Parcelable
import java.lang.RuntimeException

/**
 *@author zhangbo
 *@profile_url https://github.com/zbfzn
 *@project MediaQuick
 *@package top.lyfzn.mediaquick.exception
 *@date on 2021/7/22 22:56
 */
class ParseException(var msg: String) : RuntimeException(msg) {

}