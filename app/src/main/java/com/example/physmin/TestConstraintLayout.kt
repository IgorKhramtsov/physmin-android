package com.example.physmin

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class TestConstraintLayout(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet),
        ViewGroup.OnHierarchyChangeListener {
    var pickableGroup: PickableGroup? = null
    var settableGroup: SettableGroup? = null

    init {
        setOnHierarchyChangeListener(this)
    }

    override fun onChildViewAdded(p0: View?, p1: View) {
        if(p1 is PickableGroup) {
            pickableGroup = p1
            pickableGroup!!.setParent(this)
        }
        else if(p1 is SettableGroup) {
            settableGroup = p1
            settableGroup!!.setParent(this)
        }
    }

    override fun onChildViewRemoved(p0: View?, p1: View?) {

    }

    fun getPickedItem(): Pickable? {
        return pickableGroup?.pickedItem
    }

    fun resetPickedItem() {
        pickableGroup?.pickedItem?.unPick()
        pickableGroup?.pickedItem = null
    }

}