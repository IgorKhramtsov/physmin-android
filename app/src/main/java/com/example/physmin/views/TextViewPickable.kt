package com.example.physmin.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.Pickable
import android.opengl.ETC1.getWidth
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.util.Log
import androidx.core.graphics.withTranslation

class TextViewPickable(context: Context, attrs: AttributeSet?) : Pickable(context, attrs) {

    override var picked = false
    override var par: GroupPickable? = null

    var paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    var staticLayout: StaticLayout? = null

    var _textMeasuredWidth = 0f
    var _textMeasuredHeight = 0f

    private var outlineColor = Color.BLUE
    override var answer = -1
    var _text = ""
    var _textSize = 14.spToPx()
    var _textColor = ResourcesCompat.getColor(resources, com.example.physmin.R.color.textColor, null)


    var text: String
        get() = _text
        set(value) {
            _text = value
            invalidatePaint()
            invalidate()
        }

    var textSize: Float
        get() = _textSize
        set(value) {
            _textSize = value
            invalidatePaint()
            invalidate()
        }
    var textColor: Int
        get() = _textColor
        set(value) {
            _textColor = value
            invalidatePaint()
            invalidate()
        }

    private fun invalidatePaint() {
        paint.let {
            it.textSize = textSize
            it.color = textColor
            _textMeasuredWidth = it.measureText(_text)
            _textMeasuredHeight = Math.abs(it.fontMetrics.top)
//            it.textAlign = Paint.Align.CENTER
        }
    }
    private fun invalidateStaticLayout() {
        if (width <= 0)
            return

        staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width - paddingRight - paddingLeft)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER).build()
        else
            StaticLayout(text, paint, width - paddingRight - paddingLeft, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false)

    }

    init {
//        paint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        invalidatePaint()

        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)

        val rows = Math.max(Math.round(_textMeasuredWidth / width), 1)
        height = paddingTop + paddingBottom + Math.round(_textMeasuredHeight) * (rows + 2)

        height = resolveSizeAndState(height, heightMeasureSpec, 0)
        width = resolveSizeAndState(width, widthMeasureSpec, 0)

        setMeasuredDimension(width, height)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (staticLayout === null) invalidateStaticLayout()
        canvas.withTranslation(paddingLeft.toFloat(), paddingTop.toFloat()) {
            staticLayout?.draw(canvas)
        }
    }

    override fun pick() {
        picked = true
//        this.setShadowLayer(2.3f, 0f, 0f, outlineColor)
        this.scaleX = 1.05f
        this.scaleY = 1.05f
    }
    override fun unPick() {
        picked = false
//        this.setShadowLayer(0f, 0f, 0f, outlineColor)
        this.scaleX = 1f
        this.scaleY = 1f
    }

}
