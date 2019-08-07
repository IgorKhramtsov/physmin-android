package com.example.physmin.fragments.tests

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.physmin.Pickable
import com.example.physmin.activities.TestActivity
import com.example.physmin.pickableGroupTag
import com.example.physmin.settableGroupTag
import com.example.physmin.views.layouts.GroupPickable
import com.example.physmin.views.layouts.GroupSettable
import java.lang.Error

interface TestController {
    var settableGroup: GroupSettable
    var pickableGroup: GroupPickable
    var isTestCompleted: Boolean
    var _pickedItem: Pickable?

    fun updateTestStatus()
    fun takePickedItem(): Pickable?
    fun setPickedItem(item: Pickable?)
    fun resetPickedItem()
    fun isAnswersCorrect(): Boolean
}

abstract class FragmentTestBase : Fragment(), TestController {
    abstract var layoutResource: Int

    lateinit var listener: TestCompletingListener
    override lateinit var pickableGroup: GroupPickable
    override lateinit var settableGroup: GroupSettable
    override var isTestCompleted: Boolean = false
    override var _pickedItem: Pickable? = null
        set(value) {
            field?.deselect() // Deselect previous
            field = value
            field?.select() // Select new
        }

    abstract fun onCreateViewEvent(view: View)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context !is TestActivity) throw Error("${this.javaClass.name} can be created only in TestActivity!")
        listener = context
        context.testController = this // TODO: this is bad. Think how to do it better.
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
        if(_pickedItem == item)
            resetPickedItem()
        else
            _pickedItem = item
    }

    override fun resetPickedItem() {
        this._pickedItem = null
    }

    override fun updateTestStatus() {
        val newIsChecked = settableGroup.isAllChecked()
        if(isTestCompleted == newIsChecked)
            return

        isTestCompleted = newIsChecked
        if(isTestCompleted) onTestComplete()
        else onTestCompleteRejected()
    }

    open fun onTestComplete() {
        pickableGroup.visibility = View.GONE

        listener.onTestComplete()
    }

    open fun onTestCompleteRejected() {
        pickableGroup.visibility = View.VISIBLE

        listener.onTestCompleteRejected()
    }

    override fun isAnswersCorrect(): Boolean {
        return settableGroup.isAllCorrect()
    }

    interface TestCompletingListener {
        fun onTestComplete()
        fun onTestCompleteRejected()
    }
}