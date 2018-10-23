package com.example.physmin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView

open class ImageSettableView(context: Context, attributeSet: AttributeSet?) : ImageView(context, attributeSet), Settable {

    override var par: SettableGroup? = null
    override var answerView: Pickable? = null
        set(value) {
            field = value
            if(par!!.isAllChecked())
                par!!.par!!.testComplete()
        }
    override var correctAnswer: Short = -1


    var paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)

    init {
        var a = 1
    }

    override  fun setParent(_parent: SettableGroup) {
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