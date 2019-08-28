package com.physmin.android.views.layouts

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.physmin.android.Pickable
import com.physmin.android.R
import com.physmin.android.Settable
import com.physmin.android.Singleton
import com.physmin.android.views.dpToPx
import com.physmin.android.views.generateShadowPanel
import kotlin.math.max

const val ONE_COLUMN = 1
const val TWO_COLUMNS = 2
const val ONE_TWO_COLUMNS = 3

open class BaseGroup(context: Context, attributeSet: AttributeSet? = null):
        ViewGroup(context, attributeSet) {

    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.ui_panel, null)
    private var _backShadowColor: Int = ResourcesCompat.getColor(resources, R.color.ui_shadow, null)

    var horizontalSpacing = 0
    var verticalSpacing = 0
    var blurRadius = 2.dpToPx()
    var cornerRadius = 2.dpToPx()
    var layoutType: Int = ONE_COLUMN

    private val backPanelBitmap: Bitmap? by Singleton {
        generateShadowPanel(width, height, cornerRadius, blurRadius, _backColor, _backShadowColor, this)
    }

    init {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.BaseGroup, 0, 0)
        horizontalSpacing = a.getDimensionPixelSize(R.styleable.BaseGroup_bg_spacingHorizontal, 0)
        verticalSpacing = a.getDimensionPixelSize(R.styleable.BaseGroup_bg_spacingVertical, 0)
        setLayoutType(a.getString(R.styleable.BaseGroup_bg_layoutType))
        a.recycle()

        post {
            setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparent, null))
        }
    }

    fun setLayoutType(type: String?) {
        layoutType = when (type) {
            "one_column" -> ONE_COLUMN
            "two_columns" -> TWO_COLUMNS
            "one_two_columns" -> ONE_TWO_COLUMNS
            else -> {
                Log.e("GroupSettable", "cant parse layout type: $type")
                ONE_COLUMN
            }
        }
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        if (this.childCount <= 0)
            return

        val count: Int = childCount
        var curWidth = 0
        var curHeight = 0
        var curLeft = 0
        var curTop: Int

        var maxWidth = this.getChildAt(0).measuredWidth
        var maxHeight = this.getChildAt(0).measuredHeight

        val contentLeft = this.paddingLeft
        val contentTop = this.paddingTop
        val contentRight = this.measuredWidth - this.paddingRight
        val contentBottom = this.measuredHeight - this.paddingBottom

        var childInRow = (contentRight - contentLeft) / maxWidth
        var childSpacing = ((contentRight - contentLeft) - childInRow * maxWidth) / (childInRow + 1)
        var curChildInRow = 1

        curLeft = this.measuredWidth / 2
        curTop = contentTop
        var maxHeightInRow = 0

        var child: View
        for (i in 0 until count) {
            child = getChildAt(i)

            if ((count % 2 != 0 && i == 0 && layoutType == ONE_TWO_COLUMNS) || layoutType == ONE_COLUMN) {
                curLeft -= child.measuredWidth / 2
                child.layout(curLeft, curTop, curLeft + child.measuredWidth, curTop + child.measuredHeight)
                curLeft += child.measuredWidth / 2
                curTop += child.measuredHeight + horizontalSpacing
                continue
            }

            if (curChildInRow % 2 == 1)
                curLeft -= (child.measuredWidth + verticalSpacing / 2)
            else
                curLeft += verticalSpacing / 2

            if(layoutType == ONE_TWO_COLUMNS) {
                maxHeightInRow = max(child.measuredHeight,
                        if ((i - 1) % 2 == 0) getChildAt((i - 1) + 1).measuredHeight else getChildAt((i - 1) - 1).measuredHeight)
            } else {
                maxHeightInRow = max(child.measuredHeight,
                        if (i % 2 == 0) getChildAt(i + 1).measuredHeight else getChildAt(i - 1).measuredHeight)
            }

            child.layout(curLeft, curTop, curLeft + child.measuredWidth, curTop + maxHeightInRow)
            curLeft = this.measuredWidth / 2
            if (curChildInRow % 2 == 0)
                curTop += maxHeightInRow + horizontalSpacing

            curChildInRow++
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val childCount = childCount
        var width = 0
        var height = 0
        var maxHeightRow = 0
        var maxRowWidth = 0
        var childAt: View
        for (i in 0 until childCount) {
            childAt = getChildAt(i)
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec)

            maxHeightRow = max(childAt.measuredHeight, maxHeightRow)
            maxRowWidth += childAt.measuredWidth + verticalSpacing
            when (layoutType) {
                ONE_COLUMN -> {
                    height += childAt.measuredHeight + horizontalSpacing
                    width = max(width, childAt.measuredWidth + verticalSpacing)
                }
                TWO_COLUMNS -> {
                    if (i % 2 == 0) { // first in row
                        height += maxHeightRow + horizontalSpacing
                        maxHeightRow = 0
                    }
                    else { // last in row
                        width = max(width, maxRowWidth)
                        maxRowWidth = 0
                    }
                }
                ONE_TWO_COLUMNS -> {
                    if ((i % 2 == 0 && i != 2) || i == 1) { // first in row
                        height += maxHeightRow + horizontalSpacing
                        maxHeightRow = 0
                    }
                    if(i % 2 == 0) { // last in row
                        width = max(width, maxRowWidth)
                        maxRowWidth = 0
                    }
                }
            }
        }

        height += paddingTop + paddingBottom - horizontalSpacing // remove spacing from last element
        width += paddingLeft + paddingRight - verticalSpacing
        height = View.resolveSizeAndState(height, heightMeasureSpec, 0)
        width = View.resolveSizeAndState(width, widthMeasureSpec, 0)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        backPanelBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }

}