package com.example.physmin

import com.example.physmin.views.GroupSettable

interface Settable {
    var par: GroupSettable?
    var answerView: Pickable?

    fun setParent(_parent : GroupSettable)

    fun isCorrect(): Boolean
}