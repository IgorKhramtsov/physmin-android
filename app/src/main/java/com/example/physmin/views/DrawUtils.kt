package com.example.physmin.views

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.view.View
import androidx.core.graphics.withTranslation

fun generateBackPanel(width: Int,
                      height: Int,
                      cornerRadius: Float,
                      blurRadius: Float,
                      panelColor: Int,
                      shadowColor: Int,
                      view: View): Bitmap {
    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

    val backPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    backPaint.color = panelColor

    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    shadowPaint.color = shadowColor
    val blurFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.OUTER)
    shadowPaint.maskFilter = blurFilter

    var generatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(generatedBitmap)

    drawRoundRect(shadowPaint, canvas, width, height, cornerRadius, blurRadius)
    drawRoundRect(backPaint, canvas, width, height, cornerRadius, blurRadius)

    view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    return generatedBitmap
}
fun generateInsideShadowPanel(width: Int,
                      height: Int,
                      cornerRadius: Float,
                      blurRadius: Float,
                      panelColor: Int,
                      shadowColor: Int,
                      view: View): Bitmap {
    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

    val backPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    backPaint.color = shadowColor

    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    shadowPaint.color = panelColor
    val blurFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.INNER)
    shadowPaint.maskFilter = blurFilter

    var generatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(generatedBitmap)

    drawRoundRect(backPaint, canvas, width, height, cornerRadius, 0f)
    canvas.withTranslation(0f, 1.dpToPx()) {
        drawRoundRect(shadowPaint, canvas, width, height, cornerRadius, 0f)
    }


    view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    return generatedBitmap
}
fun drawRoundRect(paint: Paint, canvas: Canvas, width: Int, height: Int, cornerRadius: Float, blurRadius: Float) {
    paint.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(blurRadius,
                    blurRadius,
                    width - blurRadius,
                    height - blurRadius,
                    cornerRadius,
                    cornerRadius,
                    it)
        } else {
            canvas.drawRect(blurRadius,
                    blurRadius,
                    width - blurRadius,
                    height - blurRadius,
                    it)
        }
    }
}