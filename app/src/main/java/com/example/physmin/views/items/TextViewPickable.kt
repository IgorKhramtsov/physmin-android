package com.example.physmin.views.items

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.Pickable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import androidx.core.graphics.withTranslation
import com.example.physmin.R
import com.example.physmin.views.spToPx

class TextViewPickable(context: Context, attrs: AttributeSet?): Pickable(context, attrs) {

    var outlineColor = Color.BLUE

    private var _textMeasuredWidth = 0f
    private var _textMeasuredHeight = 0f
    private var _paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var _staticLayout: StaticLayout? = null

    var text: String = ""
        set(value) {
            field = value
            invalidatePaint()
            invalidate()
        }
    private var textSize = 14.spToPx()
        set(value) {
            field = value
            invalidatePaint()
            invalidate()
        }
    private var textColor = ResourcesCompat.getColor(resources, com.example.physmin.R.color.textColor, null)
        set(value) {
            field = value
            _paint.color = value
            invalidate()
        }

    private fun invalidatePaint() {
        _paint.let {
            it.textSize = textSize
            it.color = textColor
            _textMeasuredWidth = it.measureText(text)
            _textMeasuredHeight = Math.abs(it.fontMetrics.top)
        }
        invalidateStaticLayout()
    }

    private fun invalidateStaticLayout() {
        if (width <= 0) // ??
            return

        _staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            StaticLayout.Builder.obtain(text, 0, text.length, _paint, width - paddingRight - paddingLeft)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER).build()
        else
            StaticLayout(text, _paint, width - paddingRight - paddingLeft, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false)
    }

    init {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.TextViewPickable, 0, 0)

        if( a.hasValue(R.styleable.TextViewPickable_textViewPickableText))
            text = a.getString(R.styleable.TextViewPickable_textViewPickableText)!!
        if( a.hasValue(R.styleable.TextViewPickable_textViewPickableAnswer))
            answer = a.getInteger(R.styleable.TextViewPickable_textViewPickableAnswer, 0)
        if( a.hasValue(R.styleable.TextViewPickable_textViewPickableTextColor))
            textColor = a.getColor(R.styleable.TextViewPickable_textViewPickableTextColor, 0)
        if( a.hasValue(R.styleable.TextViewPickable_textViewPickableTextSize))
            textSize = a.getDimensionPixelSize(R.styleable.TextViewPickable_textViewPickableTextSize, 0).toFloat()
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        invalidatePaint()

        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY)
            return setMeasuredDimension(width, height)

        val rows = Math.max(Math.round(_textMeasuredWidth / width), 1)
        height = paddingTop + paddingBottom + Math.round(_textMeasuredHeight) * (rows + 2)

        height = resolveSizeAndState(height, heightMeasureSpec, 0)
        width = resolveSizeAndState(width, widthMeasureSpec, 0)

        setMeasuredDimension(width, height)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (_staticLayout === null) invalidateStaticLayout()
        canvas.withTranslation(paddingLeft.toFloat(), paddingTop.toFloat()) {
            _staticLayout?.draw(canvas)
        }
    }

    override fun select() {
        super.select()

        _paint.setShadowLayer(2.3f, 0f, 0f, outlineColor)
        this.scaleX = 1.05f
        this.scaleY = 1.05f
        invalidate()
    }

    override fun deselect() {
        super.deselect()

        _paint.setShadowLayer(0f, 0f, 0f, outlineColor)
        this.scaleX = 1f
        this.scaleY = 1f
        invalidate()
    }

}
