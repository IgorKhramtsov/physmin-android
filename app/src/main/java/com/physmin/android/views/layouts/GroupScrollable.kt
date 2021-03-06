package com.physmin.android.views.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Scroller

/*
    TODO: Make cleanup
 */

open class GroupScrollable(context: Context, attrs: AttributeSet? = null):
        BaseGroup(context, attrs) {
    //是否是水平滚动
    private var isHorizontalOrVertical = false
    //是否添加首位滑动阻尼效果
    private var isStartEndScroll = true
    //用于判断滑动翻页的距离
    private var scrollEdge: Int = 0
    //记录边距
    private var leftEdge: Int = 0
    private var rightEdge: Int = 0
    private var topEdge: Int = 0
    private var bottomEdge: Int = 0
    //记录坐标
    private var mLastX = 0f
    private var mLastY = 0f
    private var mLastXIntercept = 0f
    private var mLastYIntercept = 0f
    private var mScroller: Scroller? = null
    //记录手指按下触摸事件时的滚动位置用于判断滑动方向
    private var startScrollY: Int = 0
    private var endScrollY: Int = 0
    private var startScrollX: Int = 0
    private var endScrollX: Int = 0
    //当前Page
    private var currentPage = 0
    private var scrollXY = 0
    var  onPageChangeListener2: onPageChangeListener? = null
    //滚动时间默认800毫秒
    private var duration = 800
    //当前View的尺寸
    private val mWidth: Int = 0
    private val mHeight: Int = 0

    private val isScrollPath: Boolean
        get() = if (isHorizontalOrVertical) endScrollX > startScrollX else endScrollY > startScrollY

    fun isHorizontalOrVertical(): Boolean {
        return isHorizontalOrVertical
    }

    fun setHorizontal(horizontal: Boolean) {
        isHorizontalOrVertical = horizontal
    }

    init {
        init(context)
    }

    private fun init(context: Context) {
        mScroller = Scroller(context)
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        if(this.childCount <= 0 || this.getChildAt(this.childCount-1).bottom + this.top <= this.bottom)
            return false

        var isIntercept = false//判断是否拦截
        val interceptX = ev.x//获取X坐标
        val interceptY = ev.y//获取Y坐标
        when (ev.action) {
            //按下时，不拦截，返回false
            MotionEvent.ACTION_DOWN -> {
                startScrollX = scrollX
                startScrollY = scrollY
                scrollXY = 0
                isIntercept = false
            }
            //移动时对滑动进行处理
            MotionEvent.ACTION_MOVE -> {
                val deltaX = interceptX - mLastXIntercept
                val deltaY = interceptY - mLastYIntercept
                //根据水平距离与垂直距离的大小判断是否是水平滚动或垂直滚动进行事件拦截
                if (isHorizontalOrVertical && Math.abs(deltaX) > Math.abs(deltaY)) {
                    isIntercept = true
                } else if (!isHorizontalOrVertical && Math.abs(deltaY) > Math.abs(deltaX)) {
                    isIntercept = true
                } else {
                    isIntercept = false
                }
            }
            //抬起手指，不拦截，返回false
            MotionEvent.ACTION_UP -> isIntercept = false
        }
        mLastX = interceptX
        mLastY = interceptY
        mLastXIntercept = interceptX//记录最后X轴到滑动坐标
        mLastYIntercept = interceptY//记录最后Y轴到滑动坐标

        return isIntercept
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(this.childCount <= 0 || this.getChildAt(this.childCount-1).bottom + this.top <= this.bottom)
            return false
        val touchX = event.x
        val touchY = event.y

        // Override view height by height of first child
        if(this.getChildAt(0) == null) return  super.onTouchEvent(event)
        val height = this.getChildAt(0).height

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_MOVE -> {
                if (!mScroller!!.isFinished) {
                    mScroller!!.abortAnimation()
                }
                var scrollByStartX: Float = if (isHorizontalOrVertical) touchX - mLastX else 0f
                var scrollByStartY:Float = if (isHorizontalOrVertical) 0f else touchY - mLastY

                //判断左右上下边界,超过边界后设置不可以滑动或者可以滑动
                if (scrollX - scrollByStartX < leftEdge && isHorizontalOrVertical) {
                    scrollByStartX = if (isStartEndScroll) (touchX - mLastX) / 3 else 0f
                } else if (scrollX + width - scrollByStartX > rightEdge && isHorizontalOrVertical) {
                    scrollByStartX = if (isStartEndScroll) (touchX - mLastX) / 3 else 0f

                } else if (scrollY - scrollByStartY < topEdge && !isHorizontalOrVertical) {
                    scrollByStartY = if (isStartEndScroll) (touchY - mLastY) / 1.2f else 0f
                } else if (scrollY + height - scrollByStartY > bottomEdge && !isHorizontalOrVertical) {
                    scrollByStartY = if (isStartEndScroll) (touchY - mLastY) / 1.2f else 0f
                }
                scrollBy((-scrollByStartX).toInt(), (-scrollByStartY).toInt())
            }
            MotionEvent.ACTION_UP -> {
                endScrollX = scrollX
                endScrollY = scrollY
                currentPage = if (isHorizontalOrVertical) scrollX / width else scrollY / height
                // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
                if (scrollEdge == 0) {
                    //设置滑动边距横向是宽度的1/3，纵向是高度的1/5；
                    scrollEdge = if (isHorizontalOrVertical) width / 3 else height / 5
                }
                val edgeX = if (isScrollPath) scrollEdge else width - scrollEdge
                val edgeY = if (isScrollPath) scrollEdge else height - scrollEdge
                var index = if (isHorizontalOrVertical) (scrollX + width - edgeX) / width else (scrollY + height - edgeY) / height
                // Disallow scroll if it last row (work if 2 child in row)
                if (index > childCount / 2 - 1) {
                    index = childCount / 2 - 1
                }
                val dx = if (isHorizontalOrVertical) index * width - scrollX else 0
                val dy = if (isHorizontalOrVertical) 0 else index * height - scrollY
                scrollXY = if (isHorizontalOrVertical) dx else dy
                val scrollX = if (isHorizontalOrVertical) scrollX else 0
                val scrollY = if (isHorizontalOrVertical) 0 else scrollY
                mScroller!!.startScroll(scrollX, scrollY, dx, dy, duration)
                invalidate()
            }
        }
        mLastX = touchX
        mLastY = touchY
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        // 先判断mScroller滚动是否完成
        if (mScroller!!.computeScrollOffset()) {
            // 这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller!!.currX, mScroller!!.currY)
            // 必须调用该方法，否则不一定能看到滚动效果
            postInvalidate()
        } else {
            if (Math.abs(scrollXY) > scrollEdge && Math.abs(scrollXY) != 0) {
                val width = if (isHorizontalOrVertical) width else height
                val scroll = if (isHorizontalOrVertical) scrollX else scrollY
                currentPage = scroll / width
                if (onPageChangeListener2 != null) {
                    onPageChangeListener2!!.onPageChange(currentPage + 1)
                }
            }
        }
    }

    interface onPageChangeListener {
        fun onPageChange(currentPage: Int)
    }

    fun setOnPageChangeListener(onPageChangeListener: onPageChangeListener) {
        this.onPageChangeListener2 = onPageChangeListener
    }

    fun setHorizontalOrVertical(horizontalOrVertical: Boolean): GroupScrollable {
        isHorizontalOrVertical = horizontalOrVertical
        return this
    }

    fun setStartEndScroll(startEndScroll: Boolean): GroupScrollable {
        isStartEndScroll = startEndScroll
        return this
    }

    fun setScrollEdge(scrollEdge: Int): GroupScrollable {
        this.scrollEdge = scrollEdge
        return this
    }

    fun setDuration(duration: Int): GroupScrollable {
        this.duration = duration
        return this
    }

    fun setInvalidate() {
        invalidate()
    }
}