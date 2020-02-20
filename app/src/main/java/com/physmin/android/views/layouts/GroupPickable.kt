package com.physmin.android.views.layouts

import FunctionAnswerParcelable
import TextAnswerParcelable
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.physmin.android.Pickable
import com.physmin.android.fragments.tasks.TestController
import com.physmin.android.pickableGroupTag
import com.physmin.android.views.*
import com.physmin.android.views.items.ImageViewPickable
import com.physmin.android.views.items.TextViewPickable

class GroupPickable(context: Context, attrs: AttributeSet?): GroupScrollable(context, attrs),
        ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    lateinit var controller: TestController

    init {
        layoutType = TWO_COLUMNS
        tag = pickableGroupTag
        setOnHierarchyChangeListener(this)
    }

    fun setTestController(controller: TestController) {
        this.controller = controller
    }

    fun addImageViewPickable(answerParcelable: FunctionAnswerParcelable, isCorr: Boolean? = null) {
        val answerPic = ImageViewPickable(context, null).apply {
            layoutParams = LayoutParams(150.dpToPx().toInt(), 90.dpToPx().toInt())
            graph.functions = answerParcelable.functions
            answer = answerParcelable.id
            isCorrect = isCorr.toString()
        }

        this.addView(answerPic)
    }

    fun addTextViewPickable(answerParcelable: TextAnswerParcelable, correctIds: IntArray) {
        val textView = TextViewPickable(this.context!!, null).apply {
            setPadding(6.dpToPx().toInt(), 3.dpToPx().toInt(), 6.dpToPx().toInt(), 3.dpToPx().toInt())
            answer = answerParcelable.id
            layoutParams = LayoutParams(150.dpToPx().toInt(), LayoutParams.WRAP_CONTENT)
            text = answerParcelable.text
            isCorrect = correctIds.joinToString()
        }
        this.addView(textView)
    }

    override fun onClick(item: View?) {
        if (item is Pickable)
            controller.setPickedItem(item)
    }

    override fun onChildViewAdded(parent: View?, child: View) {
        if(child is Pickable) {
            child.setOnClickListener(this)
            child.setTestController(controller)
        }
    }

    override fun onChildViewRemoved(parent: View?, child: View?) { }
}
