package com.example.physmin

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.physmin.activities.TestActivity
import com.example.physmin.views.items.RelationSignView
import com.example.physmin.views.layouts.GroupSettable
import com.example.physmin.views.layouts.TestConstraintLayout

abstract class Settable(context: Context, attrs: AttributeSet?): View(context, attrs) {
    internal lateinit var parentSettableGroup: GroupSettable
    internal lateinit var parentTestConstraintLayout: TestConstraintLayout
    internal var answerView: Pickable? = null
        set(value) {
            field?.show() // Show prev
            field = value
            field?.deselect() // Deselect new
            field?.hide() // Hide new
            parentTestConstraintLayout.checkTestComplete()
            invalidate()

            onAnswerChanged(field)
        }

    open fun onAnswerChanged(answerView: Pickable?) {

    }

    fun setParent(_parent: GroupSettable) {
        this.parentSettableGroup = _parent
        this.parentTestConstraintLayout = _parent.parentTestConstraintLayout
    }

    abstract fun isCorrect(): Boolean

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