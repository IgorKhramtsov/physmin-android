package com.example.physmin.views

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import com.example.physmin.R
import com.example.physmin.activities.FunctionParcelable

// TODO: Make arrows on axis

//fun Int.spToPx(): Float = (this * Resources.getSystem().displayMetrics.density)
fun Int.pxToDp(): Float = this / Resources.getSystem().displayMetrics.density;

fun Int.pxToSp(): Float = this / Resources.getSystem().displayMetrics.scaledDensity

open class GraphView: View {

    private val positionFunctionColor: Int = ResourcesCompat.getColor(resources, R.color.graphic_position, null)
    private val velocityFunctionColor: Int = ResourcesCompat.getColor(resources, R.color.graphic_velocity, null)
    private val accelerationFunctionColor: Int = ResourcesCompat.getColor(resources, R.color.graphic_acceleration, null)

    private var _function: FunctionParcelable? = null
    private var _axisColor: Int = ResourcesCompat.getColor(resources, R.color.textColor, null)
    private var _functionColor: Int = Color.RED

    private var _textColor: Int = Color.BLACK
    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.graphic_back_gray, null)
    private var _verticalAxisLetter: String = "x"
    private var _horizontalAxisLetter: String = "t"
    private var _zeroAxisLetter: String = "0"

    private lateinit var axisPaint: Paint
    private lateinit var functionPaint: Paint
    private var functionPath: Path = Path()
    private var indexWidth: Float = 0.0f
    private var indexHeight: Float = 0.0f
    private var zeroAxisHeight: Float = 0f
    private lateinit var textPaint: TextPaint
    private lateinit var smallTextPaint: TextPaint

    private val axisPaddingLeft: Float = 40f
    private val axisPaddingTop: Float = 20f
    private val axisPaddingRight: Float = 60f
    private val axisPaddingBottom: Float = 20f

    private var textSize = 30f // TODO: Is this in pixels or sp?
    private var smallTextSize = 20f // TODO: Is this in pixels or sp?

    var contentWidth: Float = width - axisPaddingLeft + axisPaddingRight
    var contentHeight: Float = height - axisPaddingTop + axisPaddingBottom

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
    var functionColor: Int
        get() = _functionColor
        set(value) {
            _functionColor = value
            invalidateTextPaintAndMeasurements()
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
    var function: FunctionParcelable?
        get() = _function
        set(value) {
            _function = value
            regeneratePath()
        }

    constructor(context: Context): super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        setBackgroundColor(backColor)
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
        this.post {
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
            indexHeight = it.fontMetrics.bottom
        }
        smallTextPaint.let {
            it.textSize = smallTextSize
            it.color = textColor
            zeroAxisHeight = it.measureText(_zeroAxisLetter)
        }
        axisPaint.let {
            it.color = axisColor
            it.strokeWidth = 1.dpToPx()
            it.strokeCap = Paint.Cap.BUTT
        }
        functionPaint.let {
            when(vertAxisLetter) {
                "x" -> it.color = positionFunctionColor
                "v" -> it.color = velocityFunctionColor
                "a" -> it.color = accelerationFunctionColor
            }
            it.style = Paint.Style.STROKE
            it.strokeWidth = 2.dpToPx()
            val dashEffect = DashPathEffect(arrayOf(10f, 5f).toFloatArray(), 0f)
            val cornerEffect = CornerPathEffect(10f * 3)
            val complexEffect = ComposePathEffect(dashEffect, cornerEffect)
            it.pathEffect = complexEffect
        }
    }

    fun regeneratePath() {
        if (height <= 0)
            return

        functionPath.reset()
        function?.let {
            Log.i("GraphView", "funcType: ${it.funcType}\r\nparams: ${it.x}, ${it.v}, ${it.a}")

            val funcType = it.funcType
            this.vertAxisLetter = funcType

            val x = it.x
            val a = it.a
            val v = it.v

            val xAxisLength = 12
            val yAxisLength = 12
            val graphFreqFactor = 2

            val widthScaleFactor = contentWidth / xAxisLength
            val heightScaleFactor = -contentHeight / yAxisLength

            val widthStretchFactor = 3

            var calculatedPointY = 0f
            var calculatedPointX: Float
            when (funcType) {
                "x" -> functionPath.moveTo(0f, x * heightScaleFactor)
                "v" -> functionPath.moveTo(0f, v * heightScaleFactor)
                "a" -> functionPath.moveTo(0f, a * heightScaleFactor)
            }

            for (t in 0 until xAxisLength * graphFreqFactor) {
                when (funcType) {
                    "x" -> calculatedPointY = (x + v * t + (a * t * t) / 2f) * heightScaleFactor
                    "v" -> calculatedPointY = (v + a * t) * heightScaleFactor
                    "a" -> calculatedPointY = a * heightScaleFactor
                }
                calculatedPointX = t * widthScaleFactor / graphFreqFactor * widthStretchFactor
                if (calculatedPointY > contentHeight / 2f
                        || calculatedPointY < contentHeight / -2f
                        || calculatedPointX > contentWidth)
                    break

                functionPath.lineTo(calculatedPointX, calculatedPointY)
            }

        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawLine(axisPaddingLeft, axisPaddingTop,
                axisPaddingLeft, height - axisPaddingBottom,
                axisPaint)
        canvas.drawLine(axisPaddingLeft, height / 2f,
                width - axisPaddingRight, height / 2f,
                axisPaint)
        function?.let {
            canvas.drawText(it.funcType,
                    axisPaddingLeft + indexWidth,
                    axisPaddingTop + indexHeight * 3,
                    textPaint)
        }
        horAxisLetter.let {
            canvas.drawText(it,
                    axisPaddingLeft + contentWidth - indexWidth * 2,
                    (height / 2) - indexHeight * 2,
                    textPaint)
        }
        _zeroAxisLetter.let {
            canvas.drawText(it,
                    indexWidth,
                    (height / 2) + zeroAxisHeight / 2,
                    smallTextPaint)
        }

        canvas.withTranslation(axisPaddingLeft, height / 2f) {
            functionPath.let {
                canvas.drawPath(it, functionPaint)
            }
        }
    }
}
