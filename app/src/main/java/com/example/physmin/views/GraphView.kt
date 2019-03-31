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

    private var _functions: ArrayList<FunctionParcelable>? = null
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
    var functions: ArrayList<FunctionParcelable>?
        get() = _functions
        set(value) {
            _functions = value
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
            when (vertAxisLetter) {
                "x" -> it.color = positionFunctionColor
                "v" -> it.color = velocityFunctionColor
                "a" -> it.color = accelerationFunctionColor
            }
            it.style = Paint.Style.STROKE
            it.strokeWidth = 2.dpToPx()
            val dashEffect = DashPathEffect(arrayOf(10f, 5f).toFloatArray(), 0f)
            val cornerEffect = CornerPathEffect(10f * 3)
            val complexEffect = ComposePathEffect(dashEffect, cornerEffect)
//            it.pathEffect = dashEffect
        }
    }

    fun regeneratePath() {
        if (height <= 0)
            return

        functionPath.reset()
        functions?.let {
            var lastT = 0f

            var x = it[0].x
            var a = it[0].a
            var v = it[0].v
            val yAxisLength = 12
            val xAxisConstLength = 12f
            val heightScaleFactor = -contentHeight / yAxisLength
            val widthScaleFactor = contentWidth / xAxisConstLength

            val funcType = it[0].funcType
            this.vertAxisLetter = funcType
            when (funcType) {
                "x" -> functionPath.moveTo(0f, x * heightScaleFactor)
                "v" -> functionPath.moveTo(0f, v * heightScaleFactor)
                "a" -> functionPath.moveTo(0f, a * heightScaleFactor)
            }
            for (function in it) {
                Log.e("GraphView", "funcType: ${function.funcType}\r\nparams: ${function.x}, ${function.v}, ${function.a} ${function.t}")

                x = function.x
                a = function.a
                v = function.v
                val t = if (function.t > 0) function.t else 12

                val xAxisStart = lastT
                val xAxisLength = t
                val step = 0.5f

//                val widthStretchFactor = 1

                var calculatedPointY = 0f
                var calculatedPointX: Float

                when (funcType) {
                    "x" -> functionPath.moveTo(xAxisStart * widthScaleFactor, x * heightScaleFactor)
                    "v" -> functionPath.moveTo(xAxisStart * widthScaleFactor, v * heightScaleFactor)
                    "a" -> functionPath.moveTo(xAxisStart * widthScaleFactor, a * heightScaleFactor)
                }

                var i = 0f
                while (i <= xAxisLength - xAxisStart) {
                    calculatedPointY = heightScaleFactor *
                            when (funcType) {
                                "x" -> (x + v * i + (a * i * i) / 2f)
                                "v" -> (v + a * i)
                                else -> a
                            }
                    calculatedPointX = (xAxisStart + i) * widthScaleFactor
                    if (calculatedPointY > contentHeight / 2f
                            || calculatedPointY < contentHeight / -2f)
//                            || calculatedPointX > contentWidth)
                        break

                    functionPath.lineTo(calculatedPointX, calculatedPointY)

                    i += step
                }
                lastT = xAxisLength - xAxisStart
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
        functions?.let {
            canvas.drawText(it[0].funcType,
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
