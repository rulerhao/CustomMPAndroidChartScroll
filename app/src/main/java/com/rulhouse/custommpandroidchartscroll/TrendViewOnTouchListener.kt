package com.rulhouse.custommpandroidchartscroll

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View.OnTouchListener
import com.github.mikephil.charting.charts.LineChart
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class TrendViewOnTouchListener {
    private val rangeLowerBound = 20

    private val previousScalePointX = FloatArray(2)
    private val previousScalePointY = FloatArray(2)

    private var scaleCenterPointX = 0f
    private var scaleCenterPointY = 0f

    val trendViewOnTouchListener =
        OnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
                /** 當第二隻手指下手  */
                when (event.actionIndex) {
                    1 -> {
                        for (i in previousScalePointX.indices) {
                            previousScalePointX[i] = event.getX(i)
                            previousScalePointY[i] = event.getY(i)
                        }

                        // Set center point when the second finger touch down.
                        scaleCenterPointX = (previousScalePointX[0] + previousScalePointX[1]) / 2.0f - getYAxisWidth(v as LineChart)
                        scaleCenterPointY = v.getHeight() - (previousScalePointY[0] + previousScalePointY[1]) / 2.0f - getXAxisHeight(v)
                    }
                    else -> {}
                }
            } else if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                // when move your two fingers
                if (event.pointerCount == 2) {
                    if ((previousScalePointX[0] - previousScalePointX[1]) * (event.getX(0) - event.getX(1)) > 0 &&
                        (previousScalePointY[0] - previousScalePointY[1]) * (event.getY(0) - event.getY(1)) > 0
                    ) {
                        val nowScaleXPoint = FloatArray(2)
                        val nowScaleYPoint = FloatArray(2)
                        for (i in 0 until event.pointerCount) {
                            nowScaleXPoint[i] = event.getX(i)
                            nowScaleYPoint[i] = event.getY(i)
                        }

                        val scaleX = getScaleX(
                            nowXPoints = nowScaleXPoint,
                            nowYPoints = nowScaleYPoint,
                            previousXPoints = previousScalePointX,
                            lineChart = v as LineChart
                        )
                        val scaleY = getScaleY(
                            nowXPoints = nowScaleXPoint,
                            nowYPoints = nowScaleYPoint,
                            previousYPoints = previousScalePointY,
                            lineChart = v
                        )
                        // If x axis and y axis are not too small, zoom it to indicate size.
                        if (abs(nowScaleXPoint[0] - nowScaleXPoint[1]) > rangeLowerBound ||
                            abs(nowScaleYPoint[0] - nowScaleYPoint[1]) > rangeLowerBound) {
                            v.zoom(
                                scaleX,
                                scaleY,
                                scaleCenterPointX,
                                scaleCenterPointY
                            )
                        }
                        // Remember last point.
                        for (i in 0..1) {
                            previousScalePointX[i] = event.getX(i)
                            previousScalePointY[i] = event.getY(i)
                        }
                    }
                }
            }
            false
        }

    private fun scaleXOverBound(nowScaleX: Float, lineChart: LineChart): Float {
        val viewPartHandler = lineChart.viewPortHandler
        val scaleX = viewPartHandler.scaleX
        val minScaleX = viewPartHandler.minScaleX
        val maxScaleX = viewPartHandler.maxScaleX

        return if (scaleX * nowScaleX > maxScaleX) 1f
        else if (scaleX * nowScaleX < minScaleX) 1f
        else nowScaleX
    }

    private fun scaleYOverBound(nowScaleY: Float, lineChart: LineChart): Float {
        val viewPartHandler = lineChart.viewPortHandler
        val scaleY = viewPartHandler.scaleY
        val minScaleY = viewPartHandler.minScaleY
        val maxScaleY = viewPartHandler.maxScaleY

        return if (scaleY * nowScaleY > maxScaleY) 1f
        else if (scaleY * nowScaleY < minScaleY) 1f
        else nowScaleY
    }

    private fun getYAxisWidth(lineChart: LineChart): Float {
        return lineChart.rendererLeftYAxis.paintAxisLabels.measureText(lineChart.axisLeft.longestLabel)
    }

    private fun getXAxisHeight(lineChart: LineChart): Float {
        return lineChart.xAxis.textSize
    }

    private fun getScaleX(nowXPoints: FloatArray, nowYPoints: FloatArray, previousXPoints: FloatArray, lineChart: LineChart): Float {
        val nowScaleWidth = abs(nowXPoints[1] - nowXPoints[0])
        val nowScaleHeight = abs(nowYPoints[1] - nowYPoints[0])
        val previousScaleWidth =
            abs(previousXPoints[0] - previousXPoints[1])

        var nowScaleX: Float = nowScaleWidth / previousScaleWidth
        nowScaleX = scaleXOverBound(nowScaleX, lineChart)

        // If x axis or y axis is small enough than do not change it.
        if (nowScaleHeight > rangeLowerBound && nowScaleWidth <= rangeLowerBound) {
            nowScaleX = 1.0f
        }

        return nowScaleX
    }

    private fun getScaleY(nowXPoints: FloatArray, nowYPoints: FloatArray, previousYPoints: FloatArray, lineChart: LineChart): Float {
        val nowScaleWidth = abs(nowXPoints[1] - nowXPoints[0])
        val nowScaleHeight = abs(nowYPoints[1] - nowYPoints[0])
        val previousScaleHeight =
            abs(previousYPoints[0] - previousYPoints[1])

        var nowScaleY: Float = nowScaleHeight / previousScaleHeight
        nowScaleY = scaleYOverBound(nowScaleY, lineChart)

        // If x axis or y axis is small enough than do not change it.
        if (nowScaleWidth > rangeLowerBound && nowScaleHeight <= rangeLowerBound) {
            nowScaleY = 1.0f
        }

        return nowScaleY
    }
}