package jp.ryuk.deglog.ui.data

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import jp.ryuk.deglog.utilities.getDayOfMonth
import jp.ryuk.deglog.utilities.getMonth
import jp.ryuk.deglog.utilities.getYear

class MyValueFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return value.toString()
    }

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        if (value < Int.MAX_VALUE) return value.toInt().toString()

        val year = value.toLong().getYear()
        val month = value.toLong().getMonth()
        val day = value.toLong().getDayOfMonth()
        return "${year}.${month}.${day}"
    }
}