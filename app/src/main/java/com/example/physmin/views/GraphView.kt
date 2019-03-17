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

    // x - > green
    // v -> blue
    // a -> yellow

    private var _function: FunctionParcelable? = null
    private var _axisColor: Int = ResourcesCompat.getColor(resources, R.color.textColor, null)
    private var _functionColor: Int = Color.RED
    private var _textColor: Int = Color.BLACK
    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.graphic_back_gray, null)
    private var _vertAxisLetter: String = "x"
    private var _horAxisLetter: String = "t"
    private var zeroAxisLetter: String = "0"

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
        get() = _vertAxisLetter
        set(value) {
            _vertAxisLetter = value
            invalidateTextPaintAndMeasurements()
        }
    var horAxisLetter: String
        get() = _horAxisLetter
        set(value) {
            _horAxisLetter = value
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
        // Load attributes
//        val a = context.obtainStyledAttributes(
//                attrs, R.styleable.GraphView, defStyle, 0)
//        a.recycle()

        // Set up a default TextPaint object

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
            zeroAxisHeight = it.measureText(zeroAxisLetter)
        }
        axisPaint.let {
            it.color = axisColor
            it.strokeWidth = 3.pxToDp()
            it.strokeCap = Paint.Cap.BUTT
        }
        functionPaint.let {
            it.color = functionColor
            it.style = Paint.Style.STROKE
            it.strokeWidth = 6.pxToDp()
            val dashEffect = DashPathEffect(arrayOf(10f,10f).toFloatArray(), 0f)
            val cornerEffect = CornerPathEffect(10f * 3)
            val complexEffect = ComposePathEffect(dashEffect, cornerEffect)
            it.pathEffect = complexEffect
        }

        regeneratePath()
    }

    fun regeneratePath() {
        if(height <= 0)
            return

        functionPath.reset()
        function?.let {
            Log.i("GraphView","funcType: ${it.funcType}\r\n" +
                    "coords: ${it.x}, ${it.v}, ${it.a}")
            val funcType = it.funcType
            val x = it.x
            val a = it.a
            val v = it.v

            val xAxisLength = 12
            val yAxisLength = 12
            val graphFreqFactor = 2

            val widthScaleFactor = contentWidth / xAxisLength
            val heightScaleFactor = -contentHeight / yAxisLength

            val widthStretchFactor = 3

            var calculatedPoint: Float
            when(funcType) {
                "x" -> {
                    functionPath.moveTo(0f, x * heightScaleFactor)
                    for (t in 0 until xAxisLength * graphFreqFactor) {
                        calculatedPoint = (x + v * t + (a * t * t) / 2f) * heightScaleFactor
                        if(calculatedPoint > contentHeight /2f || calculatedPoint < contentHeight / -2f)
                            break

                        functionPath.lineTo(t * widthScaleFactor / graphFreqFactor * widthStretchFactor, calculatedPoint)
                    }
                }
                "v" -> {
                    functionPath.moveTo(0f, v * heightScaleFactor)
                    for (t in 0 until xAxisLength) {
                        calculatedPoint = (v + a * t) * heightScaleFactor
                        if(calculatedPoint > contentHeight /2f || calculatedPoint < contentHeight / -2f)
                            break

                        functionPath.lineTo(t * widthScaleFactor, calculatedPoint)
                    }
                }
                "a" -> {
                    functionPath.moveTo(0f, a * heightScaleFactor)
                    for (t in 0 until xAxisLength) {
                        calculatedPoint = a * heightScaleFactor
                        if(calculatedPoint > contentHeight /2f || calculatedPoint < contentHeight / -2f)
                            break

                        functionPath.lineTo(t * widthScaleFactor, calculatedPoint)
                    }
                }
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
                    axisPaddingLeft + indexWidth ,
                    axisPaddingTop + indexHeight * 3,
                    textPaint)
        }
        horAxisLetter.let {
            canvas.drawText(it,
                    axisPaddingLeft + contentWidth - indexWidth * 2,
                    (height / 2) - indexHeight * 2,
                    textPaint)
        }
        zeroAxisLetter.let {
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
