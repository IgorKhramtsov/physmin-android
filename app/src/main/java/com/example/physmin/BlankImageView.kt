package com.example.physmin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class BlankImageView(context: Context, attributeSet: AttributeSet?) : ImageView(context, attributeSet), Settable {

    override var par: SettableGroup? = null
    override var answerView: Pickable? = null
        set(value) {
            field = value
            if(answerView != null)
                this.setImageDrawable((answerView as ImageView).drawable)
            else
                this.setImageResource(R.color.transparent)
        }
    var paint = Paint(TextPaint.ANTI_ALIAS_FLAG)

    init {
//        this.setImageResource(R.color.transparent)
    }

    override fun setParent(_parent: SettableGroup) {
        this.par = _parent
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawLine(0f, 0f, width * 1f, 0f, paint)
        canvas.drawLine(0f, 0f, 0f, height * 1f, paint)
        canvas.drawLine(width * 1f, 0f, width * 1f, 0f, paint)
        canvas.drawLine(0f, height * 1f, 0f, height * 1f, paint)
    }

}

