package com.example.physmin

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * TODO: document your custom view class.
 */

class PickTextView(context: Context, attrs: AttributeSet?) : TextView(context, attrs), Pickable {

    override var picked: Boolean = false
    override var par: PickableGroup? = null
    private var outlineColor: Int = Color.BLUE
    private var answer: String = "NoAnswer"

    init {
        if(attrs != null) {
            val ar: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PickTextView)
            outlineColor = ar.getColor(R.styleable.PickTextView_outlineColor, ContextCompat.getColor(context, R.color.textview_pick_outline))

            ar.recycle()
            this.setTextColor(ContextCompat.getColor(context, R.color.half_black))
        }
    }

    override fun setParent(_parent: PickableGroup) {
        this.par = _parent
    }

    override fun isPicked() : Boolean {
        return picked
    }

    override fun Pick() {
        picked = true
        this.setShadowLayer(2.3f, 0f, 0f, outlineColor)
        this.scaleX = 1.05f
        this.scaleY = 1.05f
    }
    override fun unPick() {
        picked = false
        this.setShadowLayer(0f, 0f, 0f, outlineColor)
        this.scaleX = 1f
        this.scaleY = 1f
    }

}
