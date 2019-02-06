package com.example.physmin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.CountDownTimer
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

class TimerTask(context: Context, attrs: AttributeSet?) : ImageView(context, attrs) {

    var paintText = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var time = 60f
    var staticLayout: StaticLayout?= null
    var hsv  = floatArrayOf(120f, 0.4f, 0.75f)

    init {
        val pb = this
        paint.strokeWidth = 5f
        val density = context.getResources().getDisplayMetrics().density
        paintText.textSize = 20f*density

        var cdt = object : CountDownTimer(60000,100) {
            override fun onTick(millisUntilFinished: Long) {

                time-=0.1f

                if(hsv[0] > 0 ) {
                    if (hsv[0] > 40)
                        hsv[0] -= 0.25f
                    else hsv[0] -=0.2f
                    if(hsv[0] < 0)
                        hsv[0] = 0f
                }

                pb.invalidate()
            }

            override fun onFinish() {
            }
        }.start()
    }
    override  fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.HSVToColor(hsv)
        canvas.drawArc(RectF(canvas.width/2-canvas.width/3f,canvas.height/2-canvas.height/3f,canvas.width/2+canvas.width/3f,canvas.height/2+canvas.height/3f), 360F, 0F+time*6, true, paint)

        paint.color = Color.WHITE
        canvas.drawOval(RectF(canvas.width/2-canvas.width/4f,canvas.width/2-canvas.width/4f,canvas.width/2+canvas.width/4f,canvas.width/2+canvas.width/4f),paint)
        staticLayout = StaticLayout(Math.round(time).toString(), paintText, canvas.width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
        canvas.save()
        canvas.translate(this.width / 2f - staticLayout!!.width / 2f, this.height / 2f - staticLayout!!.height / 2f)
        staticLayout!!.draw(canvas)
        canvas.restore()
    }



}