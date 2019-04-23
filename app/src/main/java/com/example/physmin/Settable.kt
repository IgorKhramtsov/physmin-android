package com.example.physmin

import android.graphics.Color
import android.util.Log.e
import android.view.DragEvent
import android.view.View
import com.example.physmin.views.GroupSettable

interface Settable: View.OnDragListener {
    var par: GroupSettable?
    var answerView: Pickable?

    fun setParent(_parent : GroupSettable)

    fun isCorrect(): Boolean

    override fun onDrag(v: View, event: DragEvent): Boolean {
        when(event.action) {
            DragEvent.ACTION_DRAG_STARTED,
            DragEvent.ACTION_DRAG_ENDED,
            DragEvent.ACTION_DRAG_LOCATION -> return true

            DragEvent.ACTION_DRAG_ENTERED -> {
                v.setBackgroundColor(Color.LTGRAY)
                return true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                v.setBackgroundColor(Color.TRANSPARENT)
                return true
            }
            DragEvent.ACTION_DROP -> {
                v.setBackgroundColor(Color.TRANSPARENT)
                this.answerView = v as Pickable

                return true
            }
        }

        e("DRAG AND DROP SETTABLE","event action not processed!")
        return false
    }
}