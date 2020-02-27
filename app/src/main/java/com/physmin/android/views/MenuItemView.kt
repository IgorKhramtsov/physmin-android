package com.physmin.android.views

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.withTranslation
import kotlinx.android.synthetic.main.menuitem_popup.view.*
import kotlin.math.roundToInt
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.physmin.android.R
import com.physmin.android.fragments.tasks.toPx
import java.lang.Error
import kotlin.math.abs


// Make actionList with names, and in "Закрепление" make only one action, so popup will not appear
class MenuItemView(context: Context, attrs: AttributeSet?): View(context, attrs),
        View.OnClickListener {

    private var _itemTitle: String? = null
    private var _itemBackIcon: Drawable? = null
    private var _itemIcon: Drawable? = null
    private var type: String = "Vertical"

    private var textPaint: TextPaint
    private var textWidth: Float = 0f
    private var textDescent: Float = 0f
    private var textHeight: Float = 0f

    private val iconVertWidthConst = 31
    private val iconVertHeightConst = 73
    private val iconHorWidthConst = 1 // doesnt used
    private val iconHorHeightConst = 1 // doesnt used

    private val backVertWidthConst = 73
    private val backVertHeightConst = 82
    private val backHorWidthConst = 53
    private val backHorHeightConst = 52

    private var iconBackWidth = 0f
    private var iconBackHeight = 0f
    private var iconWidth = 0f
    private var iconHeight = 0f

    private var completingPercent = 0f
    private val disabledAlpha = 68
    private var progressBackPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private var progressFrontPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private var progressFinishedDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(ResourcesCompat.getColor(resources, R.color.colorPrimary, null),
                    ResourcesCompat.getColor(resources, R.color.colorAccent, null)))

    private val actionList: MutableMap<String, () -> Unit> = mutableMapOf()
    var popupWindow: PopupWindow? = null
    var popupLocation = intArrayOf(0, 0)

    private var isDisabled: Boolean = true
        set(value) {
            field = value
            if (value) {
                itemBackIcon?.alpha = disabledAlpha
                itemIcon?.alpha = disabledAlpha
                progressBackPaint.alpha = disabledAlpha
                progressFrontPaint.alpha = disabledAlpha
                textPaint.color = ResourcesCompat.getColor(resources, R.color.textColorDisabled, null)
            } else {
                itemBackIcon?.alpha = 255
                itemIcon?.alpha = 255
                progressBackPaint.alpha = 255
                progressFrontPaint.alpha = 255
                textPaint.color = ResourcesCompat.getColor(resources, R.color.textColor, null)
            }
            invalidate()
        }

    var itemTitle: String?
        get() = _itemTitle
        set(value) {
            _itemTitle = value
            invalidateTextPaintAndMeasurements()
        }
    var itemBackIcon: Drawable?
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

    private fun isHorizontal() = type == "Horizontal"

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MenuItemView, 0, 0)

        _itemTitle = a.getString(R.styleable.MenuItemView_itemTitle)
        if (a.hasValue(R.styleable.MenuItemView_type))
            type = a.getString(R.styleable.MenuItemView_type)!!
        if (a.hasValue(R.styleable.MenuItemView_itemBack_icon))
            _itemBackIcon = a.getDrawable(R.styleable.MenuItemView_itemBack_icon)?.also {
                it.mutate()
                it.callback = this
            }

        if (a.hasValue(R.styleable.MenuItemView_itemIcon))
            _itemIcon = a.getDrawable(R.styleable.MenuItemView_itemIcon)?.also {
                it.mutate()
                it.callback = this
            }

        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = if (isHorizontal()) Paint.Align.LEFT else Paint.Align.CENTER
        }
        progressFrontPaint.apply {
            color = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 4.dpToPx()
        }
        progressBackPaint.apply {
            color = ResourcesCompat.getColor(resources, R.color.progressBack, null)
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 4.dpToPx()
        }
        progressFinishedDrawable.apply {
            shape = GradientDrawable.RECTANGLE
            gradientType = GradientDrawable.LINEAR_GRADIENT
            cornerRadius = 4.dpToPx()
        }

        isDisabled = a.getBoolean(R.styleable.MenuItemView_disabled, true)

        a.recycle()


        setOnClickListener(this)
        invalidateTextPaintAndMeasurements()
    }

    /// percent in 0..1
    /// if percent in [0..1) - user should receive exercise bundle
    /// if percent == 1 - user should receive exam bundle
    /// if percent > 1 - user complete topic
    fun setComplenteessPercent(percent: Float) {
        if (percent < 0)
            throw Error("completeness percent < 0")
        this.completingPercent = percent
        invalidate()
    }

    private fun invalidateTextPaintAndMeasurements() {
        textPaint.let {
            it.textSize = 18.spToPx()
            it.color = ResourcesCompat.getColor(resources, if (isDisabled) R.color.textColorDisabled else R.color.textColor, null)
            textWidth = it.measureText(itemTitle)
            textDescent = abs(it.fontMetrics.descent)
            textHeight = abs(it.fontMetrics.ascent) + textDescent
        }
    }

    private fun invalidateIconBack() {
        val constWidth = if (isHorizontal()) backHorWidthConst else backVertWidthConst
        val constHeight = if (isHorizontal()) backHorHeightConst else backVertHeightConst
        val constWidthFront = if (isHorizontal()) iconHorWidthConst else iconVertWidthConst
        val constHeightFront = if (isHorizontal()) iconHorHeightConst else iconVertHeightConst

        iconBackWidth = constWidth.dpToPx()
        iconBackHeight = constHeight.dpToPx()
        iconWidth = constWidthFront.dpToPx()
        iconHeight = constHeightFront.dpToPx()
    }

    fun setAction(name: String, action: () -> Unit) {
        actionList[name] = action
        if (actionList.count() > 0)
            this.isDisabled = false
        popupWindow = null
    }

    fun deleteActions() {
        actionList.clear()
        isDisabled = true
        popupWindow = null
    }

    override fun onClick(p0: View?) {
        if (isDisabled)
            return

        if (popupWindow != null) {
            popupWindow?.showAsDropDown(this, -14.dpToPx().toInt(), -24.dpToPx().toInt())
            return
        }
        val popupView = inflate(context, R.layout.menuitem_popup, null) as ViewGroup
        popupView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setBackgroundDrawable(ColorDrawable())
            isOutsideTouchable = true
            isTouchable = true
        }

        popupView.menuitem_actions.removeAllViews()
        for ((name, action) in actionList) {
            popupView.menuitem_actions.addView(CustomButton(context, null).apply {
                layoutParams = LinearLayout.LayoutParams(150.dpToPx().toInt(), 37.dpToPx().toInt()).also {
                    it.gravity = Gravity.CENTER_HORIZONTAL
                    it.bottomMargin = 8.dpToPx().toInt()
                }
                text = name
                textSize = 15f
                setOnClickListener {
                    popupWindow?.dismiss()
                    action.invoke()
                }
            })
        }

        this.getLocationOnScreen(popupLocation)

        onClick(p0)
    }

    override fun onDraw(canvas: Canvas) {
        if (iconBackHeight == 0f || iconBackWidth == 0f) invalidateIconBack()

        if (isHorizontal()) {
            itemBackIcon?.let {
                canvas.withTranslation(0f, height / 2f) {
                    it.setBounds(0,
                            -(iconBackHeight / 2).roundToInt(),
                            (iconBackWidth).roundToInt(),
                            (iconBackHeight / 2).roundToInt())
                    it.draw(canvas)
                }
            }

            itemIcon?.let {
                canvas.withTranslation(0f, height / 2f) {
                    it.setBounds(-(iconWidth / 2).roundToInt(),
                            (10).dpToPx().roundToInt(),
                            (iconWidth / 2).roundToInt(),
                            (10 + iconHeight).roundToInt())
                    it.draw(canvas)
                }
            }

            itemTitle?.let {
                canvas.drawText(it, iconBackWidth + 22.dpToPx(), (height / 2f) + textDescent, textPaint)
            }
        } else {
            itemBackIcon?.let {
                canvas.withTranslation(width / 2f, 0f) {
                    it.setBounds(-(iconBackWidth / 2).roundToInt(),
                            0,
                            (iconBackWidth / 2).roundToInt(),
                            (iconBackHeight).roundToInt())
                    it.draw(canvas)
                }
            }
            itemIcon?.let {
                canvas.withTranslation(width / 2f, 0f) {
                    it.setBounds(-(iconWidth / 2).roundToInt(),
                            (10).dpToPx().roundToInt(),
                            (iconWidth / 2).roundToInt(),
                            (10 + iconHeight).roundToInt())
                    it.draw(canvas)
                }
            }

            canvas.withTranslation(width / 2f, 0f) {
                if (completingPercent > 1) {
                    progressFinishedDrawable.let {
                        it.setBounds(-(iconBackWidth / 2).roundToInt(),
                                iconBackHeight.roundToInt() + 7.toPx(),
                                (iconBackWidth / 2).roundToInt(),
                                iconBackHeight.roundToInt() + 7.toPx() + 4.toPx())
                        it.draw(canvas)
                    }
                } else {
                    canvas.drawLine(-(iconBackWidth / 2), iconBackHeight + 7.dpToPx(), (iconBackWidth / 2), iconBackHeight + 7.dpToPx(), progressBackPaint)
                    canvas.drawLine(-(iconBackWidth / 2), iconBackHeight + 7.dpToPx(), -(iconBackWidth / 2) + (iconBackWidth * (completingPercent)), iconBackHeight + 7.dpToPx(), progressFrontPaint)
                }
            }

            itemTitle?.let {
                canvas.drawText(it, width / 2f, height - textDescent, textPaint)
            }
        }

    }
}
