package com.example.physmin.views.items

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import androidx.core.graphics.withTranslation
import com.example.physmin.Pickable
import com.example.physmin.Settable
import com.example.physmin.views.GraphView
import com.example.physmin.views.spToPx

class ImageViewSettable(context: Context, attributeSet: AttributeSet?) : Settable(context, attributeSet) {

    var correctAnswers: IntArray? = null
    var graph = GraphView(context, null)
    private var paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private var staticLayout: StaticLayout? = null

    override fun isCorrect(): Boolean {
        if(answerView == null || correctAnswers == null)
            return false

        return correctAnswers!!.contains(answerView!!.answer)
    }

    init {
        paint.textSize = 14.spToPx()
    }

    override fun onAnswerChanged(answerView: Pickable?) {
        super.onAnswerChanged(answerView)

        invalidateStaticLayout()
        invalidate()
    }

    private fun invalidateStaticLayout() {
        if (width <= 0) // ??
            return

        var text = ""
        if(answerView != null)
            text = (answerView as TextViewPickable).text

        staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width - paddingRight - paddingLeft)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER).build()
        else
            StaticLayout(text, paint, width - paddingRight - paddingLeft, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        graph.draw(canvas)

        if(answerView == null)
            return

        staticLayout?.let {
        canvas.withTranslation(this.width / 2f - it.width / 2f, this.height / 2f - it.height / 2f) {
                it.draw(canvas)
            }
        }

    }
}