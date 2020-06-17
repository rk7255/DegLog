package jp.ryuk.deglog.utilities

import android.annotation.SuppressLint
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object Converter {
    /**
     * 日付変換
     */
    fun longToDateString(systemTime: Long): String {
        return SimpleDateFormat("yyyy/MM/dd")
            .format(systemTime).toString()
    }

    fun longToDateStringJp(systemTime: Long): String {
        return SimpleDateFormat("yyyy年 M月 d日")
            .format(systemTime).toString()
    }



    fun longToDateAndTimeString(systemTime: Long): String {
        return SimpleDateFormat("yyyy/MM/dd HH:mm")
            .format(systemTime).toString()
    }

    fun longToDateShortString(systemTime: Long): String {
        return SimpleDateFormat("dd日 HH:mm")
            .format(systemTime).toString()
    }

//    fun longToTimeString(systemTime: Long): String {
//        return SimpleDateFormat("HH:mm")
//            .format(systemTime).toString()
//    }
        fun longToTimeStringJp(systemTime: Long): String {
        return SimpleDateFormat("H時 m分")
            .format(systemTime).toString()
    }



    fun longToRelativeDateString(systemTime: Long): String {
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
            else -> result.append("${roundInt(number)}")
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
}