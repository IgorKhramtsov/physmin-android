package com.example.physmin.views.Layouts

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.R
import com.example.physmin.Settable
import com.example.physmin.activities.FunctionParcelable
import com.example.physmin.views.*
import com.example.physmin.views.Items.ImageViewSettableBlank

class GroupSettable(context: Context, attributeSet: AttributeSet?): ViewGroup(context, attributeSet),
        ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    lateinit var parentTestConstraintLayout: TestConstraintLayout

    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.ui_panel, null)
    private var _backShadowColor: Int = ResourcesCompat.getColor(resources, R.color.ui_shadow, null)

    var horizontalSpacing = 0
    var verticalSpacing = 0
    var blurRadius = 2.dpToPx()
    var cornerRadius = 2.dpToPx()
    var backPanelBitmap: Bitmap? = null

    private var _deviceWidth: Int = 0

    init {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.GroupSettable, 0, 0)
        horizontalSpacing = a.getDimensionPixelSize(R.styleable.GroupSettable_gs_spacingHorizontal, 0)
        verticalSpacing = a.getDimensionPixelSize(R.styleable.GroupSettable_gs_spacingVertical, 0)
        a.recycle()

        val display: Display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val displaySize = Point()
        display.getSize(displaySize)
        _deviceWidth = displaySize.x

        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparent, null))

        this.post {
            backPanelBitmap = generateShadowPanel(width + 10, height, cornerRadius, blurRadius, _backColor, _backShadowColor, this)
        }

        setOnHierarchyChangeListener(this)
    }

    fun addQuestionGraphic(functions: ArrayList<FunctionParcelable>) {
        val graphView = GraphView(context, null)
        graphView.functions = functions
        graphView.layoutParams = LayoutParams(140.dpToPx().toInt(), 85.dpToPx().toInt())

        this.addView(graphView)
    }

    fun addQuestionBlankView(correctAnswers: IntArray) {
        val imageViewSettableBlank = ImageViewSettableBlank(context, null)
        imageViewSettableBlank.correctAnswers = correctAnswers
        imageViewSettableBlank.layoutParams = LayoutParams(140.dpToPx().toInt(), 85.dpToPx().toInt())

        this.addView(imageViewSettableBlank)
    }

    fun setParent(parent: TestConstraintLayout) {
        this.parentTestConstraintLayout = parent
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

        val contentLeft = this.paddingLeft
        val contentTop = this.paddingTop
        val contentRight = this.measuredWidth - this.paddingRight
        val contentBottom = this.measuredHeight - this.paddingBottom

        var childInRow = (contentRight - contentLeft) / maxWidth
        var childSpacing = ((contentRight - contentLeft) - childInRow * maxWidth) / (childInRow + 1)
        var curChildInRow = 1

        curLeft = _width / 2
        curTop = contentTop
        var maxHeightInRow = 0

        var child: View
        for (i in 0 until count) {
            child = getChildAt(i)

            if (count % 2 != 0) {
                if (i == 0) {
                    curLeft -= child.measuredWidth / 2
                    child.layout(curLeft, curTop, curLeft + child.measuredWidth, curTop + child.measuredHeight)
                    curLeft += child.measuredWidth / 2
                    curTop += child.measuredHeight + horizontalSpacing
//                    curChildInRow++
                    continue
                }
            }

            if (curChildInRow % 2 == 1)
                curLeft -= (child.measuredWidth + verticalSpacing / 2)
            else
                curLeft += verticalSpacing / 2

            maxHeightInRow = Math.max(maxHeightInRow, child.measuredHeight)
            child.layout(curLeft, curTop, curLeft + child.measuredWidth, curTop + maxHeightInRow)
            curLeft = _width / 2
            if (curChildInRow % 2 == 0) {
                curTop += maxHeightInRow + horizontalSpacing
                maxHeightInRow = 0
            }

            curChildInRow++
        }
    }

    override fun onChildViewAdded(parent: View?, child: View) {
        when (child) {
            is Settable -> {
                child.setParent(this)
                child.setOnClickListener(this)
            }
        }

        (child as? RelationSignView)?.setParent(this) // ??
    }

    override fun onChildViewRemoved(parent: View?, child: View?) {}

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
        width = Math.round(_deviceWidth * 1.1f)

        setMeasuredDimension(width, height)
    }

    override fun onClick(clickedChild: View?) {
        if (clickedChild !is Settable) return

        clickedChild.answerView = parentTestConstraintLayout.takePickedItem()
    }

    fun isAllChecked(): Boolean {
        var child: View
        for (i in 0 until this.childCount) {
            child = getChildAt(i)
            if (child is Settable)
                if (child.answerView == null)
                    return false
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