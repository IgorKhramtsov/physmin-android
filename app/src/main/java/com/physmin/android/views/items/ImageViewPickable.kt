package com.physmin.android.views.items

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import com.physmin.android.Pickable
import com.physmin.android.views.GraphView

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

    override fun getDebugMessage(): String {
        return "x: ${this.graph.functions!![0].x}\r\n" +
                "v: ${this.graph.functions!![0].v} \r\n" +
                "a: ${this.graph.functions!![0].a} \r\n" +
                "answer: ${this.isCorrect}"
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        graph.draw(canvas)
    }


}