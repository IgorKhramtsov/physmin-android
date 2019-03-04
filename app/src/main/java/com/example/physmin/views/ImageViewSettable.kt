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

open class ImageViewSettable(context: Context, attributeSet: AttributeSet?) : ImageView(context, attributeSet), Settable {

    override var par: GroupSettable? = null
    override var answerView: Pickable? = null
        set(value) {
            field = value
            par!!.par!!.checkTestComplete(par!!.isAllChecked())
        }
    var correctAnsw: Int? = null

    override fun isCorrect(): Boolean {
        if(answerView == null || correctAnsw == null)
            return false

        return answerView?.answer == correctAnsw
    }


    var paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)

    init { }

    override  fun setParent(_parent: GroupSettable) {
        this.par = _parent
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(answerView == null)
            return
        paint.textSize = (answerView as TextView).textSize
        val text = (answerView as TextView).text.toString()

        val staticLayout = StaticLayout(text, paint, canvas.width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
        canvas.save()
        canvas.translate(this.width / 2f - staticLayout.width / 2f, this.height / 2f - staticLayout.height / 2f)
        staticLayout.draw(canvas)
        canvas.restore()
    }

    open fun onClick() {

    }

}