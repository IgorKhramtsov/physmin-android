package com.example.physmin.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.util.Size
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.Pickable
import com.example.physmin.R

/**
 * TODO: document your custom view class.
 */

class TextViewPickable(context: Context, attrs: AttributeSet?) : Pickable(context, attrs) {

    override var picked: Boolean = false
    override var par: GroupPickable? = null
    private var outlineColor: Int = Color.BLUE
    override var answer: Int = -1
    var _text: String = ""
    var _textMeasuredWidth: Float = 0f
    var _textMeasuredHeight: Float = 0f
    var paint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    var textSize: Float = 14f
    var textColor: Int = ResourcesCompat.getColor(resources, R.color.textColor, null)

    var text: String
        get() = _text
        set(value) {
            _text = value
            paint.let {
                _textMeasuredWidth = it.measureText(_text)
                _textMeasuredHeight = it.fontMetrics.bottom
            }
        }

    init {
        if(attrs != null) {
            val ar: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewPickable)
            outlineColor = ar.getColor(R.styleable.TextViewPickable_outlineColor, ContextCompat.getColor(context, R.color.textview_pick_outline))
            answer = ar.getInt(R.styleable.TextViewPickable_answer, 0)

            ar.recycle()
        }

        paint.textAlign = Paint.Align.CENTER
    }

    override fun setParent(_parent: GroupPickable) {
        this.par = _parent
    }

    override fun isPicked() : Boolean {
        return picked
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        text.let {
            canvas.drawText(it,
                    paddingLeft,
                    paddingTop,
                    (width - paddingRight).toFloat(),
                    (height - paddingBottom).toFloat(),
                    paint)
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
