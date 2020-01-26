package com.physmin.android.views

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import com.physmin.android.R
import com.physmin.android.activities.FunctionParcelable
import kotlin.math.abs
import kotlin.math.min


// TODO: Make arrows on axis

open class GraphView(context: Context, attrs: AttributeSet?): View(context, attrs) {

    private var yAxisLength = 12f
    private val xAxisLength = 12f
    private val step = 0.2f

    private val positionFunctionColor = ResourcesCompat.getColor(resources, R.color.graphic_position, null)
    private val velocityFunctionColor = ResourcesCompat.getColor(resources, R.color.graphic_velocity, null)
    private val accelerationFunctionColor = ResourcesCompat.getColor(resources, R.color.graphic_acceleration, null)

    private var _backColor = ResourcesCompat.getColor(resources, R.color.graphic_back_gray, null)
    private var _divider = ResourcesCompat.getColor(resources, R.color.graphic_divider, null)
    private var _selectedAreaColorLeft = ResourcesCompat.getColor(resources, R.color.graphic_selectedLeft, null)
    private var _selectedAreaColorRight = ResourcesCompat.getColor(resources, R.color.graphic_selectedRight, null)
    private var _axisColor = ResourcesCompat.getColor(resources, R.color.textColor, null)
    private var _textColor = ResourcesCompat.getColor(resources, R.color.textColor, null)
    private var _smallTextColor = ResourcesCompat.getColor(resources, R.color.textColorGray, null)
    private var _verticalAxisLetter = "x"
    private var _horizontalAxisLetter = "t"
    private var _zeroAxisLetter = "0"

    private val axisPaddingLeft = 40f
    private val axisPaddingTop = 20f
    private val axisPaddingRight = 20f
    private val axisPaddingBottom = 20f

    private var backgroundPaint: Paint
    private var axisPaint: Paint
    private var functionPaint: Paint
    private var functionDividerPaint: Paint
    private var selectedDividerPaintLeft: Paint
    private var selectedDividerPaintRight: Paint
    private var textPaint: TextPaint
    private var smallTextPaint: TextPaint

    private var functionPath = Path()
    private var functionDividers = ArrayList<Float>()
    private var pathAnimator = ValueAnimator()
    private var textAnimator = ValueAnimator()
    private var pathAnimationDuration = 600L
    private var textAnimationDuration = 400L
    private var indexTextWidth = 0.0f
    private var indexTextHeight = 0.0f
    private var zeroAxisTextHeight = 0f
    private var upperLimitTextWidth = 0f
    private var textSize = 27f
    private var smallTextSize = 20f

    private var contentWidth = width - (axisPaddingLeft + axisPaddingRight)
    private var contentHeight = height - (axisPaddingTop + axisPaddingBottom)

    private var _functions: ArrayList<FunctionParcelable>? = null

    var selectedArea: ArrayList<ArrayList<Int>> = ArrayList()
        set(value) {
            field = value
            invalidate()
        }
    var needShowDividers: Boolean = false
    set(value) {
        field = value
        invalidate()
    }

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
            regeneratePath()
        }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.GraphView, 0, 0)
        needShowDividers = a.getBoolean(R.styleable.GraphView_needShowDividers, false)
        a.recycle()

        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }
        smallTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        }
        axisPaint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
        }
        functionPaint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            style = Paint.Style.STROKE
            strokeWidth = 2.dpToPx()
        }
        selectedDividerPaintLeft = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = _selectedAreaColorLeft
            style = Paint.Style.STROKE
            strokeWidth = 2.dpToPx()
            pathEffect = DashPathEffect(floatArrayOf(16f, 16f), 0f)
        }
        selectedDividerPaintRight = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = _selectedAreaColorRight
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(16f, 16f), 16f)
            strokeWidth = 2.dpToPx()
        }
        functionDividerPaint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            color = _divider
            style = Paint.Style.STROKE
            strokeWidth = 2.dpToPx()
            pathEffect = getPathEffect(0f)
        }
        backgroundPaint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG
            style = Paint.Style.FILL
            color = backColor
        }
        invalidateTextPaintAndMeasurements()

        post {
            runAnimation()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = resolveSizeAndState(height, heightMeasureSpec, 0)
        val width = resolveSizeAndState(width, widthMeasureSpec, 0)

        setMeasuredDimension(width, height)
        contentWidth = width - axisPaddingLeft - axisPaddingRight
        contentHeight = height - axisPaddingTop - axisPaddingBottom
        functionPaint.pathEffect = getPathEffect(0f)
        regeneratePath()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        runAnimation()
    }

    private fun invalidateTextPaintAndMeasurements() {
        textPaint.let {
            it.color = textColor
            it.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            it.textSize = 0f
            indexTextWidth = it.measureText(vertAxisLetter)
            indexTextHeight = abs(it.fontMetrics.ascent)
        }
        smallTextPaint.let {
            it.color = _smallTextColor
            it.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            it.textSize = 0f
            zeroAxisTextHeight = abs(it.fontMetrics.ascent)
            upperLimitTextWidth = it.measureText((yAxisLength / 2).toString())
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
        }
    }

    private fun regeneratePath() {
        if (contentHeight <= 0 || functions === null)
            return

        this.vertAxisLetter = functions!![0].funcType
        if(functions!!.count() > 1)
            this.setLayerType(LAYER_TYPE_SOFTWARE, null) // why?

        if (this.vertAxisLetter == "a") yAxisLength = 1f
        else yAxisLength = 12f
        val heightScaleFactor = -(contentHeight / yAxisLength)
        val widthScaleFactor = contentWidth / (xAxisLength / step)
        var calculatedPointY: Float
        var calculatedPointX = 0f
        var len: Float

        functionPath.reset()
        functionDividers.clear()
        for (function in functions!!) {
            Log.i("GraphView", "funcType: ${function.funcType}\r\nparams: ${function.x}, ${function.v}, ${function.a} ${function.len}")

            calculatedPointY = calculateFunctionValue(function, 0f) * heightScaleFactor
            functionPath.moveTo(calculatedPointX, calculatedPointY)

            functionDividers.add(calculatedPointX)
            len = function.len.toFloat()

            var localT = 0f
            while (localT < len || (len.isZero() && calculatedPointX < contentWidth)) {
                localT = min(localT + step, len)

                calculatedPointX += widthScaleFactor
                calculatedPointY = calculateFunctionValue(function, localT) * heightScaleFactor

                if (abs(calculatedPointY) > contentHeight / 2f) {
                    functionPath.moveTo(calculatedPointX, calculatedPointY)
                    continue
                }

                functionPath.lineTo(calculatedPointX, calculatedPointY)
            }
        }
        functionDividers.add(calculatedPointX)
    }

    private fun calculateFunctionValue(function: FunctionParcelable, t: Float): Float {
        return when (function.funcType) {
            "x" -> (function.x + function.v * t + (function.a * t * t) / 2f)
            "v" -> (function.v + function.a * t)
            else -> function.a
        }
    }

    fun runAnimation(view: View = this) {
        pathAnimator.apply {
            this.setInterpolator { v -> getInterpolation(v) }
            duration = pathAnimationDuration
            this.setValues(PropertyValuesHolder.ofFloat("phase", 0f, 1f))
            this.addUpdateListener {
                val phase = it.getAnimatedValue("phase") as Float
                functionPaint.pathEffect = getPathEffect(phase)
                view.invalidate()
            }
        }.start()
        textAnimator.apply {
            this.interpolator = OvershootInterpolator()
            duration = textAnimationDuration
            this.setValues(PropertyValuesHolder.ofFloat("txSize", 0f, textSize),
                    PropertyValuesHolder.ofFloat("smTxSize", 0f, smallTextSize))
            this.addUpdateListener {
                val smTxSize = it.getAnimatedValue("smTxSize") as Float
                val txSize = it.getAnimatedValue("txSize")as Float
                textPaint.textSize = txSize
                indexTextWidth = textPaint.measureText(vertAxisLetter)
                indexTextHeight = abs(textPaint.fontMetrics.ascent)
                smallTextPaint.textSize = smTxSize
                zeroAxisTextHeight = abs(smallTextPaint.fontMetrics.ascent)
                upperLimitTextWidth = smallTextPaint.measureText((yAxisLength / 2).toString())
                invalidate()
            }
            startDelay = pathAnimationDuration / 4
        }.start()
    }

    private fun getPathEffect(phase: Float):DashPathEffect {
        Log.d("shit", "contentWi = $contentWidth")
        val pathLength = PathMeasure(functionPath, false).length
//        val pathLength = contentWidth - (functions?.count()?:1)

        return DashPathEffect(floatArrayOf(pathLength, pathLength),
                ((1 - phase) * pathLength).coerceAtLeast(0f))
    }

    var t: Float = 0f
    fun getInterpolation(argT:Float, b:Float = 0f, c: Float= 1f, d:Float = 0.7f):Float {
        // source: http://robertpenner.com/easing/
        t = argT / d
        return if (t < 1 / 2.75f) {
            c * (7.5625f * t * t) + b
        } else if (t < (2 / 2.75f)) {
            t -= (1.5f / 2.75f)
            c * (7.5625f * t * t + .75f) + b
        } else if (t < (2.5 / 2.75)) {
            t -= (2.25f / 2.75f)
            c * (7.5625f * t * t + .9375f) + b
        } else {
            t -= (2.625f / 2.75f)
            c * (7.5625f * t * t + .984375f) + b
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = canvas.width
        val height = canvas.height
        contentWidth = width - (axisPaddingLeft + axisPaddingRight)
        contentHeight = height - (axisPaddingTop + axisPaddingBottom)
        if (functionPath.isEmpty) regeneratePath()
        //if (!animationFinished && !valueAnimator.isRunning) runAnimation()

        drawRoundRect(backgroundPaint, canvas, width, height, 3f,0f)

        canvas.drawLine(axisPaddingLeft, axisPaddingTop,
                axisPaddingLeft, height - axisPaddingBottom,
                axisPaint)
        canvas.drawLine(axisPaddingLeft, height / 2f,
                width - axisPaddingRight, height / 2f,
                axisPaint)

        functionPath.let {
            canvas.withTranslation(axisPaddingLeft, height / 2f) {
                canvas.drawPath(functionPath, functionPaint)
            }
            canvas.drawText((yAxisLength / 2).toString(),
                    upperLimitTextWidth / 3,
                    axisPaddingTop + zeroAxisTextHeight / 2,
                    smallTextPaint)
            if (functionDividers.count() > 2 && needShowDividers)
                functionDividers.forEachIndexed { index, divider ->
                    canvas.withTranslation(axisPaddingLeft, axisPaddingTop) {
                        if (selectedArea.count() > 0 && selectedArea[0].contains(index)) {
                            canvas.drawLine(divider, 0f, divider, contentHeight, selectedDividerPaintLeft)
                            if (selectedArea[1].contains(index))
                                canvas.drawLine(divider, 0f, divider, contentHeight, selectedDividerPaintRight)
                        } else if (selectedArea.count() > 0 && selectedArea[1].contains(index)) {
                            canvas.drawLine(divider, 0f, divider, contentHeight, selectedDividerPaintRight)
                        } else {
                            canvas.drawLine(divider, 0f, divider, contentHeight, functionDividerPaint)
                        }
                        canvas.drawText(index.toString(),
                                if (index ==functionDividers.count() - 1) divider - 25f else divider + 15f,
                                contentHeight - 10f, textPaint)
                    }
                }
        }

        vertAxisLetter.let {
            canvas.drawText(it,
                    axisPaddingLeft + indexTextWidth / 2,
                    axisPaddingTop + indexTextHeight,
                    textPaint)
        }
        horAxisLetter.let {
            canvas.drawText(it,
                    axisPaddingLeft + contentWidth - indexTextWidth * 2,
                    (height / 2) - indexTextHeight / 2f,
                    textPaint)
        }
        _zeroAxisLetter.let {
            canvas.drawText(it,
                    indexTextWidth,
                    (height / 2) + (zeroAxisTextHeight / 2),
                    smallTextPaint)
        }

    }

}