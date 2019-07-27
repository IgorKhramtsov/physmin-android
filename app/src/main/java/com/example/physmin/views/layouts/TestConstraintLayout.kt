package com.example.physmin.views.layouts

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.Pickable
import com.example.physmin.R
import com.example.physmin.Settable
import com.example.physmin.activities.TestActivity
import com.example.physmin.views.ProgressBarView

class TestConstraintLayout(context: Context, attributeSet: AttributeSet): ConstraintLayout(context, attributeSet),
        ViewGroup.OnHierarchyChangeListener {

    lateinit var groupPickable: GroupPickable
    lateinit var groupSettable: GroupSettable
    var nextTestButton: Button? = null

    init {
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.ui_background, null))

        setOnHierarchyChangeListener(this)
        if(!isInEditMode) {
            (context as TestActivity).testConstraintLayout = this
        }
    }

    override fun onChildViewAdded(p0: View?, child: View) {
        when (child) {
            is GroupPickable -> {
                child.setParent(this)
                groupPickable = child
            }
            is GroupSettable -> {
                child.setParent(this)
                groupSettable = child
            }
            is Button -> nextTestButton = child
        }
    }

    override fun onChildViewRemoved(p0: View?, p1: View?) {}

    fun takePickedItem(): Pickable? {
        val item = groupPickable.pickedItem
        groupPickable.resetPickedItem()
        return item
    }

    fun checkTestComplete() {
        if (groupSettable.isAllChecked()) {
            (context as TestActivity).showButtonNext()

            groupPickable.visibility = View.GONE
        } else {
            (context as TestActivity).hideButtonNext()
            groupPickable.visibility = View.VISIBLE
        }
    }

    public fun isAnswersCorrect(): Boolean {
        var child: View
        for (i in 0 until this.groupSettable.childCount) {
            child = this.groupSettable.getChildAt(i)
            if (child is Settable) {
                Log.i("TestConstraintLayout", "isCorrect - " + child.isCorrect().toString())
                if (!child.isCorrect())
                    return false
            }
        }
        return true
    }
}