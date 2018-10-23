package com.example.physmin

import android.graphics.Paint
import android.text.TextPaint

interface Settable {
    var par: SettableGroup?
    var answerView: Pickable?

    var correctAnswer: Short

    fun setParent(_parent : SettableGroup)

    fun isCorrect(): Boolean {
        return correctAnswer == answerView?.answer
    }
}