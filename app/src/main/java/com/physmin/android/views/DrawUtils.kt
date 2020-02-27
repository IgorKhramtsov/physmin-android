package com.physmin.android.views

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.view.View
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import kotlin.math.abs

fun Float.isZero(): Boolean = abs(this) < 0.00001f
fun Int.spToPx(): Float = this * Resources.getSystem().displayMetrics.scaledDensity
fun Int.pxToSp(): Float = this / Resources.getSystem().displayMetrics.scaledDensity
fun Int.dpToPx(): Float = this * Resources.getSystem().displayMetrics.density
fun Float.dpToPx(): Float = this * Resources.getSystem().displayMetrics.density

const val ROUNDED_RECT = 0
const val RECT = 1
const val CIRCLE = 2

fun generateShadowPanel(width: Int,
                        height: Int,
                        cornerRadius: Float,
                        blurRadius: Float,
                        offsetX: Float,
                        offsetY: Float,
                        panelColor: Int,
                        shadowColor: Int,
                        view: View,
                        shapeType: Int = ROUNDED_RECT): Bitmap? {
    if (width <= 0 || height <= 0)
        return null

    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

    val backPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    backPaint.color = panelColor

    backPaint.setShadowLayer(blurRadius, offsetX, offsetY, shadowColor)

    var generatedBitmap = Bitmap.createBitmap(width + (2 * blurRadius - abs(offsetX)).toInt(),
            height + (2 * blurRadius - abs(offsetY)).toInt(),
            Bitmap.Config.ARGB_8888)
    val canvas = Canvas(generatedBitmap)

    when (shapeType) {
        ROUNDED_RECT -> {
            canvas.withTranslation(-offsetX, -offsetY) {
                drawRoundRect(backPaint, canvas, width, height, cornerRadius, blurRadius)
            }
        }
        CIRCLE -> {
            canvas.withTranslation(offsetX, offsetY) {
                drawCircle(backPaint, canvas, width, height, blurRadius)
            }
        }
    }


    view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    return generatedBitmap
}

fun generateShadowPanel(width: Int,
                        height: Int,
                        cornerRadius: Float,
                        blurRadius: Float,
                        panelColor: Int,
                        shadowColor: Int,
                        view: View): Bitmap? {
    return generateShadowPanel(width, height, cornerRadius, blurRadius, 0f, 0f, panelColor, shadowColor, view)
}

fun generateInsideShadowPanel(width: Int,
                              height: Int,
                              cornerRadius: Float,
                              blurRadius: Float,
                              panelColor: Int,
                              shadowColor: Int,
                              view: View): Bitmap? {
    if (width <= 0 || height <= 0)
        return null

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
    drawRoundRect(paint, canvas, width, height, cornerRadius, blurRadius, 0f, 0f)
}

fun drawRoundRect(paint: Paint, canvas: Canvas, width: Int, height: Int, cornerRadius: Float, blurRadius: Float, offsetX: Float, offsetY: Float) {
    paint.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(offsetX + blurRadius,
                    offsetY + blurRadius,
                    width - blurRadius,
                    height - blurRadius,
                    cornerRadius,
                    cornerRadius,
                    it)
        } else {
            canvas.drawRect(offsetX + blurRadius,
                    offsetY + blurRadius,
                    width - blurRadius,
                    height - blurRadius,
                    it)
        }
    }
}

fun drawCircle(paint: Paint, canvas: Canvas, width: Int, height: Int, blurRadius: Float) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        canvas.drawArc(blurRadius, blurRadius, width.toFloat() - blurRadius, height.toFloat() - blurRadius, 0f, 360f, true, paint)
    }
    //canvas.drawCircle((width - blurRadius) / 2f, (height - blurRadius) / 2f, (Math.max(width, height) - blurRadius) / 2f, paint)
}