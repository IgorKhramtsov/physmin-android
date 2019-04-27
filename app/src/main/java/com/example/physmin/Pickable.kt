package com.example.physmin

import android.content.ClipData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.example.physmin.views.GroupPickable


// TODO: Make it class??
abstract class Pickable(context: Context, attrs: AttributeSet?): View(context, attrs) {

    abstract var picked: Boolean
    abstract var par: GroupPickable?
    abstract var answer: Int

    var touchX = 0f
    var touchY = 0f
    var viewX = 0f
    var viewY = 0f
    var draggedView: View? = null

    fun setParent(_parent: GroupPickable) { this.par = _parent }

    fun isPicked(): Boolean { return picked }

    abstract fun pick()
    abstract fun unPick()

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.rawX
                touchY = event.rawY
                viewX = this.x
                viewY = this.y
            }
            MotionEvent.ACTION_MOVE -> {
                if ((Math.abs(event.rawX - touchX) > 20 || Math.abs(event.rawY - touchY) > 20) || draggedView != null) {
                    if(draggedView === null) createDraggedView()

                    draggedView?.let {
                        moveTo(it, event.rawX + par!!.x + (viewX - touchX), event.rawY + par!!.y + (viewY - touchY))
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if(draggedView === null)
                    par?.onClick(this)
                else
                    par?.par?.groupSettable?.let {
                        var child: View
                        var rect: RectF

                        for (i in 0 until it.childCount) {
                            child = it.getChildAt(i)
                            if(!(child is Settable))
                                continue
                            rect = getViewBounds(child)

                            if(rect.contains(event.rawX, event.rawY)) {
                                par?.onClick(this)
                                it.onClick(child)
                                par?.par?.removeView(draggedView)
                                draggedView = null
                                return true
                            }
                        }
                    }

                draggedView?.let {
                    moveTo(it, event.rawX + par!!.x + (viewX - touchX), event.rawY + par!!.y + (viewY - touchY))
                }
                draggedView?.let {
                    it.animate()
                            .x(viewX)
                            .y(viewY)
                            .setDuration(50)
                            .start()
                }

                par?.par?.removeView(draggedView)
                draggedView = null
                this.visibility = VISIBLE
            }
        }

        return true
    }

    private fun moveTo(view: View, x: Float, y: Float, duration: Long = 0) {
        view.animate()
                .x(x)
                .y(y)
                .setDuration(duration)
                .start()
    }
    private fun getViewBounds(view: View): RectF {
        val location = IntArray(2)
        view.getLocationOnScreen(location)

        return RectF().apply {
            left = location[0].toFloat()
            top = location[1].toFloat()
            right = left + view.width.toFloat()
            bottom = top + view.height.toFloat()
        }
    }

    fun createDraggedView() {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.draw(canvas)
        draggedView = ImageView(context)
        (draggedView as ImageView).let {
            it.setImageDrawable(BitmapDrawable(resources, bitmap))
            par?.par?.addView(it)
            it.bringToFront()
        }
        this.visibility = INVISIBLE
    }

}