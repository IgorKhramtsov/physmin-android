package com.example.physmin.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import com.example.physmin.R

class MenuItemView: View {

    private var _itemTitle: String? = null
    private var _itemIcon: Drawable? = null

    private var iconBackPaint: Paint? = null
    private var textPaint: TextPaint? = null
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f

    var itemTitle: String?
        get() = _itemTitle
        set(value) {
            _itemTitle = value
            invalidateTextPaintAndMeasurements()
        }
    var itemIcon: Drawable?
        get() = _itemIcon
        set(value) {
            _itemIcon = value
            invalidateTextPaintAndMeasurements()
        }

    constructor(context: Context): super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.MenuItemView, defStyle, 0)

        _itemTitle = a.getString(
                R.styleable.MenuItemView_itemTitle)


        if (a.hasValue(R.styleable.MenuItemView_itemIcon)) {
            _itemIcon = a.getDrawable(
                    R.styleable.MenuItemView_itemIcon)
            _itemIcon?.callback = this
        }

        a.recycle()

        // Set up a default TextPaint object
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
        }
        iconBackPaint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
        }
        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {
        textPaint?.let {
            it.textSize = 20.spToPx()
            it.color = ResourcesCompat.getColor(resources, R.color.textColor, null)
            textWidth = it.measureText(itemTitle)
            textHeight = it.fontMetrics.bottom
        }
        iconBackPaint?.let {
            it.color = ResourcesCompat.getColor(resources, R.color.icon_back, null)
            it.style = Paint.Style.FILL
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        itemTitle?.let {
            canvas.withTranslation(contentWidth / 2f, contentHeight - textHeight) {
                canvas.drawText(it, 0f, 0f, textPaint)
            }
        }

        itemIcon?.let {
            canvas.withTranslation(contentWidth / 2f, 0f) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // TODO: check this out
                    canvas.drawOval(-(25.dpToPx()), 0f,
                            35.dpToPx(), 60.dpToPx(), iconBackPaint)
                }

                it.setBounds(-Math.round(10.dpToPx()), Math.round(12.dpToPx()),
                        Math.round(25.dpToPx()), Math.round(48.dpToPx()))
                it.draw(canvas)
            }
        }

    }
}
