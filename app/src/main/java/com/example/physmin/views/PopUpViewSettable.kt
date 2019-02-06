package com.example.physmin.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.FrameLayout
import android.graphics.drawable.ColorDrawable
import android.graphics.Rect
import com.example.physmin.Pickable
import com.example.physmin.R
import com.example.physmin.Settable


class PopUpViewSettable(context: Context, attrs: AttributeSet?) : ImageView(context, attrs), View.OnClickListener, Settable {

    var pickedView: ImageView? = null
    val _paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override var par: GroupSettable? = null
    override var answerView: Pickable? = null
        set(value) {
            field = value
            if(par!!.isAllChecked())
                par!!.par!!.testComplete()
        }
    override var correctAnswer: Short = -1

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

            pickedView = if (pickedView == choosed_view) null else choosed_view as ImageView

            popupWindow.dismiss()
            view?.invalidate()
        }

        popupView.findViewById<ImageView>(R.id.imageView_less).setOnClickListener(listener)
        popupView.findViewById<ImageView>(R.id.imageView_equal).setOnClickListener(listener)
        popupView.findViewById<ImageView>(R.id.imageView_more).setOnClickListener(listener)

        var location = intArrayOf(0,0)
        this.getLocationOnScreen(location)
        popupWindow.showAtLocation(view,0,location[0]+Math.round(this.width*0.1f),location[1]-Math.round(this.height*0.4f))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(pickedView == null)
            return

        var bitmap = (pickedView!!.drawable as? BitmapDrawable)?.bitmap
        var image_bitmap = (this!!.drawable as? BitmapDrawable)?.bitmap

        var iHeight = (canvas.height/10f)
        var iWidth = (canvas.width/10f)

        val src = Rect(0, 0, bitmap!!.getWidth() - 1, bitmap!!.getHeight() - 1)
        val dest = Rect(Math.round(iWidth*4),Math.round(iHeight*2),Math.round(iWidth*6),Math.round(iHeight*8))
        canvas.drawBitmap(bitmap,src,dest,null)

    }

    override fun setParent(_parent : GroupSettable) {
        this.par = _parent
    }

}

