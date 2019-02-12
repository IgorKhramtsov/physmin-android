package com.example.physmin.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.TextView
import com.example.physmin.Pickable
import com.example.physmin.R

/**
 * TODO: document your custom view class.
 */

class TextViewPickable(context: Context, attrs: AttributeSet?) : TextView(context, attrs), Pickable {

    override var picked: Boolean = false
    override var par: GroupPickable? = null
    private var outlineColor: Int = Color.BLUE
    override var answer: Short = -1

    init {
        if(attrs != null) {
            val ar: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewPickable)
            outlineColor = ar.getColor(R.styleable.TextViewPickable_outlineColor, ContextCompat.getColor(context, R.color.textview_pick_outline))
            answer = ar.getInt(R.styleable.TextViewPickable_answer, 0).toShort()

            ar.recycle()
            this.setTextColor(ContextCompat.getColor(context, R.color.half_black))
        }
    }

    override fun setParent(_parent: GroupPickable) {
        this.par = _parent
    }

    override fun isPicked() : Boolean {
        return picked
    }

    override fun pick() {
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
