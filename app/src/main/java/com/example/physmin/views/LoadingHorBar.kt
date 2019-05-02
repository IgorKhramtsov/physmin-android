package com.example.physmin.views

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.example.physmin.R
import android.os.Handler
import android.util.Log
import android.view.animation.AccelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation

/**
 * TODO: document your custom view class.
 */
class LoadingHorBar(context: Context, attrs: AttributeSet?): View(context, attrs) {

    private var backPaint: Paint? = null
    private var frontPaint: Paint? = null

    var backColor = ResourcesCompat.getColor(resources, R.color.loading_back, null)
    var frontColor = ResourcesCompat.getColor(resources, R.color.loading_front, null)
    val loadingBarBackHeight = 3.dpToPx()
    var loadingBarStart = 0f
    var loadingBarWidth = 20.dpToPx()
    var valueAnimator: ValueAnimator? = null

    init {
        this.visibility = GONE
    }

    fun show() {
        this.visibility = VISIBLE
    }

    fun hide() {
        this.visibility = GONE
    }

    private fun init() {
        valueAnimator = ValueAnimator.ofFloat(0f, width.toFloat()).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                loadingBarStart = value
                loadingBarWidth = widthInterpolate(20.dpToPx(), 100.dpToPx(), it.animatedFraction)
                invalidate()
            }
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration = 1200
            start()
        }
        backPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply { color = backColor }
        frontPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply { color = frontColor }
    }

    fun widthInterpolate(a: Float, b: Float, f: Float) = a + f * (b - a)

    private fun redraw(canvas: Canvas) {
        canvas.withTranslation(paddingLeft.toFloat(), (height - paddingTop - paddingBottom) / 2f) {
            backPaint?.let {
                canvas.drawRect(0f,
                        -loadingBarBackHeight / 2f,
                        (width).toFloat(),
                        loadingBarBackHeight,
                        it)
            }
            frontPaint?.let {
                canvas.drawRect(loadingBarStart,
                        -loadingBarBackHeight / 2f,
                        loadingBarStart + loadingBarWidth,
                        loadingBarBackHeight,
                        it)
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (valueAnimator == null)
            init()
        else
            redraw(canvas)
    }
}
