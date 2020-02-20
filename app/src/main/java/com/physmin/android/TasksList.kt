package com.physmin.android

import org.json.JSONObject

typealias TaskObject = HashMap<String, *>
data class TestItem(val taskObject: TaskObject, var nextItem: TestItem? = null)

class TasksList(array: ArrayList<HashMap<String, *>>) {
    var iterator: TestItem? = null
    lateinit var last: TestItem

    init {
        setTasks(array)
    }

    private fun setTasks(array: ArrayList<HashMap<String, *>>) {
        if (array.count() == 0)
            throw Error("Array is empty!")

        // The first element will be duplicated, to pop/push work correctly
        iterator = TestItem(array.first())
        val first = iterator!!
        array.forEach {
            iterator!!.nextItem = TestItem(it)
            iterator = iterator!!.nextItem!!
        }
        last = iterator!!
        iterator = first
    }

    fun pop(): TaskObject {
        if (iterator!!.nextItem == null)
            throw Error("Cant pop, iterator is null. (reach the end of array)")

        iterator = iterator!!.nextItem
        return iterator!!.taskObject
    }

    fun isMoreTests(): Boolean = iterator?.nextItem != null

    fun isEnd(): Boolean = iterator?.nextItem == null

    fun pushCurrentToBack() {
        if (iterator == null)
            return

        last.nextItem = TestItem(iterator!!.taskObject)
        last = last.nextItem!!
    }

    fun getAsArray(): Array<TaskObject> {
        if (iterator == null)
            throw Error("iterator is null!")

        val lastPos = iterator
        val list = ArrayList<TaskObject>()
        while (iterator != null) {
            list.add(iterator!!.taskObject)
            iterator = iterator!!.nextItem
        }
        iterator = lastPos

        return list.toTypedArray()
    }
}