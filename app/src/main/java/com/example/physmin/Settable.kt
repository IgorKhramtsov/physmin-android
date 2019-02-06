package com.example.physmin

interface Settable {
    var par: GroupSettable?
    var answerView: Pickable?

    var correctAnswer: Short

    fun setParent(_parent : GroupSettable)

    fun isCorrect(): Boolean {
        return correctAnswer == answerView?.answer
    }
}