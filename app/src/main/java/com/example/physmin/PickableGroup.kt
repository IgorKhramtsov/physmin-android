package com.example.physmin

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.util.AttributeSet
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.util.DisplayMetrics
import android.os.Bundle
import android.widget.ScrollView

class PickableGroup(context: Context, attrs: AttributeSet?) : ScrollGroup(context, attrs),
        ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    var deviceWidth: Int = 0
    var pickedItem: Pickable? = null
    var par: TestConstraintLayout? = null

    init {
        val display: Display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val displaySize = Point()

        display.getSize(displaySize)
        deviceWidth = displaySize.x

        setOnHierarchyChangeListener(this)

        val mScrollGroup = findViewById(R.id.pickableGroup) as? ScrollGroup
        mScrollGroup?.setHorizontalOrVertical(false)
                ?.setStartEndScroll(true)
                ?.setDuration(300)
                ?.setInvalidate()
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
                child.Pick()
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
        (child as? PickTextView)?.setParent(this)
        (child as? PickImageView)?.setParent(this)

    }

    override fun onChildViewRemoved(parent: View?, child: View?) {

    }

//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val count = childCount
//        // Measurement will ultimately be computing these values.
//        var maxHeight = 0
//        var maxWidth = 0
//        var childState = 0
//        var mLeftWidth = 0
//        var rowCount = 0
//        // Iterate through all children, measuring them and computing our dimensions
//        // from their size.
//        for (i in 0 until count) {
//            val child = getChildAt(i)
//            if (child.visibility == View.GONE)
//                continue
//            // Measure the child.
//            measureChild(child, widthMeasureSpec, heightMeasureSpec)
//            maxWidth += Math.max(maxWidth, child.measuredWidth)
//            mLeftWidth += child.measuredWidth
//            if (mLeftWidth / deviceWidth > rowCount) {
//                maxHeight += child.measuredHeight
//                rowCount++
//            } else {
//                maxHeight = Math.max(maxHeight, child.measuredHeight)
//            }
//            childState = View.combineMeasuredStates(childState, child.measuredState)
//        }
//        // Check against our minimum height and width
//        maxHeight = Math.max(maxHeight, suggestedMinimumHeight)
//        maxWidth = Math.max(maxWidth, suggestedMinimumWidth)
//        // Report our final dimensions.
//        setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
//                View.resolveSizeAndState(maxHeight, heightMeasureSpec, childState shl View.MEASURED_HEIGHT_STATE_SHIFT))
//
////        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//    }

}
