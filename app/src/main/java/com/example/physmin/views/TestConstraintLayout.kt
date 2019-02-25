package com.example.physmin.views

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.physmin.Pickable
import com.example.physmin.activities.TestActivity

class TestConstraintLayout(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet),
        ViewGroup.OnHierarchyChangeListener {
    var groupPickable: GroupPickable? = null
    var groupSettable: GroupSettable? = null
    var nextTestButton: Button? = null

    init {
        setOnHierarchyChangeListener(this)
    }

    override fun onChildViewAdded(p0: View?, p1: View) {
        if(p1 is GroupPickable) {
            groupPickable = p1
            groupPickable!!.setParent(this)
        }
        else if(p1 is GroupSettable) {
            groupSettable = p1
            groupSettable!!.setParent(this)
        }
        else if(p1 is Button) {
            nextTestButton = p1
        }
    }

    override fun onChildViewRemoved(p0: View?, p1: View?) {

    }

    fun getPickedItem(): Pickable? {
        return groupPickable?.pickedItem
    }

    fun resetPickedItem() {
        groupPickable?.pickedItem?.unPick()
        groupPickable?.pickedItem = null
    }

    fun checkTestComplete(isAllChecked: Boolean) {
        if(isAllChecked) {
            (context as TestActivity).showButtonNext()
            groupPickable?.visibility = View.GONE
//            nextTestButton!!.visibility = View.VISIBLE
        }
        else {
            (context as TestActivity).hideButtonNext()
            groupPickable?.visibility = View.VISIBLE
//            nextTestButton!!.visibility = View.GONE
        }
    }
}