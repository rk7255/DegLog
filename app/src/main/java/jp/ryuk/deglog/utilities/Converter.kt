package jp.ryuk.deglog.utilities

import android.annotation.SuppressLint
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


/**
 * 日付変換
 */
@SuppressLint("SimpleDateFormat")
fun Long.getYear(): Int = SimpleDateFormat("yyyy").format(this).toInt()

@SuppressLint("SimpleDateFormat")
fun Long.getMonth(): Int = SimpleDateFormat("M").format(this).toInt()

@SuppressLint("SimpleDateFormat")
fun Long.getDayOfMonth(): Int = SimpleDateFormat("d").format(this).toInt()


@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("yyyy/MM/dd")
        .format(systemTime).toString()
}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateStringInTime(systemTime: Long): String {
    return SimpleDateFormat("yyyy/MM/dd HH:mm")
        .format(systemTime).toString()
}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateStringOutYear(systemTime: Long): String {
    return SimpleDateFormat("dd日 HH:mm")
        .format(systemTime).toString()
}

/**
 * 相対時間の計算
 */
fun convertLongToDateStringRelative(systemTime: Long): String {
    val diff = Calendar.getInstance().timeInMillis - systemTime
    val second = 1000
    val minute = second * 60
    val hour = minute * 60
    val day = 24 * 60 * 60 * 1000
    val days = day * 2

    return when {
        diff < 0 -> "0秒前"
        diff < minute -> "${diff % minute / second}秒前"
        diff < hour -> "${diff / minute}分前"
        diff < day -> "${diff / hour}時間前"
        diff < days -> "昨日"
        else -> "${diff / day}日前"
    }
}

/**
 * TODOのアラート設定 2週間でTrue
 */
fun hasAlert(time: Long): Boolean {
    val diff = Calendar.getInstance().timeInMillis - time
    val week = 1000 * 60 * 60 * 24 * 7 * 2
    return diff >= week
}

/**
 * 単位変換
 */
fun convertUnit(number: Float, unit: String, onSuffix: Boolean): String {
    val result = StringBuilder()
    when (unit) {
        "g" -> result.append("${roundInt(number)}")
        "kg" -> result.append("${roundFloatInM(number)}")
        "mm" -> result.append("${roundInt(number)}")
        "cm" -> result.append("${roundFloatInCm(number)}")
        "m" -> result.append("${roundFloatInM(number)}")
        else -> return "単位不明"
    }

    if (onSuffix) result.append(" $unit")
    return result.toString()
}

private fun roundInt(number: Float): BigDecimal =
    BigDecimal(number.toString()).setScale(0, RoundingMode.HALF_UP)

private fun roundFloatInCm(number: Float): BigDecimal {
    val scale = when (number / 10) {
        in 0.0..99.0 -> 1
        in 99.0..999.0 -> 0
        else -> 0
    }
    return BigDecimal((number / 10).toString()).setScale(scale, RoundingMode.HALF_UP)
}

private fun roundFloatInM(number: Float): BigDecimal {
    val scale = when (number / 1000) {
        in 0.0..99.0 -> 2
        in 99.0..999.0 -> 1
        else -> 0
    }
    return BigDecimal((number / 1000).toString()).setScale(scale, RoundingMode.HALF_UP)
}
