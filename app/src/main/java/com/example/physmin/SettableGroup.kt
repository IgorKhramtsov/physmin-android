package com.example.physmin

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

class SettableGroup(context: Context, attributeSet: AttributeSet?) : ViewGroup(context, attributeSet), ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

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
        val count: Int = childCount
        var curWidth = 0
        var curHeight = 0
        var curLeft = 0
        var curTop = 0
        var maxHeight = 0

        val contentLeft = this.paddingLeft + 8
        val contentTop = this.paddingTop
        val contentRight = this.measuredWidth - this.paddingRight
        val contentBottom = this.measuredHeight - this.paddingBottom
        val childWidth = if (count > 0 ) getChildAt(0).layoutParams.width else contentRight - contentLeft
        val childHeight = if(count > 0) getChildAt(0).layoutParams.height else contentBottom - contentTop

        curLeft = contentLeft
        curTop = contentTop

        for (i in 0 until count) {
            val child: View = getChildAt(i)

            if(child.visibility == View.GONE)
                continue

            //Get the maximum size of the child
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST))
            curWidth = child.measuredWidth
            curHeight = child.measuredHeight
            //wrap is reach to the end
            if(curLeft + curWidth >= contentRight) {
                curLeft = contentLeft
                curTop += maxHeight
                maxHeight = 0
            }
            curLeft += 8
            //do the layout
            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
            //store the max height
            if(maxHeight < curHeight)
                maxHeight = curHeight

            curLeft += curWidth
        }
    }

    override fun onChildViewAdded(parent: View?, child: View) {
        (child as? ImageSettableView)?.setParent(this)
        (child as? ImageSettableView)?.setOnClickListener(this)

        (child as? BlankImageView)?.setParent(this)
        (child as? BlankImageView)?.setOnClickListener(this)
    }

    override fun onChildViewRemoved(parent: View?, child: View?) {

    }

    override fun onClick(_view: View?) {

        if( _view is ImageSettableView ) {
            val imageView = _view as ImageSettableView
            (imageView.answerView as View?)?.visibility = View.VISIBLE

            imageView.answerView = par?.getPickedItem()
            (par?.getPickedItem() as View?)?.visibility = View.GONE
            imageView.invalidate()

            var child: View?
            for (i in 0 until childCount) {
                child = getChildAt(i)
                if (child !is ImageSettableView)
                    continue

                if (child != imageView)
                    if (child.answerView == imageView.answerView) {
                        child.answerView = null
                        child.invalidate()
                    }
            }
            par?.resetPickedItem()
        }
        else if(_view is BlankImageView) {
            val imageView = _view as BlankImageView
            (imageView.answerView as View?)?.visibility = View.VISIBLE

            imageView.answerView = par?.getPickedItem()
            (par?.getPickedItem() as View?)?.visibility = View.GONE
            imageView.invalidate()

            var child: View?
            for (i in 0 until childCount) {
                child = getChildAt(i)
                if (child !is ImageSettableView)
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
}