package com.physmin.android.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.button.MaterialButton
import com.physmin.android.R


class CustomButton(context: Context, attrs: AttributeSet?): androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    private val blurRadius = 4f
    private val offsetX = 0f
    private val offsetY = 4f
    private val button_back by lazy {
        generateShadowPanel(this.width, this.height,
                44.dpToPx(), blurRadius, offsetX, offsetY,
                ResourcesCompat.getColor(resources, R.color.colorPrimary, null),
                ResourcesCompat.getColor(resources, R.color.ui_shadow25, null), this)
    }
    private lateinit var buttonDrawable: Drawable

    init {
        this.setTextColor(ResourcesCompat.getColor(resources, R.color.textColorLight, null))
        this.gravity = Gravity.CENTER
        this.setShadowLayer(4.dpToPx(),
                2.dpToPx(), 2.dpToPx(),
                ResourcesCompat.getColor(resources, R.color.textShadow, null))
        this.setPadding(0,0,0,(blurRadius + offsetY).toInt())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = View.resolveSizeAndState(height, heightMeasureSpec, 0)
        var width = View.resolveSizeAndState(width, widthMeasureSpec, 0)

        height += 2*blurRadius.toInt() + offsetX.toInt()
        width += 2*blurRadius.toInt() + offsetY.toInt()
        setMeasuredDimension(width, height)

    }

    override fun onDraw(canvas: Canvas) {
        button_back?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        super.onDraw(canvas)
    }
}
