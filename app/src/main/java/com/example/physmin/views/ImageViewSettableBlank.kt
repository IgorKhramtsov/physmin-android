package com.example.physmin.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.ImageView
import com.example.physmin.Pickable
import com.example.physmin.R
import com.example.physmin.Settable

class ImageViewSettableBlank(context: Context, attributeSet: AttributeSet?) : ImageView(context, attributeSet), Settable {

    override var par: GroupSettable? = null
    override var answerView: Pickable? = null
        set(value) {
            field = value
            if(answerView != null)
                this.setImageDrawable((answerView as ImageView).drawable)
            else
                this.setImageResource(R.color.transparent)
            par!!.par!!.checkTestComplete(par!!.isAllChecked())
        }
    var correctAnsw: IntArray? = null

    override fun isCorrect(): Boolean {
        if(answerView == null || correctAnsw == null)
            return false

        return correctAnsw!!.contains(answerView!!.answer)
    }

    var paint = Paint(TextPaint.ANTI_ALIAS_FLAG)

    init { }

    override fun setParent(_parent: GroupSettable) {
        this.par = _parent
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.strokeWidth = 10f

        canvas.drawLine(0f, 0f, width * 1f, 0f, paint)
        canvas.drawLine(0f, 0f, 0f, height * 1f, paint)
        canvas.drawLine(width * 1f, 0f, width * 1f, height * 1f, paint)
        canvas.drawLine(0f, height * 1f, width * 1f, height * 1f, paint)
    }

}

