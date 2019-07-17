package com.example.physmin.views.items

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import com.example.physmin.Pickable
import com.example.physmin.R
import com.example.physmin.Settable
import com.example.physmin.fragments.tests.toPx
import com.example.physmin.views.spToPx
import kotlin.math.round


class RelationSignView(context: Context, attributeSet: AttributeSet?, letter: String?, lIndexes: IntArray?, rIndexes: IntArray?): Settable(context, attributeSet), OnClickListener {
    private var isInitialized = false

    var popupWindow: PopupWindow? = null
    var padding: Int = 0
    var textSize = 24.spToPx()
    var textIndexSize = 12.spToPx()
    var location = intArrayOf(0, 0)
    var correctAnswers: Int? = null

    private var letterPaint: TextPaint
    private var indexPaint: TextPaint

    private var letterWidth: Float = 0f
    private var letterHeight: Float = 0f
    private var indexWidth: Float = 0f
    private var indexHeight: Float = 0f
    private var signWidth: Float = 0f

    var contentWidth = width
    var contentHeight = height

    override fun isCorrect(): Boolean {
        if (answerView == null || correctAnswers == null)
            return false

        return answerView?.answer == correctAnswers
    }

    var letter: String? = null
        set(value) {
            field = value
            invalidateTextPaintAndMeasurements()
        }

    var leftIndexes: IntArray? = null
        set(value) {
            field = value
            invalidateTextPaintAndMeasurements()
        }

    var rightIndexes: IntArray? = null
        set(value) {
            field = value
            invalidateTextPaintAndMeasurements()
        }

    var currentSign: String? = null
        set(value) {
            field = value
            invalidateTextPaintAndMeasurements()
        }

    init {
        this.letter = letter
        this.leftIndexes = lIndexes
        this.rightIndexes = rIndexes

        this.letter?.let {
            if(it == "S")
                this.setPadding(10.toPx(), 0, 10.toPx(), 0)
            else if(it.length == 1) // not delta
                this.letter = it.toUpperCase()
        }


        layoutParams = ViewGroup.LayoutParams(120.toPx(), 50.toPx())

        letterPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }
        indexPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }

        this.setOnClickListener(this)

        isInitialized = true
        invalidateTextPaintAndMeasurements()
    }

    override fun onAnswerChanged(answerView: Pickable?) {
        super.onAnswerChanged(answerView)

        when {
            answerView?.answer == -1 -> currentSign = context.getString(R.string.relation_sign_less)
            answerView?.answer == 0 -> currentSign = context.getString(R.string.relation_sign_equal)
            answerView?.answer == 1 -> currentSign = context.getString(R.string.relation_sign_more)
            else -> currentSign = null
        }
    }

    private fun invalidateTextPaintAndMeasurements() {
        if (!isInitialized)
            return

        letterPaint.let {
            it.textSize = textSize
            it.color = ResourcesCompat.getColor(resources, R.color.textColor, null)
            it.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            letterWidth = it.measureText(letter)
            letterHeight = it.fontMetrics.descent
            if (currentSign != null)
                signWidth = it.measureText(currentSign)
        }
        indexPaint.let {
            it.textSize = textIndexSize
            it.color = ResourcesCompat.getColor(resources, R.color.textColor, null)
            it.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            indexWidth = it.measureText(leftIndexes?.joinToString(""))
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        contentWidth = width
        contentHeight = height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onClick(view: View?) {
        if (popupWindow != null) {
            popupWindow!!.showAtLocation(this.parentTestConstraintLayout.groupPickable, 0,
                    location[0] + padding,
                    location[1] - round(this.height * 0.7f).toInt())
            return
        }

        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.pop_up_elements, null)

        popupView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        padding = (this.width - popupView.measuredWidth) / 2

        popupWindow = PopupWindow(popupView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        popupWindow!!.setBackgroundDrawable(ColorDrawable())
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.isTouchable = true

        val listener = OnClickListener { choosed_view ->
            answerView = if (answerView == choosed_view) null else (choosed_view as TextViewPickable)

            popupWindow!!.dismiss()
            view?.invalidate()
        }

        popupView.findViewById<TextViewPickable>(R.id.textViewPickable_less).setDraggable(false)
        popupView.findViewById<TextViewPickable>(R.id.textViewPickable_equal).setDraggable(false)
        popupView.findViewById<TextViewPickable>(R.id.textViewPickable_more).setDraggable(false)
        popupView.findViewById<TextViewPickable>(R.id.textViewPickable_less).setOnClickListener(listener)
        popupView.findViewById<TextViewPickable>(R.id.textViewPickable_equal).setOnClickListener(listener)
        popupView.findViewById<TextViewPickable>(R.id.textViewPickable_more).setOnClickListener(listener)

        this.getLocationOnScreen(location)

        onClick(view)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        letter?.let {
            canvas.drawText(it,
                    paddingLeft.toFloat(),
                    (contentHeight / 2) + letterHeight,
                    letterPaint)
            canvas.drawText(it,
                    contentWidth - paddingRight - letterWidth - indexWidth,
                    (contentHeight / 2) + letterHeight,
                    letterPaint)
        }
        leftIndexes?.let {
            canvas.drawText(it.joinToString(""),
                    paddingLeft + letterWidth,
                    (contentHeight / 2) + letterHeight + indexHeight,
                    indexPaint)
        }
        rightIndexes?.let {
            canvas.drawText(it.joinToString(""),
                    contentWidth - paddingRight - indexWidth,
                    (contentHeight / 2) + letterHeight + indexHeight,
                    indexPaint)
        }
        currentSign?.let {
            canvas.drawText(it,
                    ((contentWidth - paddingRight + paddingLeft) / 2) - signWidth / 2,
                    (contentHeight / 2) + letterHeight,
                    letterPaint)
        }

    }

}
