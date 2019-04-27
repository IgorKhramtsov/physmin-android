package com.example.physmin.views

import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import com.example.physmin.Pickable
import com.example.physmin.Settable

class ImageViewSettable(context: Context, attributeSet: AttributeSet?) : Settable(context, attributeSet) {

    override var par: GroupSettable? = null
    override var answerView: Pickable? = null
        set(value) {
            field = value
            if (value != null)
                paint.textSize = (value as TextViewPickable).textSize

            par!!.par!!.checkTestComplete(par!!.isAllChecked())
        }
    var correctAnsw: IntArray? = null
    var paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    var graph = GraphView(context, null)

    override fun isCorrect(): Boolean {
        if(answerView == null || correctAnsw == null)
            return false

        return correctAnsw!!.contains(answerView!!.answer)
    }

    init { }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        graph.draw(canvas)

        if(answerView == null)
            return

        // Move out static layout
        val staticLayout = StaticLayout((answerView as TextViewPickable).text, paint, canvas.width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
        canvas.save()
        canvas.translate(this.width / 2f - staticLayout.width / 2f, this.height / 2f - staticLayout.height / 2f)
        staticLayout.draw(canvas)
        canvas.restore()
    }
}