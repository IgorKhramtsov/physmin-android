package com.example.physmin.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import com.example.physmin.R

/**
 * TODO: document your custom view class.
 */
class ProgressBarView(context: Context, attrs: AttributeSet?): View(context, attrs) {

    private var paintBack: Paint
    private var paintFront: Paint

    var barHeight = 4.dpToPx()
    private var _completeSegmentCount = 0
    private var _segmentLen = 0f
    private var _segmentPadding = 0f
    private var segmentCoords: Array<Array<Float>>? = null
    private var needDraw = false

    var segmentCount = 0
        set(value) {
            field = value
            recalculateSegments()
            invalidate()
        }

    init {
        paintBack = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
            color = ResourcesCompat.getColor(resources, R.color.progressBack, null)
        }
        paintFront = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
            color = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
        }

        post {

        }
    }

    fun show() {
        this.needDraw = true
        invalidate()
    }

    fun hide() {
        this.needDraw = false
        invalidate()
    }

    private fun recalculateSegments() {
        if (segmentCount <= 0 || width == 0)
            return

        val contentWidth = (width - paddingLeft - paddingRight).toFloat()

        _segmentLen = (contentWidth / 6f) * 5f
        _segmentPadding = contentWidth / 6f
        _segmentLen /= segmentCount
        _segmentPadding /= segmentCount - 1

        segmentCoords = Array(segmentCount + 1) { return@Array Array(2) { return@Array 0f } }

        segmentCoords?.let {
            for (i in 0 until segmentCount) {
                it[i][1] += _segmentLen
                it[i + 1][0] = it[i][1] + _segmentPadding
                it[i + 1][1] = it[i][1] + _segmentPadding
                Log.i("Segments coordinates", "${it[i][0]}, ${it[i][1]}")
            }
        }
    }

    fun addSegment() {
        if (_completeSegmentCount >= segmentCount)
            return

        _completeSegmentCount += 1
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!needDraw)
            return

        segmentCoords?.let {
            canvas.withTranslation(paddingLeft.toFloat(), paddingTop + (height - barHeight) / 2f) {
                for (i in 0 until segmentCount) {
                    canvas.drawRect(it[i][0], 0f, it[i][1], barHeight, if (i >= _completeSegmentCount) paintBack else paintFront)
                }
            }
        }
    }
}
