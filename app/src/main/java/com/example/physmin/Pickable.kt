package com.example.physmin

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
import com.example.physmin.activities.TestActivity
import com.example.physmin.fragments.tests.TestController
import com.example.physmin.views.items.ImageViewPickable
import com.example.physmin.views.items.TextViewPickable
import com.example.physmin.views.layouts.GroupPickable
import java.util.zip.Inflater
import kotlin.math.abs
import kotlin.math.pow

abstract class Pickable(context: Context, attrs: AttributeSet?): View(context, attrs) {
    lateinit var controller: TestController
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

    fun setTestController(controller: TestController) {
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
                if(isDev()) {
                    val text = when (this) {
                        is ImageViewPickable -> {
                            "x: ${this.graph.functions!![0].x}\r\n" +
                                    "v: ${this.graph.functions!![0].v} \r\n" +
                                    "a: ${this.graph.functions!![0].a} \r\n" +
                                    "answer: ${this.isCorrect}"
                        }
                        is TextViewPickable -> {
                            " correct question: ${this.isCorrect}"
                        }
                        else -> {
                            " none "
                        }
                    }
                    (context as TestActivity).showDebugMessage(text)
                }

            }
            MotionEvent.ACTION_MOVE -> {
                if (draggedView != null || ((event.rawX - touchX).pow(2) + (event.rawY - touchY).pow(2) > touchSlop.pow(2))) {
                    draggedView?:createDraggedView()
                    controller.resetPickedItem()

                    draggedView?.let {
                        moveTo(it, event.rawX + controller.pickableGroup.x + (viewX - touchX), event.rawY + controller.pickableGroup.y + (viewY - touchY))
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (draggedView === null)
                    controller.setPickedItem(this)
                else
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
                                draggedView?.let { (it.parent as ViewGroup).removeView(it) }
                                draggedView = null
                                return true
                            }
                        }
                    }

                draggedView?.let {
                    moveTo(it, event.rawX + controller.pickableGroup.x + (viewX - touchX), event.rawY + controller.pickableGroup.y + (viewY - touchY))
                }
                draggedView?.let { moveTo(it, viewX, viewY, 50) }
                draggedView?.let { (it.parent as ViewGroup).removeView(it) }
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
            (context as Activity).addContentView(it, ViewGroup.LayoutParams(width, height))
            it.bringToFront()
        }
        this.visibility = INVISIBLE
    }

}