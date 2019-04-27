package com.example.physmin

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log.e
import android.view.DragEvent
import android.view.View
import com.example.physmin.views.GroupPickable
import com.example.physmin.views.GroupSettable

abstract class Settable(context: Context, attrs: AttributeSet?): View(context, attrs) {
    abstract var par: GroupSettable?
    abstract var answerView: Pickable?

    fun setParent(_parent: GroupSettable) { this.par = _parent }


    abstract  fun isCorrect(): Boolean

}