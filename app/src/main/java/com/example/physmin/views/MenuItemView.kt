package com.example.physmin.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import com.example.physmin.R
import kotlinx.android.synthetic.main.menuitem_popup.view.*

class MenuItemView(context: Context, attrs: AttributeSet?): View(context, attrs),
        View.OnClickListener
{

    private var _itemTitle: String? = null
    private var _itemBackIcon: Drawable? = null
    private var _itemIcon: Drawable? = null

    private var iconBackPaint: Paint? = null
    private lateinit var textPaint: TextPaint
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f

    private val iconWidth = 31
    private val iconHeight = 73
    private val iconBackWidth = 73
    private val iconBackHeight = 82

    lateinit var popupWindow: PopupWindow
    var popupLocation = intArrayOf(0, 0)

    var onTestButtonClick: (()->Unit)? = null
    var onLearnButtonClick: (()->Unit)? = null

    var itemTitle: String?
        get() = _itemTitle
        set(value) {
            _itemTitle = value
            invalidateTextPaintAndMeasurements()
        }
    var itemBack_icon: Drawable?
        get() = _itemBackIcon
        set(value) {
            _itemBackIcon = value
            invalidateTextPaintAndMeasurements()
        }
    var itemIcon: Drawable?
        get() = _itemIcon
        set(value) {
            _itemIcon = value
            invalidateTextPaintAndMeasurements()
        }

    init {
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.MenuItemView, 0, 0)

        _itemTitle = a.getString(R.styleable.MenuItemView_itemTitle)
        if (a.hasValue(R.styleable.MenuItemView_itemBack_icon)) {
            _itemBackIcon = a.getDrawable(R.styleable.MenuItemView_itemBack_icon)
            _itemBackIcon?.callback = this
        }
        if (a.hasValue(R.styleable.MenuItemView_itemIcon)) {
            _itemIcon = a.getDrawable(R.styleable.MenuItemView_itemIcon)
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
        setOnClickListener(this)
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

    override fun onClick(p0: View?) {
        if (::popupWindow.isInitialized) {
            popupWindow.showAsDropDown(this)
            return
        }
        val popupView = inflate(context, R.layout.menuitem_popup, null)
        popupView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
//        popupView.animation = AnimationUtils.loadAnimation(context, R.anim.popup_anim)
        popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.setBackgroundDrawable(ColorDrawable())
        popupWindow.isOutsideTouchable = true
        popupWindow.isTouchable = true
        popupView.menuitem_test.setOnClickListener {
            popupWindow.dismiss()
            onTestButtonClick?.invoke()
        }
        popupView.menuitem_learn.setOnClickListener {
            popupWindow.dismiss()
            onLearnButtonClick?.invoke()
        }

        this.getLocationOnScreen(popupLocation)

        onClick(p0)
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
            canvas.withTranslation(contentWidth / 2f, height - textHeight) {
                canvas.drawText(it, 0f, 0f, textPaint)
            }
        }

        itemBack_icon?.let {
            canvas.withTranslation(width / 2f, 0f) {
                it.setBounds(-Math.round((iconBackWidth/2).dpToPx()),0,
                        Math.round((iconBackWidth/2).dpToPx()), Math.round((iconBackHeight).dpToPx()))
                it.draw(canvas)
            }
        }
        itemBack_icon?.minimumHeight
        itemIcon?.let {
            canvas.withTranslation(width / 2f, 0f) {
                it.setBounds(-Math.round((iconWidth / 2).dpToPx()), Math.round((10).dpToPx()),
                        Math.round((iconWidth / 2).dpToPx()), Math.round((10 + iconHeight).dpToPx()))
                it.draw(canvas)
            }
        }

    }
}
