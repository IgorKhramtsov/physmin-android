package com.example.physmin.views.layouts

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import com.example.physmin.R
import com.example.physmin.Settable
import com.example.physmin.activities.FunctionAnswerRelationSignParcelable
import com.example.physmin.activities.FunctionParcelable
import com.example.physmin.views.*
import com.example.physmin.views.items.ImageViewSettable
import com.example.physmin.views.items.ImageViewSettableBlank
import com.example.physmin.views.items.RelationSignView
import java.util.*
import kotlin.math.max


class GroupSettable(context: Context, attributeSet: AttributeSet?): BaseGroup(context, attributeSet),
        ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    lateinit var parentTestConstraintLayout: TestConstraintLayout

    init {
        setOnHierarchyChangeListener(this)
    }

    fun addRelationSignView(answer: FunctionAnswerRelationSignParcelable) {
        val relationSignView = RelationSignView(this.context!!, null, answer.letter,
                answer.leftIndex, answer.rightIndex)
        relationSignView.correctAnswers = answer.correctSign
        this.addView(relationSignView)
    }

    fun addQuestionGraphic(functions: ArrayList<FunctionParcelable>) {
        val graphView = GraphView(context, null)
        graphView.functions = functions
        graphView.layoutParams = LayoutParams(140.dpToPx().toInt(), 85.dpToPx().toInt())

        this.addView(graphView)
    }

    fun addViewSettable(correctIds: IntArray, functions: ArrayList<FunctionParcelable>) {
        val questView = ImageViewSettable(this.context, null).apply {
            correctAnswers = correctIds
            layoutParams = LayoutParams(140.dpToPx().toInt(), 85.dpToPx().toInt())
            graph.functions = functions
        }
        this.addView(questView)
    }

    fun addQuestionBlankView(correctAnswers: IntArray) {
        val imageViewSettableBlank = ImageViewSettableBlank(context, null)
        imageViewSettableBlank.correctAnswers = correctAnswers
        imageViewSettableBlank.layoutParams = LayoutParams(140.dpToPx().toInt(), 85.dpToPx().toInt())

        this.addView(imageViewSettableBlank)
    }

    fun setParent(parent: TestConstraintLayout) {
        this.parentTestConstraintLayout = parent
    }

    override fun onChildViewAdded(parent: View?, child: View) {
        when (child) {
            is Settable -> {
                child.setParent(this)
                if (child !is RelationSignView)
                    child.setOnClickListener(this)
            }
        }
    }

    override fun onChildViewRemoved(parent: View?, child: View?) {}

    override fun onClick(clickedChild: View?) {
        if (clickedChild !is Settable) return

        clickedChild.answerView = parentTestConstraintLayout.takePickedItem()
    }

    fun isAllChecked(): Boolean {
        var child: View
        for (i in 0 until this.childCount) {
            child = getChildAt(i)
            if (child is Settable)
                if (child.answerView == null)
                    return false
        }
        return true
    }
}