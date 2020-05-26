package jp.ryuk.deglog.ui.data

import android.graphics.Color
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import jp.ryuk.deglog.utilities.Deco

object ChartCreator {

    fun createLineChartByDate(
        chart: LineChart,
        dataList: List<ChartData>,
        nameList: List<String>,
        decoration: Int,
        colorMap: Map<String, Int>? = null
    ) {
        chart.description.text = ""
        val dataSets = mutableListOf<ILineDataSet>()

        nameList.forEach { name ->
            val entries = mutableListOf<Entry>()

            val list = dataList.filter { it.name == name }.sortedBy { it.date }
            list.forEach { d ->
                val entry = Entry(d.date.toFloat(), d.data)
                entries.add(entry)
            }

            val dataSet = LineDataSet(entries, name)

            when (decoration) {
                Deco.DASHBOARD -> dataSet.decorateDB()
                Deco.CHART -> dataSet.decorateCH(colorMap?.get(name))
            }
            dataSets.add(dataSet)
        }

        val lineData = LineData(dataSets)
        chart.apply {
            data = lineData
            when (decoration) {
                Deco.DASHBOARD -> decorateDB()
                Deco.CHART -> decorateCH()
            }
            invalidate()
        }
    }

    fun createLineChartByIndex(
        chart: LineChart,
        dataList: List<ChartData>,
        nameList: List<String>,
        decoration: Int,
        colorMap: Map<String, Int>? = null
    ) {
        chart.description.text = ""
        val dataSets = mutableListOf<ILineDataSet>()

        nameList.forEach { name ->
            val entries = mutableListOf<Entry>()

            val list = dataList.filter { it.name == name }.sortedBy { it.date }
            list.forEachIndexed { i, d ->
                val entry = Entry(i.toFloat(), d.data)
                entries.add(entry)
            }

            val dataSet = LineDataSet(entries, name)
            when (decoration) {
                Deco.DASHBOARD -> dataSet.decorateDB()
                Deco.CHART -> dataSet.decorateCH(colorMap?.get(name))
            }
            dataSets.add(dataSet)
        }

        val lineData = LineData(dataSets)
        chart.apply {
            data = lineData
            when (decoration) {
                Deco.DASHBOARD -> decorateDB()
                Deco.CHART -> decorateCH()
            }
            invalidate()
        }
    }

    private fun LineDataSet.decorateDB() {
        this.apply {
            setDrawValues(false)
            lineWidth = 4f
            circleRadius = 6f
            color = Color.parseColor("#c6aa80")
            setCircleColor(Color.parseColor("#c6aa80"))
        }
    }

    private fun LineDataSet.decorateCH(c: Int? = null) {
        this.apply {
            setDrawValues(false)
            lineWidth = 4f
            circleRadius = 4f
            if (c == null) {
                color = Color.parseColor("#c6aa80")
                setCircleColor(Color.parseColor("#c6aa80"))
            } else {
                color = c
                setCircleColor(c)
            }
        }
    }


    private fun LineChart.decorateDB() {

        this.apply {
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
    }



    private fun LineChart.decorateCH() {
        val legend = this.legend
        legend.apply {
            isEnabled = true
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            yOffset = 4f
        }

        this.apply {
            isEnabled = true
            isDoubleTapToZoomEnabled = true
            setTouchEnabled(true)
            setDrawBorders(false)
            animateX(500, Easing.EaseInOutCubic)
            animateY(300, Easing.EaseInOutCubic)
            val desc = Description()
            desc.text = ""
            description = desc
            setExtraOffsets(0f, 0f, 0f, 12f)


            xAxis.apply {
                setDrawLabels(true)
                setDrawGridLines(true)
                textSize = 14f
                position = XAxis.XAxisPosition.BOTTOM
                setLabelCount(3, false)
                valueFormatter = MyValueFormatter()
            }

            // 縦軸左
            axisLeft.apply {
                enableGridDashedLine(20f, 30f, 0f)
                textSize = 14f
            }
            // 縦軸右
            axisRight.isEnabled = false
        }
    }
}