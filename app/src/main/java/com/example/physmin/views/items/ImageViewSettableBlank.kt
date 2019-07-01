package com.example.physmin.views.items

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.Pickable
import com.example.physmin.R
import com.example.physmin.Settable
import com.example.physmin.views.GraphView
import com.example.physmin.views.dpToPx
import com.example.physmin.views.generateInsideShadowPanel

class ImageViewSettableBlank(context: Context, attributeSet: AttributeSet?) : Settable(context, attributeSet) {

    var correctAnswers: IntArray? = null
    private var graph = GraphView(context, null)

    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.graphic_back_gray, null)
    private var _backShadowColor: Int = ResourcesCompat.getColor(resources, R.color.ui_shadow, null)
    var blurRadius = 4.dpToPx()
    var cornerRadius = 2.dpToPx()

    private var generatedPanel: Bitmap? = null

    override fun onAnswerChanged(answerView: Pickable?) {
        super.onAnswerChanged(answerView)

        if(answerView != null)
            this.graph.functions = (answerView as ImageViewPickable).graph.functions
        else
            this.graph.functions = null
    }

    override fun isCorrect(): Boolean {
        if(answerView == null || correctAnswers == null)
            return false

        return correctAnswers!!.contains(answerView!!.answer)
    }

    init {
        this.post {
            generatedPanel = generateInsideShadowPanel(width, height, cornerRadius, blurRadius, _backColor, _backShadowColor, this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(graph.functions != null)
            graph.draw(canvas)
        else
            if(generatedPanel != null) canvas.drawBitmap(generatedPanel!!, 0f, 0f, null)
    }

}

