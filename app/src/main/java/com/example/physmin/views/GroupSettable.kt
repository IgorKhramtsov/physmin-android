package com.example.physmin.views

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.R
import com.example.physmin.Settable

class GroupSettable(context: Context, attributeSet: AttributeSet?): ViewGroup(context, attributeSet), ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    var deviceWidth: Int = 0
    var par: TestConstraintLayout? = null

    var horizontalSpacing = 0
    var verticalSpacing = 0

    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.ui_panel, null)
    private var _backShadowColor: Int = ResourcesCompat.getColor(resources, R.color.ui_shadow, null)
    var blurRadius = 2.dpToPx()
    var cornerRadius = 2.dpToPx()

    //    var backPaint: Paint? = null
//    var shadowPaint: Paint? = null
    var backPanelBitmap: Bitmap? = null

    val inRowSpacing = 8.dpToPx().toInt()
//    val secondRowMarging = 16.dpToPx().toInt()

    init {
        val a = context.obtainStyledAttributes(
                attributeSet, R.styleable.GroupSettable, 0, 0)

        horizontalSpacing = a.getDimensionPixelSize(R.styleable.GroupSettable_gs_spacingHorizontal, 0)
        verticalSpacing = a.getDimensionPixelSize(R.styleable.GroupSettable_gs_spacingVertical, 0)

        a.recycle()

        val display: Display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val displaySize = Point()

        display.getSize(displaySize)
        deviceWidth = displaySize.x

        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparent, null))
        setOnHierarchyChangeListener(this)

        this.post { backPanelBitmap = generateBackPanel(width + 10, height, cornerRadius, blurRadius, _backColor, _backShadowColor, this) }
    }

    public fun setParent(_par: TestConstraintLayout?) {
        this.par = _par
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, _width: Int, p4: Int) {
        if (this.childCount <= 0)
            return

        val count: Int = childCount
        var curWidth = 0
        var curHeight = 0
        var curLeft = 0
        var curTop: Int

        var maxWidth = this.getChildAt(0).measuredWidth
        var maxHeight = this.getChildAt(0).measuredHeight

        val wdh = _width
        val hgh = this.measuredHeight
        val contentLeft = this.paddingLeft
        val contentTop = this.paddingTop
        val contentRight = this.measuredWidth - this.paddingRight
        val contentBottom = this.measuredHeight - this.paddingBottom
        val childWidth = if (count > 0) getChildAt(0).layoutParams.width else contentRight - contentLeft
        val childHeight = if (count > 0) getChildAt(0).layoutParams.height else contentBottom - contentTop

        var childInRow = (contentRight - contentLeft) / maxWidth
        var childSpacing = ((contentRight - contentLeft) - childInRow * maxWidth) / (childInRow + 1)
        var curChildInRow = 1

        curLeft = wdh / 2
        curTop = contentTop

        var child: View
        for (i in 0 until count) {
            child = getChildAt(i)

            if (count % 2 != 0)
                if (i == 0) {
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

            child.layout(curLeft, curTop, curLeft + child.measuredWidth, curTop + child.measuredHeight)
            curLeft = wdh / 2
            if (curChildInRow % 2 == 0)
                curTop += child.measuredHeight + horizontalSpacing

            curChildInRow++
        }
    }

    override fun onChildViewAdded(parent: View?, child: View) {
        (child as? ImageViewSettable)?.setParent(this)
        (child as? ImageViewSettable)?.setOnClickListener(this)

        (child as? ImageViewSettableBlank)?.setParent(this)
        (child as? ImageViewSettableBlank)?.setOnClickListener(this)

        (child as? RelationSignView)?.setParent(this)

    }

    override fun onChildViewRemoved(parent: View?, child: View?) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val childCount = childCount
        var width = 0
        var height = 0
        for (i in 0 until childCount) {
            val childAt = getChildAt(i)
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec)

            width += childAt.measuredWidth + verticalSpacing / 2
            if (height < childAt.measuredHeight)
                height = childAt.measuredHeight
        }
        if (childCount > 2)
            height += getChildAt(childCount - 1).measuredHeight + horizontalSpacing // childHeight * 2 + paddings

        height += paddingTop + paddingBottom

        height = View.resolveSizeAndState(height, heightMeasureSpec, 0)
//        width = View.resolveSizeAndState(width, widthMeasureSpec, 0)
        width = Math.round(deviceWidth * 1.1f)

        setMeasuredDimension(width, height)
    }

    override fun onClick(_view: View?) {
        if (_view is ImageViewSettable) {
            val imageView = _view as ImageViewSettable
            (imageView.answerView as View?)?.visibility = View.VISIBLE

            imageView.answerView = par?.getPickedItem()
            (par?.getPickedItem() as View?)?.visibility = View.GONE
            imageView.invalidate()

            var child: View?
            for (i in 0 until childCount) {
                child = getChildAt(i)
                if (child !is ImageViewSettable)
                    continue

                if (child != imageView)
                    if (child.answerView == imageView.answerView) {
                        child.answerView = null
                        child.invalidate()
                    }
            }
            par?.resetPickedItem()
        } else if (_view is ImageViewSettableBlank) {
            val imageView = _view as ImageViewSettableBlank
            (imageView.answerView as View?)?.visibility = View.VISIBLE

            imageView.answerView = par?.getPickedItem()
            (par?.getPickedItem() as View?)?.visibility = View.GONE
            imageView.invalidate()

            var child: View?
            for (i in 0 until childCount) {
                child = getChildAt(i)
                if (child !is ImageViewSettable)
                    continue

                if (child != imageView)
                    if (child.answerView == imageView.answerView) {
                        child.answerView = null
                        child.invalidate()
                    }
            }
            par?.resetPickedItem()
        }
    }

    fun isAllChecked(): Boolean {
        val childCount = this.childCount
        var child: View
        for (i in 0 until childCount) {
            child = getChildAt(i)
            if (child is Settable)
                if (child.answerView == null) return false
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        backPanelBitmap?.let {
            canvas.drawBitmap(it, -5f, 0f, null)
        }
    }
}