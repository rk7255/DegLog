package jp.ryuk.deglog.utilities

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


/**
 * 日付の表示設定
 */
@SuppressLint("SimpleDateFormat")
fun convertYMDToLong(y: Int, m: Int, d: Int) : Long {
    val calendar = Calendar.getInstance()
    calendar.set(y, m, d, 0, 0, 0)
    return calendar.timeInMillis
}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("yyyy/MM/dd")
        .format(systemTime).toString()
}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateStringInTime(systemTime: Long): String {
    return SimpleDateFormat("yyyy/MM/dd - HH:mm")
        .format(systemTime).toString()
}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateStringRelative(systemTime: Long): String {
    val diff = System.currentTimeMillis() - systemTime
    val minute = 60 * 1000
    val hour = minute * 60
    val day = 24 * 60 * 60 * 1000
    val days = day * 2

    return when {
        diff < hour -> "${diff / minute}分前"
        diff < day -> "${diff / hour}時間前"
        diff < days -> "昨日"
        else -> "${diff / day}日前"
    }

}



/**
 * 型変換
 */
fun convertIntToString(num: Int?): String {
    return num?.toString() ?: ""
}

fun convertStringToInt(str: String?): Int? {
    return if (str.isNullOrEmpty()) null else str.toInt()
}

/**
 * 体重と体長の単位付与
 */
fun convertWeight(weight: Int?): String? {
    return if (weight == null) { null } else { "$weight g" }
}

fun convertLength(length: Int?): String? {
    return if (length == null) { null } else { "$length mm" }
}