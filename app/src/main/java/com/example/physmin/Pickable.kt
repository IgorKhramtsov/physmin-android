package com.example.physmin

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.example.physmin.views.GroupPickable


// TODO: Make it class??
abstract class Pickable(context: Context, attrs: AttributeSet?): View(context, attrs), View.OnLongClickListener {

    abstract var picked: Boolean
    abstract var par: GroupPickable?
    abstract var answer: Int

    abstract fun setParent(_parent: GroupPickable)

    abstract fun isPicked() : Boolean

    abstract fun pick()
    abstract fun unPick()



    override fun onLongClick(v: View): Boolean { // TODO: DOESNT WORK
        val data = ClipData.newPlainText("", "")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            v.startDragAndDrop(data, View.DragShadowBuilder(v), null,  0)
        else
            v.startDrag(data, View.DragShadowBuilder(v), null,  0)
        return true
    }
}