package com.rulhouse.custommpandroidchartscroll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.ArrayList
import kotlin.random.Random

@Composable
fun MainScreen(
    trendViewClass: TrendViewOnTouchListener = TrendViewOnTouchListener(),
) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        factory = {
            val lineChart = LineChart(it)
            val values: ArrayList<Entry> = ArrayList()
            val dataSet = LineDataSet(values, "Label").apply{
                color = Color.Red.toArgb()
            }

            val xAxis = lineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            val rightYAxis = lineChart.axisRight
            rightYAxis.isEnabled = false

            lineChart.axisLeft.apply {
                axisMaximum = 100f
                axisMinimum = 0f
            }

            lineChart.apply {
                data.apply {
                    LineData(dataSet)
                }

                legend.apply {
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    setDrawInside(true)
                }
                description.apply {
                    text = ""
                }
                invalidate()
            }
        },
        update = { v ->
            val list: MutableList<Int> = arrayListOf()
            val values: MutableList<Entry> = arrayListOf()
            for (i in 0 until 20) {
                list.add(i)
                values.add(Entry(list[i].toFloat(), Random.nextInt(0, 100).toFloat()))
            }

            val dataSet = LineDataSet(values, "Label").apply{
                color = Color(205 / 255f, 139 / 255f, 1 / 255f, 255 / 255f).toArgb()
                lineWidth = 2f
                valueTextSize = 15f
                circleRadius = 4f
                circleColors = listOf(
                    Color(205 / 255f, 139 / 255f, 1 / 255f, 255 / 255f
                    ).toArgb()
                )
                circleHoleRadius = 3f
                circleHoleColor = Color(0xEB / 255f, 0xEB / 255f, 0xD3 / 255f, 255 / 255f).toArgb()
                isHighlightEnabled = false
            }

            v.axisLeft.apply {
                textSize = 15f
            }

            v.xAxis.apply {
                spaceMax = 0.5f
                spaceMin = 0.5f
                setDrawGridLines(false)
                granularity = 1f
                labelCount = 2
            }

            // Set custom zoom method.
            v.setScaleEnabled(false)
            v.setOnTouchListener(trendViewClass.trendViewOnTouchListener)

            v.data = LineData(dataSet)

            v.viewPortHandler.apply {
//                setMinMaxScaleX(values.size / 5f, values.size / 2f)
//                setMaximumScaleY(24f)
            }
            v.moveViewToX(values.size.toFloat())
            v.invalidate()
        }
    )
}