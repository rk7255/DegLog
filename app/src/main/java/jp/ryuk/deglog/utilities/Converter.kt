package jp.ryuk.deglog.utilities

import android.annotation.SuppressLint
import java.math.BigDecimal
import java.math.RoundingMode
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
fun Long.getYear(): Int =
    SimpleDateFormat("yyyy").format(this).toInt()

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

// 作った理由忘れた 不要なら削除
//@SuppressLint("SimpleDateFormat")
//fun c(systemTime: Long): SimpleDateFormat {
//    val t = SimpleDateFormat("yyyy/MM/dd")
//        .format(systemTime).toString()
//    return SimpleDateFormat(t, Locale.JAPAN)
//}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateStringRelative(systemTime: Long): String {
    val diff = System.currentTimeMillis() - systemTime
    val second = 1000
    val minute = second * 60
    val hour = minute * 60
    val day = 24 * 60 * 60 * 1000
    val days = day * 2

    return when {
        diff < 0 -> "0秒前"
        diff < second -> "${diff / minute}秒前"
        diff < hour -> "${diff / minute}分前"
        diff < day -> "${diff / hour}時間前"
        diff < days -> "昨日"
        else -> "${diff / day}日前"
    }
}

/**
 * 型変換
 */
fun convertStringToFloat(str: String?, unit: String): Float? {
    return if (unit == "kg" || unit == "m") {
        if (str.isNullOrEmpty()) null else (str.toFloat() * 1000)
    } else {
        if (str.isNullOrEmpty()) null else str.toFloat()
    }
}


/**
 * 単位変換
 */
fun convertUnit(number: Float, unit: String): String {
    return when (unit) {
        "g" -> "${roundDown(number)} g"
        "kg" -> "${roundUp(number)} kg"
        "mm" -> "${roundDown(number)} mm"
        "m" -> "${roundUp(number)} m"
        else -> "$number"
    }
}

private fun roundDown(number: Float): BigDecimal =
    BigDecimal(number.toString()).setScale(0, RoundingMode.HALF_UP)

private fun roundUp(number: Float): BigDecimal =
    BigDecimal((number / 1000).toString()).setScale(2, RoundingMode.HALF_UP)
