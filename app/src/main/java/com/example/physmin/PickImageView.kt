package com.example.physmin

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.ImageView

class PickImageView(context: Context, attributeSet: AttributeSet?) : ImageView(context, attributeSet), Pickable {

    private var outlineColor: Int = Color.BLUE
    override var picked: Boolean = false
    override var par: PickableGroup? = null
    override var answer: Short = -1

    init {
        if(attributeSet != null) {
            val ar: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PickTextView)
            outlineColor = ar.getColor(R.styleable.PickTextView_outlineColor, ContextCompat.getColor(context, R.color.textview_pick_outline))

            ar.recycle()
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