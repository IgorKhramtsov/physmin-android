package com.example.physmin

import android.widget.ImageView

interface Pickable {

    var picked: Boolean
    var par: PickableGroup?
    var answer: Short

    fun setParent(_parent: PickableGroup)

    fun isPicked() : Boolean

    fun Pick()
    fun unPick()
}