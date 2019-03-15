package com.example.physmin.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import com.example.physmin.Pickable
import com.example.physmin.R

class ImageViewPickable(context: Context, attributeSet: AttributeSet?) : GraphView(context, attributeSet), Pickable {

    private var outlineColor: Int = Color.BLUE
    override var picked: Boolean = false
    override var par: GroupPickable? = null
    override var answer: Int = -1

    init {
        if(attributeSet != null) {
            val ar: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TextViewPickable)
            outlineColor = ar.getColor(R.styleable.TextViewPickable_outlineColor, ContextCompat.getColor(context, R.color.textview_pick_outline))

            ar.recycle()
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
}