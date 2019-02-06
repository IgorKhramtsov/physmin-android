package com.example.physmin

interface Pickable {

    var picked: Boolean
    var par: GroupPickable?
    var answer: Short

    fun setParent(_parent: GroupPickable)

    fun isPicked() : Boolean

    fun pick()
    fun unPick()
}