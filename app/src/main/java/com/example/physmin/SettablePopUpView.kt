package com.example.physmin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import java.lang.ClassCastException
import android.widget.FrameLayout
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.R.attr.bitmap
import android.graphics.Rect
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.view.Display
import android.view.WindowManager








class SettablePopUpView(context: Context, attrs: AttributeSet?) : ImageView(context, attrs), View.OnClickListener, Settable {

    var picked_view: ImageView? = null
    val _paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override var par: SettableGroup? = null
    override var answerView: Pickable? = null

    init {
       setOnClickListener(this)
    }


    override fun onClick(view: View?) {

        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.pop_up_elements, null)
        val popupWindow = PopupWindow(popupView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        popupWindow.setBackgroundDrawable(ColorDrawable())
        popupWindow.isOutsideTouchable = true
        popupWindow.isTouchable = true

        var listener = OnClickListener { choosed_view ->

            picked_view = if (picked_view == choosed_view) null else choosed_view as ImageView

            popupWindow.dismiss()
            view?.invalidate()
        }

        popupView.findViewById<ImageView>(R.id.imageView_less).setOnClickListener(listener)
        popupView.findViewById<ImageView>(R.id.imageView_equal).setOnClickListener(listener)
        popupView.findViewById<ImageView>(R.id.imageView_more).setOnClickListener(listener)

        var location = intArrayOf(0,0)
        this.getLocationOnScreen(location);
        popupWindow.showAtLocation(view,0,location[0]-25,location[1]-130)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(picked_view == null)
            return

        var bitmap = (picked_view!!.drawable as? BitmapDrawable)?.bitmap

        val src = Rect(0, 0, bitmap!!.getWidth() - 1, bitmap!!.getHeight() - 1)
        val dest = Rect(this.left/2-28, this.top/2-39, this.left/2+28, this.top/2+39)
        canvas.drawBitmap(bitmap,src,dest,null)

    }

    override fun setParent(_parent : SettableGroup) {
        this.par = _parent
    }

}

