package com.example.physmin.views.items

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.Pickable
import com.example.physmin.R
import com.example.physmin.Settable
import com.example.physmin.fragments.tests.toPx
import com.example.physmin.views.spToPx


class RelationSignView(context: Context, attributeSet: AttributeSet?, letter: String?, lIndex: String?, rIndex: String?): Settable(context, attributeSet), View.OnClickListener, View.OnTouchListener  {
    var popupWindow: PopupWindow? = null
    var padding: Int = 0
    var location = intArrayOf(0, 0)
    var correctAnswers: Int? = null

    private var _letter: String? = null
    private var _leftIndex: String? = null
    private var _rightIndex: String? = null
    private var _currentSign: String? = null

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
        if(answerView == null || correctAnswers == null)
            return false

        return answerView?.answer == correctAnswers
    }

    var letter: String?
        get() = _letter
        set(value) {
            _letter = value
            invalidateTextPaintAndMeasurements()
        }

    var leftIndex: String?
        get() = _leftIndex
        set(value) {
            _leftIndex = value
            invalidateTextPaintAndMeasurements()
        }

    var rightIndex: String?
        get() = _rightIndex
        set(value) {
            _rightIndex = value
            invalidateTextPaintAndMeasurements()
        }

    var currentSign: String?
        get() = _currentSign
        set(value) {
            _currentSign = value
            invalidateTextPaintAndMeasurements()
        }

    override fun onAnswerChanged(answerView: Pickable?) {
        super.onAnswerChanged(answerView)

        when {
            answerView?.answer == -1 -> currentSign = "<"
            answerView?.answer == 0 -> currentSign = "="
            answerView?.answer == 1 -> currentSign = ">"
            else -> currentSign = null
        }
    }


    init {
        if(attributeSet != null) {
            val a = context.obtainStyledAttributes(
                    attributeSet, R.styleable.RelationSignView, 0, 0)

            _letter = a.getString(R.styleable.RelationSignView_letter)
            _leftIndex = a.getString(R.styleable.RelationSignView_leftIndex)
            _rightIndex = a.getString(R.styleable.RelationSignView_rightIndex)

            a.recycle()
        }

        _letter = letter
        _leftIndex = lIndex
        _rightIndex = rIndex

        layoutParams = ViewGroup.LayoutParams(150.toPx(), 50.toPx())

        letterPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }
        indexPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }

        this.setOnClickListener(this)


        invalidateTextPaintAndMeasurements()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean { return false }

    // TODO: find correct color from theme
    private fun invalidateTextPaintAndMeasurements() {
        letterPaint?.let {
            it.textSize = 28.spToPx()
            it.color = ResourcesCompat.getColor(resources, R.color.textColor, null)
            letterWidth = it.measureText(letter)
            letterHeight = it.fontMetrics.descent
            if(currentSign != null)
                signWidth = it.measureText(currentSign)
        }
        indexPaint?.let {
            it.textSize = 12.spToPx()
            it.color = ResourcesCompat.getColor(resources, R.color.textColor, null)
            indexWidth = it.measureText(leftIndex)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)


    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        contentWidth = width
        contentHeight = height
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        letter?.let {
            canvas.drawText(it,
                    letterWidth,
                    (contentHeight / 2) + letterHeight,
                    letterPaint)
            canvas.drawText(it,
                    contentWidth - letterWidth - indexWidth*2 ,
                    (contentHeight / 2) + letterHeight,
                    letterPaint)
        }
        leftIndex?.let {
            canvas.drawText(it,
                    letterWidth * 2,
                    (contentHeight / 2) + letterHeight + indexHeight,
                    indexPaint)
        }
        rightIndex?.let {
            canvas.drawText(it,
                    contentWidth - (indexWidth*2),
                    (contentHeight / 2) + letterHeight + indexHeight,
                    indexPaint)
        }

        currentSign?.let {
            canvas.drawText(it,
                    (contentWidth / 2) - signWidth  + letterWidth / 2,
                    (contentHeight / 2) + letterHeight,
                    letterPaint)
        }

    }

    override fun onClick(view: View?) {
        if(popupWindow != null) {
            popupWindow!!.showAtLocation(this.parentTestConstraintLayout.groupPickable, 0,
                    location[0] + padding,
                    location[1] - Math.round(this.height * 0.7f))
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

//        val liste = OnTouchListener { choosed_view, event ->
//            if(event.action == MotionEvent.ACTION_UP) {
//                answerView = if (answerView == choosed_view) null else (choosed_view as TextViewPickable)
//
//                popupWindow!!.dismiss()
//                view?.invalidate()
//            }
//            return@OnTouchListener true
//        }
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

}
