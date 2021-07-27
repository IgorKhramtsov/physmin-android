package com.physmin.android

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ImageView
import com.physmin.android.activities.TaskPlayerActivity
import com.physmin.android.fragments.tasks.TaskController
import kotlin.math.pow

abstract class Pickable(context: Context, attrs: AttributeSet?): View(context, attrs) {
    lateinit var controller: TaskController
    internal var answer = -1
    internal var picked = false
    internal var isCorrect = "unknown"
    private var isDraggable = true

    private var touchX = 0f
    private var touchY = 0f
    private var viewX = 0f
    private var viewY = 0f
    private var touchSlop = 0f
    private var draggedView: View? = null

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
    }

    fun setTestController(controller: TaskController) {
        this.controller = controller
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

    open fun getDebugMessage(): String {
        return isCorrect
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

                if (isDev())
                    (context as TaskPlayerActivity).showDebugMessage(getDebugMessage())
            }
            MotionEvent.ACTION_MOVE -> {
                if (draggedView != null || ((event.rawX - touchX).pow(2) + (event.rawY - touchY).pow(2) > touchSlop.pow(2))) {
                    draggedView ?: createDraggedView()
                    controller.resetPickedItem()

                    draggedView?.let {
                        moveTo(it,
                                event.rawX + controller.pickableGroup.x + (viewX - touchX),
                                event.rawY + controller.pickableGroup.y + (viewY - touchY))
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (draggedView === null) {
                    controller.setPickedItem(this)
                } else {
                    controller.settableGroup.let {
                        var child: View
                        var rect: RectF

                        for (i in 0 until it.childCount) {
                            child = it.getChildAt(i)
                            if (child !is Settable)
                                continue
                            rect = getViewBounds(child)

                            if (rect.contains(event.rawX, event.rawY)) {
                                controller.setPickedItem(this)
                                it.onClick(child)
                                removeDraggedView()
                                this.visibility = INVISIBLE
                                return true
                            }
                        }
                    }
                    val location = IntArray(2)
                    this.getLocationOnScreen(location)
                    draggedView?.let { moveTo(it, location[0].toFloat(), location[1].toFloat(), 100, Runnable { removeDraggedView() }) }
                }
            }
        }

        return true
    }

    private fun moveTo(view: View, x: Float, y: Float, duration: Long = 0, endAction: Runnable = Runnable {  })  {
        view.animate()
                .x(x)
                .y(y)
                .setDuration(duration)
                .withEndAction(endAction)
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
            (context as Activity).addContentView(it, ViewGroup.LayoutParams(width, height))
            it.bringToFront()
        }
        this.visibility = INVISIBLE
    }

    private fun removeDraggedView() {
        draggedView?.let { (it.parent as ViewGroup).removeView(it) }
        draggedView = null
        this.visibility = VISIBLE
    }

}