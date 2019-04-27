package com.example.physmin.views

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.Pickable
import com.example.physmin.R

class GroupPickable(context: Context, attrs: AttributeSet?) : GroupScrollable(context, attrs),
        ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    var deviceWidth: Int = 0
    var pickedItem: Pickable? = null
    var par: TestConstraintLayout? = null

    var horizontalSpacing = 0
    var verticalSpacing = 0

    private var _backColor: Int = ResourcesCompat.getColor(resources, R.color.ui_panel, null)
    private var _backShadowColor: Int = ResourcesCompat.getColor(resources, R.color.ui_shadow, null)
    var blurRadius = 2.dpToPx()
    var cornerRadius = 2.dpToPx()

    var backPanelBitmap: Bitmap? = null

    init {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.GroupPickable, 0, 0)

        horizontalSpacing = a.getDimensionPixelSize(R.styleable.GroupPickable_gp_spacingHorizontal, 0)
        verticalSpacing = a.getDimensionPixelSize(R.styleable.GroupPickable_gp_spacingVertical, 0)

        a.recycle()

        val display: Display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val displaySize = Point()
        display.getSize(displaySize)
        deviceWidth = displaySize.x

        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparent, null))

        this.post {
            backPanelBitmap = generateShadowPanel(width, height,
                    cornerRadius, blurRadius, _backColor, _backShadowColor, this)
        }


        setOnHierarchyChangeListener(this)
    }

    public fun setParent(_par: TestConstraintLayout?) {
        this.par = _par
    }

    public fun pick(item: Pickable?) {
        val childs: Int = childCount
        pickedItem = item

        var child: View
        for(i in 0 until childs) {
            child = getChildAt(i)
            if(child !is Pickable)
                continue

            if(child == item) {
                child.pick()
            }
            else
                child.unPick()
        }
    }

    override fun onClick(p0: View?) {
        if(p0 is Pickable) {
            if (p0.picked) pick(null) else pick(p0)
        }
    }

    override fun onChildViewAdded(parent: View?, child: View) {
        child.setOnClickListener(this)
        (child as? TextViewPickable)?.setParent(this)
        (child as? ImageViewPickable)?.setParent(this)

    }

    override fun onChildViewRemoved(parent: View?, child: View?) {

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if(this.childCount <= 0)
            return

        val count: Int = childCount
        var curWidth = 0
        var curHeight = 0
        var center = 0
        var curTop = 20

        var childWidth = this.getChildAt(0).measuredWidth

        val contentLeft = this.paddingLeft
        val contentTop = this.paddingTop + blurRadius.toInt()
        val contentRight = this.measuredWidth - this.paddingRight
        val contentBottom = this.measuredHeight - this.paddingBottom


//        var childInRow = Math.floor((contentRight - contentLeft)*1.0/maxWidth*1.0).toInt()
        val childInRow = (contentRight - contentLeft) / (childWidth + verticalSpacing / 2)
//        var childSpacing = ((contentRight - contentLeft)-childInRow*maxWidth)/(childInRow+1)
        var curChildInRow = 0

        center = width / 2 //- childSpacing / 2
        curTop = contentTop
        var centerOffset = 0

        var curLeft = 0
        var curRight = 0
        var maxHeightInRow = 0

        for (i in 0 until count) {
            val child = getChildAt(i)

            curChildInRow++
            if(curChildInRow > childInRow) {
                curChildInRow = 1
                curTop += maxHeightInRow + horizontalSpacing
                maxHeightInRow = 0
            }
            maxHeightInRow = Math.max(maxHeightInRow, child.measuredHeight)
            curLeft = if (curChildInRow == 1)
                center - child.measuredWidth - verticalSpacing / 2
            else
                center + verticalSpacing / 2

            child.layout(curLeft, curTop, curLeft + child.measuredWidth, curTop + maxHeightInRow)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount
        var height = 0
        var width = 0
        var itemsInRowWidth = 0

        for (i in 0 until count) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
//            if (child.visibility == View.GONE)
//                continue

            itemsInRowWidth += child.measuredWidth + verticalSpacing / 2
            if(itemsInRowWidth > deviceWidth) {
                width = Math.max(width, itemsInRowWidth)
                height += child.measuredHeight + horizontalSpacing
                itemsInRowWidth = 0
            }
            else
                height = Math.max(height, child.measuredHeight)
        }
        height += paddingTop + paddingBottom + blurRadius.toInt() * 2
        width += blurRadius.toInt() * 2

        height = View.resolveSizeAndState(height, heightMeasureSpec, 0)
        width = View.resolveSizeAndState(width, widthMeasureSpec, 0)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        backPanelBitmap?.let {
            canvas.drawBitmap(it, 0f, scrollY.toFloat(), null)
        }
    }

}
