package com.physmin.android.views.layouts

import com.physmin.android.FunctionParcelable
import com.physmin.android.RSAnswerParcelable
import android.content.Context
import android.util.AttributeSet
import android.view.*
import com.physmin.android.Settable
import com.physmin.android.fragments.tasks.TaskController
import com.physmin.android.settableGroupTag
import com.physmin.android.views.*
import com.physmin.android.views.items.ImageViewSettable
import com.physmin.android.views.items.ImageViewSettableBlank
import com.physmin.android.views.items.RelationSignView
import kotlin.collections.ArrayList


class GroupSettable(context: Context, attributeSet: AttributeSet?): BaseGroup(context, attributeSet),
        ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    lateinit var controller: TaskController

    init {
        tag = settableGroupTag
        setOnHierarchyChangeListener(this)
    }

    fun addRelationSignView(answer: RSAnswerParcelable, graphView: GraphView? = null) {
        val relationSignView = RelationSignView(this.context!!, null, answer.letter,
                answer.leftSegment, answer.rightSegment).apply {

            this.correctAnswers = answer.correctSign
            this.graphView = graphView
        }

        this.addView(relationSignView)
    }

    fun addQuestionGraphic(functions: ArrayList<FunctionParcelable>) {
        val graphView = GraphView(context, null).apply {
            this.functions = functions
        }
        val layoutParams = LayoutParams(140.dpToPx().toInt(), 85.dpToPx().toInt())

        this.addView(graphView, layoutParams)
    }

    fun addViewSettable(correctIds: IntArray, functions: ArrayList<FunctionParcelable>) {
        val questView = ImageViewSettable(this.context, null).apply {
            correctAnswers = correctIds
            graph.functions = functions
        }

        val layoutParams = LayoutParams(140.dpToPx().toInt(), 85.dpToPx().toInt())
        this.addView(questView, layoutParams)
    }

    fun addQuestionBlankView(correctAnswers: IntArray) {
        val imageViewSettableBlank = ImageViewSettableBlank(context, null).apply {
            this.correctAnswers = correctAnswers
        }

        val layoutParams = LayoutParams(140.dpToPx().toInt(), 85.dpToPx().toInt())
        this.addView(imageViewSettableBlank, layoutParams)
    }

    fun setTestController(controller: TaskController) {
        this.controller = controller
    }

    override fun onChildViewAdded(parent: View?, child: View) {
        if (child is Settable) {
            child.setTestController(controller)
            if (child !is RelationSignView)
                child.setOnClickListener(this)
        }
    }

    override fun onChildViewRemoved(parent: View?, child: View?) {}

    override fun onClick(clickedChild: View?) {
        if (clickedChild !is Settable) return

        clickedChild.answerView = controller.takePickedItem()
    }

    fun isAllChecked(): Boolean {
        var child: View
        for (i in 0 until this.childCount) {
            child = getChildAt(i)
            if (child is Settable && child.answerView == null)
                return false
        }

        return true
    }

    fun isAllCorrect(): Boolean {
        var child: View
        for (i in 0 until childCount) {
            child = getChildAt(i)
            if (child is Settable && !child.isCorrect())
                return false
        }

        return true
    }
}