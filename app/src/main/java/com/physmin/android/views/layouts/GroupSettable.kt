package com.physmin.android.views.layouts

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import com.physmin.android.R
import com.physmin.android.Settable
import com.physmin.android.activities.FunctionAnswerRelationSignParcelable
import com.physmin.android.activities.FunctionParcelable
import com.physmin.android.fragments.tests.TestController
import com.physmin.android.settableGroupTag
import com.physmin.android.views.*
import com.physmin.android.views.items.ImageViewSettable
import com.physmin.android.views.items.ImageViewSettableBlank
import com.physmin.android.views.items.RelationSignView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max


class GroupSettable(context: Context, attributeSet: AttributeSet?): BaseGroup(context, attributeSet),
        ViewGroup.OnHierarchyChangeListener, View.OnClickListener {

    lateinit var controller: TestController

    init {
        tag = settableGroupTag
        setOnHierarchyChangeListener(this)
    }

    fun addRelationSignView(answer: FunctionAnswerRelationSignParcelable, graphView: GraphView? = null) {
        val relationSignView = RelationSignView(this.context!!, null, answer.letter,
                answer.leftIndex, answer.rightIndex)
        relationSignView.correctAnswers = answer.correctSign
        relationSignView.graphView = graphView
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

    fun setTestController(controller: TestController) {
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