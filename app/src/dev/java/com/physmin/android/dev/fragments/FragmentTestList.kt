package com.physmin.android.dev.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.physmin.android.R
import com.physmin.android.activities.TestActivity
import org.json.JSONObject


class FragmentTestList(_testsList: Array<JSONObject>): Fragment() {
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

        for ((iter, test) in testsList.withIndex()) {
            val listView = view.findViewById<LinearLayout>(R.id.test_list)
            listView.addView(Button(this.context).apply {
                text = "$iter: ${test.getString("type")}"
                setOnClickListener { testActivity.switchTest(test, true) }
            })
        }

        return view
    }

}
