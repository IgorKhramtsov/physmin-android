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


    init {
//        attributeSet?.let {
//            val ar: TypedArray = context.obtainStyledAttributes(it, R.styleable.TextViewPickable)
//            outlineColor = ar.getColor(R.styleable.TextViewPickable_outlineColor, ContextCompat.getColor(context, R.color.textview_pick_outline))
//
//            ar.recycle()
//        }

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



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        graph.draw(canvas)
    }


}