package com.physmin.android

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.physmin.android.activities.TestActivity
import com.physmin.android.fragments.tasks.TestController
import com.physmin.android.views.items.RelationSignView

abstract class Settable(context: Context, attrs: AttributeSet?): View(context, attrs) {
    lateinit var controller: TestController

    internal var answerView: Pickable? = null
        set(value) {
            field?.show() // Show prev
            field = value
            field?.hide() // Hide new
            controller.updateTestStatus()
            invalidate()

            onAnswerChanged(field)
        }

    abstract fun isCorrect(): Boolean

    open fun onAnswerChanged(answerView: Pickable?) { }

    fun setTestController(controller: TestController) {
        this.controller = controller
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                when(this) {
                    is RelationSignView ->
                        (context as TestActivity).showDebugMessage(correctAnswers.toString())
                }
            }
        }

        return super.onTouchEvent(event)
    }
}