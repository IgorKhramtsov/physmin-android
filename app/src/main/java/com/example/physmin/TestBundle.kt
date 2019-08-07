package com.example.physmin

import org.json.JSONObject

data class TestItem(val jsonObject: JSONObject, var nextItem: TestItem? = null)

class TestBundle(array: ArrayList<JSONObject>) {
    var iterator: TestItem? = null
    lateinit var last: TestItem

    init {
        setBundle(array)
    }

    fun setBundle(array: ArrayList<JSONObject>) {
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

    fun pop(): JSONObject {
        if (iterator!!.nextItem == null)
            throw Error("Cant pop, iterator is null. (reach the end of array)")

        iterator = iterator!!.nextItem
        return iterator!!.jsonObject
    }

    fun isMoreTests(): Boolean = iterator?.nextItem != null

    fun isEnd(): Boolean = iterator == null

    fun pushCurrentToBack() {
        if (iterator == null)
            return

        last.nextItem = TestItem(iterator!!.jsonObject)
        last = last.nextItem!!
    }

    fun getAsArray(): Array<JSONObject> {
        if(iterator == null)
            throw Error("iterator is null!")

        val lastPos = iterator
        val list = ArrayList<JSONObject>()
        while(iterator != null) {
            list.add(iterator!!.jsonObject)
            iterator = iterator!!.nextItem
        }
        iterator = lastPos

        return list.toTypedArray()
    }
}