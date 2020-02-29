package com.physmin.android

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.physmin.android.activities.TaskPlayerActivity
import com.physmin.android.fragments.tasks.TaskController
import com.physmin.android.views.items.RelationSignView

abstract class Settable(context: Context, attrs: AttributeSet?): View(context, attrs) {
    lateinit var controller: TaskController

    internal var answerView: Pickable? = null
        set(value) {
            field?.show() // Show prev
            field = value
            field?.hide() // Hide new
            controller.updateTaskStatus()
            invalidate()

            onAnswerChanged(field)
        }

    abstract fun isCorrect(): Boolean

    open fun onAnswerChanged(answerView: Pickable?) { }

    fun setTestController(controller: TaskController) {
        this.controller = controller
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                when(this) {
                    is RelationSignView ->
                        (context as TaskPlayerActivity).showDebugMessage(correctAnswers.toString())
                }
            }
        }

        return super.onTouchEvent(event)
    }
}