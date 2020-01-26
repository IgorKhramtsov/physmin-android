package com.physmin.android.views.items

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.physmin.android.Pickable
import com.physmin.android.R
import com.physmin.android.Settable
import com.physmin.android.Singleton
import com.physmin.android.views.GraphView
import com.physmin.android.views.dpToPx
import com.physmin.android.views.generateInsideShadowPanel
import com.physmin.android.views.generateShadowPanel

class ImageViewSettableBlank(context: Context, attributeSet: AttributeSet?) : Settable(context, attributeSet) {

    var correctAnswers: IntArray? = null
    private var graph = GraphView(context, null)

    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.graphic_back_gray, null)
    private var _backShadowColor: Int = ResourcesCompat.getColor(resources, R.color.ui_shadow, null)
    var blurRadius = 4.dpToPx()
    var cornerRadius = 2.dpToPx()

    private val generatedPanel: Bitmap? by Singleton {
        generateInsideShadowPanel(width, height, cornerRadius, blurRadius, _backColor, _backShadowColor, this)
    }

    override fun onAnswerChanged(answerView: Pickable?) {
        super.onAnswerChanged(answerView)

        if(answerView != null) {
            this.graph.functions = (answerView as ImageViewPickable).graph.functions
            this.graph.runAnimation(this)
        }
        else
            this.graph.functions = null
    }

    override fun isCorrect(): Boolean {
        if(answerView == null || correctAnswers == null)
            return false

        return correctAnswers!!.contains(answerView!!.answer)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(graph.functions != null)
            graph.draw(canvas)
        else
            if(generatedPanel != null) canvas.drawBitmap(generatedPanel!!, 0f, 0f, null)
    }

}

