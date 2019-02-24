package com.example.physmin.views

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.physmin.Settable

class GroupSettable(context: Context, attributeSet: AttributeSet?) : ViewGroup(context, attributeSet), ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    var deviceWidth: Int = 0
    var par: TestConstraintLayout? = null

    init {
        val display: Display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val displaySize = Point()

        display.getSize(displaySize)
        deviceWidth = displaySize.x
        setOnHierarchyChangeListener(this)
    }

    public fun setParent(_par: TestConstraintLayout?) {
        this.par = _par
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        if(this.childCount <= 0)
            return

        val count: Int = childCount
        var curWidth = 0
        var curHeight = 0
        var curLeft = 0
        var curTop: Int = 20

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
                curTop += maxHeight
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
        (child as? ImageViewSettable)?.setParent(this)
        (child as? ImageViewSettable)?.setOnClickListener(this)

        (child as? ImageViewSettableBlank)?.setParent(this)
        (child as? ImageViewSettableBlank)?.setOnClickListener(this)

        (child as? RelationSignView)?.setParent(this)

    }

    override fun onChildViewRemoved(parent: View?, child: View?) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val childAt = getChildAt(i)
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onClick(_view: View?) {
        if( _view is ImageViewSettable) {
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
        }
        else if(_view is ImageViewSettableBlank) {
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
        for(i in 0 until childCount) {
            child = getChildAt(i)
            if(child is Settable)
                if(child.answerView == null) return false
        }
        return true

    }







}