package com.example.physmin.views

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.minus
import androidx.core.graphics.withTranslation
import com.example.physmin.R


class TimerView(context: Context, attrs: AttributeSet?): ImageView(context, attrs) {

    var paintText = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    var hsvWheelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var circleColor = ResourcesCompat.getColor(resources, R.color.ui_panel, null)
    var circleShadowColor = ResourcesCompat.getColor(resources, R.color.ui_shadow, null)
    val shadowBlurRadius = 2.dpToPx()
    val shadowOffsetY = 2.dpToPx()
    val shadowOffsetX = 0f
    var time = 60f
    var staticLayout: StaticLayout? = null
    var hsvColor = floatArrayOf(120f, 0.4f, 0.75f)
    var hsvWheelStrokeWidth = 4.dpToPx()
    var timerHandler = Handler()
    lateinit var timerRunnable: Runnable
    var hsvWheelRect: RectF? = null
    var backShadowPanelBitmap: Bitmap? = null

    init {
        hsvWheelPaint.style = Paint.Style.STROKE
        hsvWheelPaint.strokeCap = Paint.Cap.ROUND
        hsvWheelPaint.strokeWidth = hsvWheelStrokeWidth
        paintText.textSize = 20f.dpToPx()

        this.post {
            val width = this.width - shadowOffsetX
            val height = this.height - shadowOffsetY

            hsvWheelRect = RectF(0f  + hsvWheelStrokeWidth / 2f,
                    0f  + hsvWheelStrokeWidth / 2f,
                    width  - hsvWheelStrokeWidth / 2f,
                    height  - hsvWheelStrokeWidth / 2f)

            backShadowPanelBitmap = generateShadowPanel(width.toInt(), height.toInt(),
                    0f, shadowBlurRadius,
                    shadowOffsetX, shadowOffsetY,
                    circleColor, circleShadowColor,
                    this, CIRCLE)

            val view = this
            timerRunnable = Runnable {
                if (time <= 0) {
                    time = 0f
                    view.invalidate()
                    return@Runnable
                }

                if (hsvColor[0] > 0) {
                    if (hsvColor[0] > 40)
                        hsvColor[0] -= 0.25f
                    else hsvColor[0] -= 0.2f
                    if (hsvColor[0] < 0)
                        hsvColor[0] = 0f
                }
                time -= 0.1f
                view.invalidate()
                timerHandler.postDelayed(timerRunnable, 100)
            }
        }

    }

    fun Restart() {
        time = 60f
        hsvColor = floatArrayOf(120f, 0.4f, 0.75f)
        Start()
    }

    fun Start() {
        timerHandler.removeCallbacks(timerRunnable)
        timerHandler.postDelayed(timerRunnable, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            var height = this.minimumHeight + shadowBlurRadius.toInt() + shadowOffsetY.toInt()

            var width = this.minimumWidth + shadowBlurRadius.toInt() + shadowOffsetX.toInt()

            setMeasuredDimension(width, height)
        } else {
            TODO("VERSION.SDK_INT < JELLY_BEAN")
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        backShadowPanelBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        hsvWheelPaint.color = Color.HSVToColor(hsvColor)
        hsvWheelRect?.let {
            canvas.drawArc(it, 360f, 0f + time * 6, false, hsvWheelPaint)
        }

        staticLayout = StaticLayout(Math.round(time).toString(), paintText, canvas.width, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
        canvas.save()
        canvas.translate(this.width / 2f - staticLayout!!.width / 2f, this.height / 2f - staticLayout!!.height / 2f)
        staticLayout!!.draw(canvas)
        canvas.restore()
    }


}