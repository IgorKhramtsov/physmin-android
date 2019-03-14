package com.example.physmin.views

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withScale
import com.example.physmin.R
import org.json.JSONArray
import org.json.JSONObject

// TODO: Make arrows on axis

//fun Int.spToPx(): Float = (this * Resources.getSystem().displayMetrics.density)
fun Int.pxToDp(): Float = this / Resources.getSystem().displayMetrics.density;
fun Int.pxToSp(): Float = this / Resources.getSystem().displayMetrics.scaledDensity

class GraphicView: View {

    // Y: -3 to 3
    // X: 0 to 12(excluding)

    private var _axisColor: Int = Color.BLACK
    private var _functionColor: Int = Color.RED
    private var _textColor: Int = Color.BLACK
    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.graphic_back_gray, null)
    private var _vertAxisLetter: String = "x"
    private var _function: JSONArray? = null
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
            invalidateTextPaintAndMeasurements()
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
    var function: JSONArray?
        get() = _function
        set(value) {
            _function = value
            invalidateTextPaintAndMeasurements()
        }

    constructor(context: Context): super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
//        val a = context.obtainStyledAttributes(
//                attrs, R.styleable.GraphicView, defStyle, 0)
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

        val functions = "{function: [\n" +
                "    {\n" +
                "\t\tfuncType: \"x\",\n" +
                "\t\tparams: {\n" +
                "            x: 5,        \n" +
                "            v: 3,\n" +
                "            a: 1\t\n" +
                "        }\n" +
                "\t}\n" +
                "]}"
        _function = JSONObject(functions.substring(functions.indexOf("{"), functions.lastIndexOf("}") + 1)).
                optJSONArray("function")

        // Update TextPaint and text measurements from attributes
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
            it.strokeCap = Paint.Cap.ROUND
        }
        functionPaint.let {
            it.color = functionColor
            it.style = Paint.Style.STROKE
            it.strokeWidth = 6.pxToDp()
            it.pathEffect = DashPathEffect(arrayOf(10f,10f).toFloatArray(), 0f)
        }

        function?.let {
            functionPath.reset()
            var from: Array<Float>
            var to: Array<Float>
            var type: String

            for(i in 0 until it.length()) {

                var funcType = ""
                var x = 0
                var a = 0
                var v = 0
                it.getJSONObject(i).apply {
                    funcType = getString("funcType")
                    x = getJSONObject("params").getInt("x")
                    v = getJSONObject("params").getInt("v")
                    a = getJSONObject("params").getInt("a")
                }
                var maxT = x + v * 12 + (a * 12*12) / 2f
                var minT = x
                var funcHeight = Math.abs(maxT) + Math.abs(minT)
                var scaleFactor = -300 / funcHeight // TODO: get height


                when(funcType) {
                    "x" -> {
                        functionPath.moveTo(0f, x * scaleFactor)
                        for (t in 0 until 12)
                            functionPath.lineTo(t * 10f, (x + v * t + (a * t * t) / 2f) * scaleFactor)
                    }
                    "v" -> {
                        functionPath.moveTo(0f, v * scaleFactor)
                        for (t in 0 until 12)
                            functionPath.lineTo(t * 10f, (v  + a * t ) * scaleFactor)
                    }
                    "a" -> {
                        functionPath.moveTo(0f, a * scaleFactor)
                        for (t in 0 until 12)
                            functionPath.lineTo(t * 10f, (a) * scaleFactor)
                    }
                }


            }
        }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        contentWidth = MeasureSpec.getSize(measuredWidth) - axisPaddingLeft - axisPaddingRight
        contentHeight = height - axisPaddingTop - axisPaddingBottom




    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawLine(axisPaddingLeft, axisPaddingTop,
                axisPaddingLeft, height - axisPaddingBottom,
                axisPaint)
        canvas.drawLine(axisPaddingLeft, height / 2f,
                width - axisPaddingRight, height / 2f,
                axisPaint)
        vertAxisLetter.let {
            canvas.drawText(it,
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

        canvas.translate(axisPaddingLeft, height / 2f)
        functionPath.let {
            canvas.drawPath(it, functionPaint)
        }
    }
}
