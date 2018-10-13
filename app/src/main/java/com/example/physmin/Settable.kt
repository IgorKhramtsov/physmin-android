package com.example.physmin

import android.graphics.Paint
import android.text.TextPaint

interface Settable {
    var par: SettableGroup?
    var answerView: Pickable?

    fun setParent(_parent : SettableGroup)
}