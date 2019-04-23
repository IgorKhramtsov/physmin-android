package com.example.physmin.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.Pickable
import com.example.physmin.R
import com.example.physmin.Settable

class ImageViewSettableBlank(context: Context, attributeSet: AttributeSet?) : GraphView(context, attributeSet), Settable {

    override var par: GroupSettable? = null
    override var answerView: Pickable? = null
        set(value) {
            field = value
            if(answerView != null)
                this.functions = (answerView as ImageViewPickable).graph.functions
            else
                this.functions = null
            par!!.par!!.checkTestComplete(par!!.isAllChecked())
        }
    var correctAnsw: IntArray? = null
    var paint = Paint(TextPaint.ANTI_ALIAS_FLAG)


    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.graphic_back_gray, null)
    private var _backShadowColor: Int = ResourcesCompat.getColor(resources, R.color.ui_shadow, null)
    var blurRadius = 4.dpToPx()
    var cornerRadius = 2.dpToPx()

    var generatedPanel: Bitmap? = null

    override fun isCorrect(): Boolean {
        if(answerView == null || correctAnsw == null)
            return false

        return correctAnsw!!.contains(answerView!!.answer)
    }


    override fun setParent(_parent: GroupSettable) {
        this.par = _parent
    }

    init {
        this.post {
            generatedPanel = generateInsideShadowPanel(width, height,
                    cornerRadius, blurRadius, _backColor, _backShadowColor, this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if(functions != null)
            super.onDraw(canvas)
        else
            generatedPanel?.let {
                canvas.drawBitmap(it, 0f, 0f, null)
            }
    }

}

