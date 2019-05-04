package com.example.physmin.views.Items

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import com.example.physmin.Pickable
import com.example.physmin.views.GraphView

class ImageViewPickable(context: Context, attributeSet: AttributeSet?): Pickable(context, attributeSet) {

    var graph = GraphView(context, null)

    init {
    }

    override fun select() {
        picked = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.elevation = 4f

        this.scaleX = 1.05f
        this.scaleY = 1.05f
    }

    override fun deselect() {
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