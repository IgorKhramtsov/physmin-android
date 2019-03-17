package com.example.physmin.views

import android.content.Context
import android.graphics.Point
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

    var itemsPadding = 8.dpToPx().toInt()

    init {
        val display: Display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val displaySize = Point()

        display.getSize(displaySize)
        deviceWidth = displaySize.x

        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.ui_panel, null))

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

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        if(this.childCount <= 0)
            return

        val count: Int = childCount
        var curWidth = 0
        var curHeight = 0
        var curLeft = 0
        var curTop = 20

        var maxWidth = this.getChildAt(0).measuredWidth
        var maxHeight = this.getChildAt(0).measuredHeight

        val contentLeft = this.paddingLeft
        val contentTop = this.paddingTop
        val contentRight = this.measuredWidth - this.paddingRight
        val contentBottom = this.measuredHeight - this.paddingBottom
        val childWidth = if (count > 0 ) getChildAt(0).layoutParams.width else contentRight - contentLeft
        val childHeight = if(count > 0) getChildAt(0).layoutParams.height else contentBottom - contentTop

        var childInRow = Math.floor((contentRight - contentLeft)*1.0/maxWidth*1.0).toInt()
        var childSpacing = ((contentRight - contentLeft)-childInRow*maxWidth)/(childInRow+1)
        var curChildInRow = 0

        curLeft = contentLeft
        curTop = contentTop

        for (i in 0 until count) {
            val child: View = getChildAt(i)

//            if(child.visibility == View.GONE)
//                continue
            //Get the maximum size of the child
//            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST))
//            curWidth = maxWidth
//            curHeight = maxHeight
            //wrap is reach to the end
//            if(curLeft + curWidth >= contentRight) {
//                curLeft = contentLeft
//                curTop += maxHeight
//                maxHeight = 0
//            }

            curChildInRow++
            if(curChildInRow > childInRow){
                curLeft = contentLeft
                curTop += maxHeight + 20
                curChildInRow = 1
            }
            curLeft += childSpacing
            //do the layout
            child.layout(curLeft, curTop, curLeft + maxWidth, curTop + maxHeight)
            //store the max height
//            if(maxHeight < curHeight)
//                maxHeight = curHeight

            curLeft += maxWidth
        }


    }

    override fun onChildViewAdded(parent: View?, child: View) {
        child.setOnClickListener(this)
        (child as? TextViewPickable)?.setParent(this)
        (child as? ImageViewPickable)?.setParent(this)

    }

    override fun onChildViewRemoved(parent: View?, child: View?) {

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount
        // Measurement will ultimately be computing these values.
        var height = 0
        var width = 0
        var itemsInRowWidth = 0
        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (i in 0 until count) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            if (child.visibility == View.GONE)
                continue

            itemsInRowWidth += child.measuredWidth
            if(itemsInRowWidth > deviceWidth) {
                width = Math.max(width, itemsInRowWidth)
                height += child.measuredHeight + itemsPadding
                itemsInRowWidth = 0
            }
            else
                height = Math.max(height, child.measuredHeight)
        }
        height += paddingTop + paddingBottom
        height = View.resolveSizeAndState(height, heightMeasureSpec, 0)
        width = View.resolveSizeAndState(width, widthMeasureSpec, 0)
        setMeasuredDimension(width, height)
    }

}
