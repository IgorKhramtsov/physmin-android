package com.example.physmin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.example.physmin.activities.TestActivity
import com.example.physmin.views.items.ImageViewPickable
import com.example.physmin.views.layouts.GroupPickable
import kotlin.math.abs

abstract class Pickable(context: Context, attrs: AttributeSet?): View(context, attrs) {
    internal lateinit var par: GroupPickable
    internal var answer = -1
    internal var picked = false
    internal var isCorrect:Boolean? = null
    private var isDraggable = true

    private var touchX = 0f
    private var touchY = 0f
    private var viewX = 0f
    private var viewY = 0f
    private var draggedView: View? = null

    fun setParent(_parent: GroupPickable) {
        this.par = _parent
    }

    fun isPicked() = picked

    fun show() {
        this.visibility = VISIBLE
    }

    fun hide() {
        this.visibility = GONE
    }

    fun setDraggable(boolean: Boolean) {
        this.isDraggable = boolean
    }

    open fun select() {
        picked = true
    }

    open fun deselect() {
        picked = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (!isDraggable) {
            if (event.action == MotionEvent.ACTION_UP)
                this.callOnClick()
            return true
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.rawX
                touchY = event.rawY
                viewX = this.x
                viewY = this.y

                // Create Debug message singleton on corner
                if(BuildConfig.FLAVOR.contains("dev")) {
                    var text = when (this) {
                        is ImageViewPickable -> {
                            "x: ${this.graph.functions!![0].x}\r\n" +
                                    "v: ${this.graph.functions!![0].v} \r\n" +
                                    "a: ${this.graph.functions!![0].a} \r\n" +
                                    "isCorrect: ${this.isCorrect}"
                        }
                        else -> {
                            " none "
                        }
                    }
                    (context as TestActivity).showDebugMessage(text)
                }

            }
            MotionEvent.ACTION_MOVE -> {
                if ((abs(event.rawX - touchX) > 20 || abs(event.rawY - touchY) > 20) || draggedView != null) {
                    if (draggedView === null) createDraggedView()
                    par.resetPickedItem()

                    draggedView?.let {
                        moveTo(it, event.rawX + par.x + (viewX - touchX), event.rawY + par.y + (viewY - touchY))
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (draggedView === null)
                    par.onClick(this)
                else
                    par.parentTestConstraintLayout.groupSettable.let {
                        var child: View
                        var rect: RectF

                        for (i in 0 until it.childCount) {
                            child = it.getChildAt(i)
                            if (child !is Settable)
                                continue
                            rect = getViewBounds(child)

                            if (rect.contains(event.rawX, event.rawY)) {
                                par.onClick(this)
                                it.onClick(child)
                                par.parentTestConstraintLayout.removeView(draggedView)
                                draggedView = null
                                return true
                            }
                        }
                    }

                draggedView?.let {
                    moveTo(it, event.rawX + par.x + (viewX - touchX), event.rawY + par.y + (viewY - touchY))
                }
                draggedView?.let {
                    it.animate()
                            .x(viewX)
                            .y(viewY)
                            .setDuration(50)
                            .start()
                }

                par.parentTestConstraintLayout.removeView(draggedView)
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

    private fun createDraggedView() {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.draw(canvas)
        draggedView = ImageView(context)
        (draggedView as ImageView).let {
            it.setImageDrawable(BitmapDrawable(resources, bitmap))
            par?.parentTestConstraintLayout?.addView(it)
            it.bringToFront()
        }
        this.visibility = INVISIBLE
    }

}