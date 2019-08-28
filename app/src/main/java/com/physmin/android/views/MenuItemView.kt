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
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import com.physmin.android.R
import java.util.*
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

    private val actionList: MutableMap<String, () -> Unit> = mutableMapOf()
    var popupWindow: PopupWindow? = null
    var popupLocation = intArrayOf(0, 0)

    var isDisabled: Boolean = false
        set(value) {
            field = value
            if (value) {
                val greyFilter = PorterDuffColorFilter(ResourcesCompat.getColor(resources, R.color.disabled_filter, null), PorterDuff.Mode.MULTIPLY)
                this._itemBackIcon?.colorFilter = greyFilter
                this._itemIcon?.colorFilter = greyFilter
                this.textPaint.color = ResourcesCompat.getColor(resources, R.color.textColorDisabled, null)
            } else {
                this._itemBackIcon?.clearColorFilter()
                this._itemIcon?.clearColorFilter()
                this.textPaint.color = ResourcesCompat.getColor(resources, R.color.textColor, null)
            }
            invalidate()
        }

    var onTestButtonClick: (() -> Unit)? = null
    var onLearnButtonClick: (() -> Unit)? = null

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

    fun isHorizontal() = type == "Horizontal"

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MenuItemView, 0, 0)

        _itemTitle = a.getString(R.styleable.MenuItemView_itemTitle)
        if (a.hasValue(R.styleable.MenuItemView_type))
            type = a.getString(R.styleable.MenuItemView_type)!!
        if (a.hasValue(R.styleable.MenuItemView_itemBack_icon))
            _itemBackIcon = a.getDrawable(R.styleable.MenuItemView_itemBack_icon)?.also { it.callback = this }
        if (a.hasValue(R.styleable.MenuItemView_itemIcon))
            _itemIcon = a.getDrawable(R.styleable.MenuItemView_itemIcon)?.also { _itemIcon?.callback = this }

        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = if(isHorizontal()) Paint.Align.LEFT else Paint.Align.CENTER
        }
        isDisabled = a.getBoolean(R.styleable.MenuItemView_disabled, true)

        a.recycle()


        setOnClickListener(this)
        invalidateTextPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {
        textPaint.let {
            it.textSize = 20.spToPx()
            it.color = ResourcesCompat.getColor(resources, if (isDisabled) R.color.textColorDisabled else R.color.textColor, null)
            textWidth = it.measureText(itemTitle)
            textDescent = abs(it.fontMetrics.descent)
            textHeight = abs(it.fontMetrics.ascent) + textDescent
        }
    }

    private fun invalidateIconBack() {
        val availableWidth = this.width.toFloat()
        val availableHeight = if (isHorizontal())
            this.height.toFloat()
        else
            this.height - textHeight - 8.dpToPx()

        val constWidth = if (isHorizontal()) backHorWidthConst else backVertWidthConst
        val constHeight = if (isHorizontal()) backHorHeightConst else backVertHeightConst
        val constWidthFront = if (isHorizontal()) iconHorWidthConst else iconVertWidthConst
        val constHeightFront = if (isHorizontal()) iconHorHeightConst else iconVertHeightConst

        var theoreticalWidth = availableWidth
        var theoreticalHeight = constHeight.toFloat() / constWidth * theoreticalWidth

        if (theoreticalHeight > availableHeight) {
            theoreticalHeight = availableHeight
            theoreticalWidth = constWidth.toFloat() / constHeight * theoreticalHeight
        }

        iconBackWidth = theoreticalWidth
        iconBackHeight = theoreticalHeight
        iconWidth = theoreticalWidth * constWidthFront.toFloat() / constWidth
        iconHeight = theoreticalHeight * constHeightFront.toFloat() / constHeight
    }

    fun setAction(name: String, action: () -> Unit) {
        actionList[name] = action
        if(actionList.count() > 0)
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
            popupWindow?.showAsDropDown(this)
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
        for ((name,action) in actionList) {
            popupView.menuitem_actions.addView(TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).also {
                    it.topMargin = 16.dpToPx().roundToInt()
                    it.leftMargin = 20.dpToPx().roundToInt()
                    it.rightMargin = 20.dpToPx().roundToInt()
                    it.gravity = Gravity.CENTER_HORIZONTAL
                }
                text = name
                textSize = 24f
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
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        if (iconBackHeight == 0f || iconBackWidth == 0f) invalidateIconBack()

        if (isHorizontal()) {
            itemTitle?.let {
                canvas.drawText(it, iconBackWidth + 22.dpToPx(), (height / 2f) + textDescent, textPaint)
            }

            itemBackIcon?.let {
                canvas.withTranslation(0f, height / 2f) {
                    it.setBounds(0,
                            -(iconBackHeight/2).roundToInt(),
                            (iconBackWidth).roundToInt(),
                            (iconBackHeight/2).roundToInt())
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
        } else {
            itemTitle?.let {
                canvas.drawText(it, contentWidth / 2f, height - textDescent, textPaint)
            }

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
        }

    }
}
