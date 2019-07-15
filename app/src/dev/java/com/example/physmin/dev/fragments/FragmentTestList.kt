package com.example.physmin.dev.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.physmin.R
import com.example.physmin.activities.TestActivity
import kotlinx.android.synthetic.dev.fragment_test_list.view.*


class FragmentTestList: Fragment() {
    lateinit var testActivity: TestActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.testActivity = activity as TestActivity
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_test_list, container, false)

        for ((iter, test) in testActivity.testBundle.withIndex()) {
            view.test_list.addView(Button(this.context).apply {
                text = "$iter: ${test.getString("type")}"
                setOnClickListener { testActivity.switchTest(iter, true) }
            })
        }

        return view
    }
}
