package com.example.physmin.views

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import com.example.physmin.R
import com.example.physmin.activities.FunctionParcelable



// TODO: Make arrows on axis

open class GraphView(context: Context, attrs: AttributeSet?): View(context, attrs) {

    private val yAxisLength = 12
    private val xAxisLength = 12f
    private val step = 0.2f

    private val positionFunctionColor       = ResourcesCompat.getColor(resources, R.color.graphic_position, null)
    private val velocityFunctionColor       = ResourcesCompat.getColor(resources, R.color.graphic_velocity, null)
    private val accelerationFunctionColor   = ResourcesCompat.getColor(resources, R.color.graphic_acceleration, null)

    private var _backColor      = ResourcesCompat.getColor(resources, R.color.graphic_back_gray, null)
    private var _axisColor      = ResourcesCompat.getColor(resources, R.color.textColor, null)
    private var _textColor      = ResourcesCompat.getColor(resources, R.color.textColor, null)
    private var _verticalAxisLetter     = "x"
    private var _horizontalAxisLetter   = "t"
    private var _zeroAxisLetter         = "0"

    private val axisPaddingLeft     = 40f
    private val axisPaddingTop      = 20f
    private val axisPaddingRight    = 60f
    private val axisPaddingBottom   = 20f

    private var backgroundPaint:   Paint
    private var axisPaint:         Paint
    private var functionPaint:     Paint
    private var textPaint:         TextPaint
    private var smallTextPaint:    TextPaint
    private var functionPath = Path()
    private var indexWidth      = 0.0f
    private var indexHeight     = 0.0f
    private var zeroAxisHeight  = 0f
    private var textSize        = 30f
    private var smallTextSize   = 20f

    private var contentWidth    = width  - (axisPaddingLeft + axisPaddingRight)
    private var contentHeight   = height - (axisPaddingTop + axisPaddingBottom)

    private var _functions: ArrayList<FunctionParcelable>? = null

    var axisColor: Int
        get() = _axisColor
        set(value) {
            _axisColor = value
            invalidateTextPaintAndMeasurements()
        }
    var textColor: Int
        get() = _textColor
        set(value) {
            _textColor = value
            invalidateTextPaintAndMeasurements()
        }
    var backColor: Int
        get() = _backColor
        set(value) {
            _backColor = value
            setBackgroundColor(value)
        }
    var vertAxisLetter: String
        get() = _verticalAxisLetter
        set(value) {
            _verticalAxisLetter = value
            invalidateTextPaintAndMeasurements()
        }
    var horAxisLetter: String
        get() = _horizontalAxisLetter
        set(value) {
            _horizontalAxisLetter = value
            invalidateTextPaintAndMeasurements()
        }
    var functions: ArrayList<FunctionParcelable>?
        get() = _functions
        set(value) {
            _functions = value
            if (contentHeight > 0) regeneratePath()
        }

    init {
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }
        smallTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }
        axisPaint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
        }
        functionPaint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
        }
        backgroundPaint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG
            style = Paint.Style.FILL
            color = backColor
        }
        this.post {
            if (height <= 0)
                return@post
            contentWidth = width - axisPaddingLeft - axisPaddingRight
            contentHeight = height - axisPaddingTop - axisPaddingBottom
            regeneratePath()
        }

        invalidateTextPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {
        textPaint.let {
            it.textSize = textSize
            it.color = textColor
            indexWidth = it.measureText(vertAxisLetter)
            indexHeight = Math.abs(it.fontMetrics.top)
        }
        smallTextPaint.let {
            it.textSize = smallTextSize
            it.color = textColor
            zeroAxisHeight = Math.abs(it.fontMetrics.top)
        }
        axisPaint.let {
            it.color = axisColor
            it.strokeWidth = 1.dpToPx()
            it.strokeCap = Paint.Cap.BUTT
        }
        functionPaint.let {
            it.color = when (vertAxisLetter) {
                "x" -> positionFunctionColor
                "v" -> velocityFunctionColor
                "a" -> accelerationFunctionColor
                else -> Color.RED
            }
            it.style = Paint.Style.STROKE
            it.strokeWidth = 2.dpToPx()
        }
    }

    fun regeneratePath() {
        if (contentHeight <= 0 || functions === null)
            return

        this.vertAxisLetter = functions!![0].funcType

        val heightScaleFactor   = -(contentHeight / yAxisLength)
        val widthScaleFactor    =   contentWidth / xAxisLength
        var calculatedPointY: Float
        var calculatedPointX = 0f
        var globalT = 0f
        var len: Float

        functionPath.reset()
        for (function in functions!!) {
            Log.e("GraphView", "funcType: ${function.funcType}\r\nparams: ${function.x}, ${function.v}, ${function.a} ${function.len}")

            calculatedPointY = calculateFunctionValue(function, 0f) * heightScaleFactor
            functionPath.moveTo(calculatedPointX, calculatedPointY)

            len = function.len.toFloat()

            var localT = 0f
            while (localT < len || (len.isZero() && calculatedPointX < contentWidth)) {
                localT = Math.min(localT + step, len)

                calculatedPointX += step * widthScaleFactor
                calculatedPointY = calculateFunctionValue(function, localT) * heightScaleFactor

                if (Math.abs(calculatedPointY) > contentHeight / 2f)
                    continue

                functionPath.lineTo(calculatedPointX, calculatedPointY)
            }

        }

    }

    private fun calculateFunctionValue(function: FunctionParcelable, t: Float): Float {
        return when (function.funcType) {
            "x" -> (function.x + function.v * t + (function.a * t * t) / 2f)
            "v" -> (function.v + function.a * t)
            else -> function.a
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = canvas.width
        val height = canvas.height
        contentWidth = width - (axisPaddingLeft + axisPaddingRight)
        contentHeight = height - (axisPaddingTop + axisPaddingBottom)
        if (functionPath.isEmpty) regeneratePath()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 3f, 3f, backgroundPaint)
        else
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        canvas.drawLine(axisPaddingLeft, axisPaddingTop,
                axisPaddingLeft, height - axisPaddingBottom,
                axisPaint)
        canvas.drawLine(axisPaddingLeft, height / 2f,
                width - axisPaddingRight, height / 2f,
                axisPaint)
        canvas.withTranslation(axisPaddingLeft, height / 2f) {
            functionPath.let {
                canvas.drawPath(it, functionPaint)
            }
        }
        vertAxisLetter.let {
            canvas.drawText(it,
                    axisPaddingLeft + indexWidth,
                    axisPaddingTop + indexHeight,
                    textPaint)
        }
        horAxisLetter.let {
            canvas.drawText(it,
                    axisPaddingLeft + contentWidth - indexWidth * 2,
                    (height / 2) - indexHeight / 2f,
                    textPaint)
        }
        _zeroAxisLetter.let {
            canvas.drawText(it,
                    indexWidth,
                    (height / 2) + zeroAxisHeight / 2,
                    smallTextPaint)
        }

    }
}
