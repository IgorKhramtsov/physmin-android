package com.example.physmin.dev.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.physmin.R
import com.example.physmin.activities.TestActivity
import kotlinx.android.synthetic.dev.fragment_test_list.view.*
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
            view.test_list.addView(Button(this.context).apply {
                text = "$iter: ${test.getString("type")}"
                setOnClickListener { testActivity.switchTest(test, true) }
            })
        }

        return view
    }

}
