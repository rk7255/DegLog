package jp.ryuk.deglog.ui.data

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

object ChartCreator {

    fun createLineChartByIndex(chart: LineChart, dataList: List<Float>) {
        chart.description.text = ""

        val values = arrayListOf<Entry>()
        dataList.reversed().forEachIndexed { index, value ->
            val entry = Entry(index.toFloat(), value)
            values.add(entry)
        }
        val valuesSet = LineDataSet(values, "")

        // 描画
        chart.data = LineData(valuesSet)

        // チャートの設定
        valuesSet.apply {
            lineWidth = 4f
            setDrawValues(false)
            circleRadius = 6f
            color = Color.parseColor("#c6aa80")
            setCircleColor(Color.parseColor("#c6aa80"))
        }

        chart.apply {
            isEnabled = true
            isDoubleTapToZoomEnabled = false
            setTouchEnabled(false)
            setDrawBorders(false)
            animateX(0)
            legend.isEnabled = false

            // 軸の非表示
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
        }

        chart.invalidate()
    }
}