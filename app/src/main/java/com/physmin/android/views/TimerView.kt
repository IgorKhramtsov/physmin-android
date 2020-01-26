package com.physmin.android.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import com.physmin.android.R
import kotlin.math.abs


class TimerView(context: Context, attrs: AttributeSet?): View(context, attrs) {

    private var paintText: TextPaint
    private var hsvWheelPaint: Paint
    private lateinit var valueAnimator: ValueAnimator

    var circleColor = ResourcesCompat.getColor(resources, R.color.ui_panel, null)
    var circleShadowColor = ResourcesCompat.getColor(resources, R.color.ui_shadow, null)
    private val shadowBlurRadius = 2.dpToPx()
    private val shadowOffsetY = 2.dpToPx()
    private val shadowOffsetX = 0f
    var hsvWheelStrokeWidth = 4.dpToPx()

    private var _wheelLen = 360f
    private var _wheelDelta = 0f
    private var _time = 60
    private var _timeTextHeight = 0f
    private var _staticLayout: StaticLayout? = null
    private var _hsvWheelRect: RectF? = null
    private var _backShadowPanelBitmap: Bitmap? = null
    private var _timerHandler = Handler()
    private lateinit var _timerRunnable: Runnable

    init {
        hsvWheelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = hsvWheelStrokeWidth
            color = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
        }

        paintText = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply { textSize = 20f.dpToPx() }
        _timeTextHeight = abs(paintText.fontMetrics.top)

        val view = this
        this.post {
            generateVisualElements()

            valueAnimator = ValueAnimator.ofFloat(0f, 360f / 60f).apply {
                addUpdateListener {
                    _wheelDelta = it.animatedValue as Float
                    view.invalidate()
                }
                this.doOnEnd {
                    _wheelLen -= _wheelDelta
                    _wheelDelta = 0f
                    invalidate()
                }
                duration = 150
            }

            _timerRunnable = Runnable {
                if (_time <= 0) {
                    timerEnd()
                    return@Runnable
                }

                _time -= 1
                createStaticLayout()
                valueAnimator.start()
                _timerHandler.postDelayed(_timerRunnable, 1000)
            }
        }

    }

    private fun createStaticLayout() {
        _staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            StaticLayout.Builder.obtain(_time.toString(), 0, _time.toString().length, paintText, width - paddingRight - paddingLeft)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER).build()
        else
            StaticLayout(_time.toString(), paintText, width - paddingRight - paddingLeft, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false)
    }

    private fun generateVisualElements() {
        val width = this.width - shadowOffsetX
        val height = this.height - shadowOffsetY
        if(width <= 0 || height <= 0) {
            _hsvWheelRect = null
            _backShadowPanelBitmap = null
            return
        }

        createStaticLayout()

        _hsvWheelRect = RectF().apply {
            left = 0f + hsvWheelStrokeWidth / 2f
            top = 0f + hsvWheelStrokeWidth / 2f
            right = width - hsvWheelStrokeWidth / 2f
            bottom = height - hsvWheelStrokeWidth / 2f
        }

        _backShadowPanelBitmap = generateShadowPanel(width.toInt(), height.toInt(),
                0f, shadowBlurRadius,
                shadowOffsetX, shadowOffsetY,
                circleColor, circleShadowColor,
                this, CIRCLE)
    }

    fun restart() {
        _time = 60
        _wheelLen = 360f
        start()
    }

    fun start() {
        _timerHandler.removeCallbacks(_timerRunnable)
        _timerHandler.postDelayed(_timerRunnable, 0)
    }

    private fun timerEnd() {
        _time = 0
        _wheelLen = 0f
        this.invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val height = this.minimumHeight + shadowBlurRadius.toInt() + shadowOffsetY.toInt()
            val width = this.minimumWidth + shadowBlurRadius.toInt() + shadowOffsetX.toInt()
            setMeasuredDimension(width, height)
        } else {
            TODO("VERSION.SDK_INT < JELLY_BEAN")
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(_backShadowPanelBitmap == null)
            generateVisualElements()

        _backShadowPanelBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        _hsvWheelRect?.let {
            canvas.drawArc(it, 0f, _wheelLen - _wheelDelta, false, hsvWheelPaint)
        }

        _staticLayout?.let {
            canvas.withTranslation(paddingLeft.toFloat(), paddingTop + (_timeTextHeight / 2f)) {
                it.draw(canvas)
            }
        }
    }


}