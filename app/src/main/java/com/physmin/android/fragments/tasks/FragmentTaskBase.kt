package com.physmin.android.fragments.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.physmin.android.Pickable
import com.physmin.android.activities.TaskPlayerActivity
import com.physmin.android.pickableGroupTag
import com.physmin.android.settableGroupTag
import com.physmin.android.views.layouts.GroupPickable
import com.physmin.android.views.layouts.GroupSettable
import java.lang.Error

interface TaskController {
    var taskId: Int
    var taskType: String

    var settableGroup: GroupSettable
    var pickableGroup: GroupPickable
    var isTaskCompleted: Boolean
    var _pickedItem: Pickable?

    fun updateTaskStatus()
    fun takePickedItem(): Pickable?
    fun setPickedItem(item: Pickable?)
    fun resetPickedItem()
    fun isAnswersCorrect(): Boolean
    fun getAnswers(): HashMap<Int, Int>
}

abstract class FragmentTaskBase: Fragment(), TaskController {
    abstract var layoutResource: Int
    override var taskId: Int = 0

    lateinit var listener: TestCompletingListener
    override lateinit var pickableGroup: GroupPickable
    override lateinit var settableGroup: GroupSettable
    override var isTaskCompleted: Boolean = false
        set(value) {
            if (field == value)
                return

            if (value) onTestComplete()
            else onTestCompleteRejected()
            field = value
        }
    override var _pickedItem: Pickable? = null
        set(value) {
            field?.deselect() // Deselect previous
            field = value
            field?.select() // Select new
        }

    abstract fun onCreateViewEvent(view: View)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context !is TaskPlayerActivity) throw Error("${this.javaClass.name} can be created only in TestActivity!")
        listener = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews() // Fragment overlapping fix.

        val view: View = inflater.inflate(layoutResource, container, false)
        settableGroup = view.findViewWithTag(settableGroupTag)
        settableGroup.setTestController(this)

        pickableGroup = view.findViewWithTag(pickableGroupTag)
        pickableGroup.setTestController(this)

        onCreateViewEvent(view)

        pickableGroup.setHorizontalOrVertical(false)
                .setStartEndScroll(true)
                .setDuration(300)
                .setInvalidate()
        return view
    }

    override fun takePickedItem(): Pickable? {
        val item = _pickedItem
        resetPickedItem()
        return item
    }

    override fun setPickedItem(item: Pickable?) {
        if (_pickedItem == item)
            resetPickedItem()
        else
            _pickedItem = item
    }

    override fun resetPickedItem() {
        this._pickedItem = null
    }

    override fun updateTaskStatus() {
        isTaskCompleted = settableGroup.isAllChecked()
    }

    open fun onTestComplete() {
        pickableGroup.visibility = View.GONE

        listener.onTaskComplete()
    }

    open fun onTestCompleteRejected() {
        pickableGroup.visibility = View.VISIBLE

        listener.onTaskCompleteRejected()
    }

    override fun isAnswersCorrect(): Boolean {
        return settableGroup.isAllCorrect()
    }

    override fun getAnswers(): HashMap<Int, Int> {
        return settableGroup.getAnswers()
    }

    interface TestCompletingListener {
        // When user set last answer
        fun onTaskComplete()
        // When user have all answers setted, and unset one
        fun onTaskCompleteRejected()
    }
}