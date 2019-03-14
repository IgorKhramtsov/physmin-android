package com.example.physmin.views

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import com.example.physmin.Pickable
import com.example.physmin.R
import com.example.physmin.Settable
import com.example.physmin.fragments.tests.toPx

fun Int.spToPx(): Float = this * Resources.getSystem().displayMetrics.scaledDensity
fun Int.dpToPx(): Float = this * Resources.getSystem().displayMetrics.density

class RelationSignView : View, View.OnClickListener, Settable {
    var popupWindow: PopupWindow? = null
    var padding: Int = 0
    var location = intArrayOf(0, 0)
    override var par: GroupSettable? = null
    var correctAnsw: Int? = null

    private var _letter: String? = null
    private var _leftIndex: String? = null
    private var _rightIndex: String? = null
    private var _currentSign: String? = null

    private var letterPaint: TextPaint? = null
    private var indexPaint: TextPaint? = null

    private var letterWidth: Float = 0f
    private var letterHeight: Float = 0f
    private var indexWidth: Float = 0f
    private var indexHeight: Float = 0f
    private var signWidth: Float = 0f

    var contentWidth = width
    var contentHeight = height

    override fun isCorrect(): Boolean {
        if(answerView == null || correctAnsw == null)
            return false

        return answerView?.answer == correctAnsw
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

    override var answerView: Pickable? = null
        set(value) {
            field = value
            when {
                answerView?.answer == -1 -> currentSign = "<"
                answerView?.answer == 0 -> currentSign = "="
                answerView?.answer == 1 -> currentSign = ">"
                else -> currentSign = null
            }
            par!!.par!!.checkTestComplete(par!!.isAllChecked())
        }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        if(attrs != null) {
            val a = context.obtainStyledAttributes(
                    attrs, R.styleable.RelationSignView, defStyle, 0)

            _letter = a.getString(R.styleable.RelationSignView_letter)
            _leftIndex = a.getString(R.styleable.RelationSignView_leftIndex)
            _rightIndex = a.getString(R.styleable.RelationSignView_rightIndex)

            a.recycle()
        }

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
    constructor(context: Context, letter: String, lIndex: String, rIndex: String) : super(context) {
        _letter = letter
        _leftIndex = lIndex
        _rightIndex = rIndex

        init(null, 0)
    }

    // TODO: find correct color from theme
    private fun invalidateTextPaintAndMeasurements() {
        letterPaint?.let {
            it.textSize = 28.spToPx()
            it.color = resources.getColor(android.R.color.primary_text_light)
            letterWidth = it.measureText(letter)
            letterHeight = it.fontMetrics.descent
            if(currentSign != null)
                signWidth = it.measureText(currentSign)
        }
        indexPaint?.let {
            it.textSize = 12.spToPx()
            it.color = resources.getColor(android.R.color.primary_text_light)
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
            popupWindow!!.showAtLocation(view, 0,
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

        val listener = OnClickListener { choosed_view ->
            answerView = if (answerView == choosed_view) null else (choosed_view as TextViewPickable)

            popupWindow!!.dismiss()
            view?.invalidate()
        }

        popupView.findViewById<TextViewPickable>(R.id.textViewPickable_less).setOnClickListener(listener)
        popupView.findViewById<TextViewPickable>(R.id.textViewPickable_equal).setOnClickListener(listener)
        popupView.findViewById<TextViewPickable>(R.id.textViewPickable_more).setOnClickListener(listener)

        this.getLocationOnScreen(location)

        onClick(view)
    }

    override fun setParent(_parent : GroupSettable) {
        this.par = _parent
    }
}
