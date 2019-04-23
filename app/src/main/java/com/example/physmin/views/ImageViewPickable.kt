package com.example.physmin.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.example.physmin.Pickable
import com.example.physmin.R

class ImageViewPickable(context: Context, attributeSet: AttributeSet?): Pickable(context, attributeSet) {

//    private var outlineColor: Int = Color.BLUE
    override var picked: Boolean = false
    override var par: GroupPickable? = null
    override var answer: Int = -1
    var graph = GraphView(context, null)
    var touchX = 0f
    var touchY = 0f
    var viewX = 0f
    var viewY = 0f
    var isDragged = false
    var draggedView: View? = null

    init {
//        attributeSet?.let {
//            val ar: TypedArray = context.obtainStyledAttributes(it, R.styleable.TextViewPickable)
//            outlineColor = ar.getColor(R.styleable.TextViewPickable_outlineColor, ContextCompat.getColor(context, R.color.textview_pick_outline))
//
//            ar.recycle()
//        }

    }

    override fun setParent(_parent: GroupPickable) {
        this.par = _parent
    }

    override fun isPicked(): Boolean {
        return picked
    }

    override fun pick() {
        picked = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.elevation = 4f

        this.scaleX = 1.05f
        this.scaleY = 1.05f
    }

    override fun unPick() {
        picked = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.elevation = 0f

        this.scaleX = 1f
        this.scaleY = 1f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.rawX
                touchY = event.rawY
                viewX = this.x
                viewY = this.y
            }
            MotionEvent.ACTION_MOVE -> {
                if ((Math.abs(event.rawX - touchX) > 20 || Math.abs(event.rawY - touchY) > 20)) {
                    if(draggedView === null) {
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

                    draggedView!!.animate()
                            .x(event.rawX + par!!.x + (viewX - touchX))
                            .y(event.rawY + par!!.y + (viewY - touchY))
                            .setDuration(0)
                            .start()
                    isDragged = true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if(!isDragged)
                    par?.onClick(this)
                else {
                    par?.par?.groupSettable?.let {
                        for (i in 0 until it.childCount) {
                            var child = it.getChildAt(i)
                            var location = IntArray(2)
                            child.getLocationOnScreen(location)


                            var rect = RectF(location[0].toFloat(),
                                    location[1].toFloat(),
                                    location[0] + child.width.toFloat(),
                                    location[1] + child.height.toFloat())
                            if(rect.contains(event.rawX, event.rawY)) {
                                par?.onClick(this)
                                it.onClick(child)
                            }
                        }
                    }
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
                isDragged = false
                this.visibility = VISIBLE
            }
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        graph.draw(canvas)
    }


}