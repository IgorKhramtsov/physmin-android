package com.example.physmin

import com.example.physmin.views.GroupPickable

interface Pickable {

    var picked: Boolean
    var par: GroupPickable?
    var answer: Int

    fun setParent(_parent: GroupPickable)

    fun isPicked() : Boolean

    fun pick()
    fun unPick()
}