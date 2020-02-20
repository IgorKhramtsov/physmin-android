package com.physmin.android.dev.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.physmin.android.R
import com.physmin.android.TaskObject
import com.physmin.android.activities.TestActivity
import kotlinx.android.synthetic.dev.fragment_test_list.view.*
import org.json.JSONObject


class FragmentTestList(_testsList: Array<TaskObject>): Fragment() {
    lateinit var testActivity: TestActivity
    var testsList = _testsList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.testActivity = activity as TestActivity
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        container?.removeAllViews()
        val view = inflater.inflate(R.layout.fragment_test_list, container, false)

        for ((iter, task) in testsList.withIndex()) {
            view.test_list.addView(Button(this.context).apply {
                text = "$iter: ${task["type"]}"
                setOnClickListener { testActivity.switchTest(task, true) }
            })
        }

        return view
    }

}
