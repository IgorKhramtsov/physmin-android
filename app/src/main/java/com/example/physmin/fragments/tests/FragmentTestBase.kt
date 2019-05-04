package com.example.physmin.fragments.tests

import android.content.Context
import com.example.physmin.views.Layouts.GroupPickable
import com.example.physmin.views.Layouts.GroupSettable

open class FragmentTestBase : androidx.fragment.app.Fragment() {
    lateinit var settableGroup: GroupSettable
    lateinit var pickableGroup: GroupPickable
    lateinit var listener: OnFragmentTestBaseListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnFragmentTestBaseListener)
            listener = context
    }

    interface OnFragmentTestBaseListener {
        fun asd()
    }
}