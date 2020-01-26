package com.physmin.android.views.items

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import androidx.core.graphics.withTranslation
import com.physmin.android.Pickable
import com.physmin.android.Settable
import com.physmin.android.views.GraphView
import com.physmin.android.views.spToPx

class ImageViewSettable(context: Context, attributeSet: AttributeSet?) : Settable(context, attributeSet) {

    var correctAnswers: IntArray? = null
    var graph = GraphView(context, null)
    private var paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private var staticLayout: StaticLayout? = null

    private var answerTextSize = 14.spToPx()

    override fun isCorrect(): Boolean {
        if(answerView == null || correctAnswers == null)
            return false

        return correctAnswers!!.contains(answerView!!.answer)
    }

    init {
        paint.textSize = answerTextSize
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = resolveSizeAndState(height, heightMeasureSpec, 0)
        val width = resolveSizeAndState(width, widthMeasureSpec, 0)
        graph.measure(widthMeasureSpec, heightMeasureSpec)

        // TODO: idk, is it needed here
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        graph.runAnimation(this)
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