package com.physmin.android.views.items

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
import com.physmin.android.Pickable
import com.physmin.android.R
import com.physmin.android.Settable
import com.physmin.android.Singleton
import com.physmin.android.fragments.tasks.toPx
import com.physmin.android.views.GraphView
import com.physmin.android.views.dpToPx
import com.physmin.android.views.generateShadowPanel
import com.physmin.android.views.spToPx
import kotlin.math.round


@SuppressLint("ViewConstructor")
class RelationSignView(context: Context, attributeSet: AttributeSet?, letter: String?, lIndexes: IntArray?, rIndexes: IntArray?): Settable(context, attributeSet), OnClickListener {
    private var isInitialized = false

    var correctAnswers: Int? = null
    var popupWindow: PopupWindow? = null
    var letterSize = 20.spToPx()
    var indexesSize = 10.spToPx()
    var popupLocation = intArrayOf(0, 0)
    var popupPadding: Int = 0
    var graphView: GraphView? = null

    private var letterPaint: TextPaint
    private var indexPaint: TextPaint

    private var letterWidth: Float = 0f
    private var letterHeight: Float = 0f
    private var indexWidth: Float = 0f
    private var indexHeight: Float = 0f
    private var signWidth: Float = 0f

    var contentWidth = width
    var contentHeight = height

    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.ui_panel, null)
    private var _backShadowColor: Int = ResourcesCompat.getColor(resources, R.color.ui_shadow, null)
    var blurRadius = 2.5f.dpToPx()
    var cornerRadius = 0f
    private val backPanelBitmap: Bitmap? by Singleton {
        generateShadowPanel(width, height, cornerRadius, blurRadius, 2.dpToPx(), 2.dpToPx(), _backColor, _backShadowColor, this)
    }

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

    var currentSign: String? = "?"
        set(value) {
            field = value
            invalidateTextPaintAndMeasurements()
        }

    init {
        this.letter = letter
        this.leftIndexes = lIndexes
        this.rightIndexes = rIndexes

        this.letter?.let {
            if(it.length == 1) {
                this.setPadding(10.toPx(), 0, 10.toPx(), 0)
                this.letter = it.toUpperCase()
            }
        }


        layoutParams = ViewGroup.LayoutParams(130.toPx(), 46.toPx())
        this.setPadding(15.toPx(), 0,15.toPx(), 0)

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
            else -> currentSign = "?"
        }
    }

    private fun invalidateTextPaintAndMeasurements() {
        if (!isInitialized)
            return

        letterPaint.let {
            it.textSize = letterSize
            it.color = ResourcesCompat.getColor(resources, R.color.textColor, null)
            it.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            letterWidth = it.measureText(letter)
            letterHeight = it.fontMetrics.descent
            if (currentSign != null)
                signWidth = it.measureText(currentSign)
        }
        indexPaint.let {
            it.textSize = indexesSize
            it.color = ResourcesCompat.getColor(resources, R.color.textColor, null)
            it.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            indexWidth = it.measureText(leftIndexes?.joinToString(","))
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
            popupWindow!!.showAtLocation(controller.pickableGroup, 0,
                    popupLocation[0] + popupPadding,
                    popupLocation[1] - round(this.height * 0.7f).toInt())
            val array = ArrayList<ArrayList<Int>>()
            array.add(arrayListOf(leftIndexes!![0], leftIndexes!![1]))
            array.add(arrayListOf(rightIndexes!![0], rightIndexes!![1]))
            graphView?.selectedArea = array
            return
        }

        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.pop_up_elements, null)

        popupView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        popupPadding = (this.width - popupView.measuredWidth) / 2

        popupWindow = PopupWindow(popupView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        popupWindow!!.setBackgroundDrawable(ColorDrawable())
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.isTouchable = true
        popupWindow!!.setOnDismissListener {
            graphView?.selectedArea = ArrayList()
        }

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

        this.getLocationOnScreen(popupLocation)

        onClick(view)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        backPanelBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

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
            canvas.drawText(it.joinToString(","),
                    paddingLeft + letterWidth,
                    (contentHeight / 2) + letterHeight + indexHeight,
                    indexPaint)
        }
        rightIndexes?.let {
            canvas.drawText(it.joinToString(","),
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
