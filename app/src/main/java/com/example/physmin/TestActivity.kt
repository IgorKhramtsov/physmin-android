package com.example.physmin

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class TestActivity : AppCompatActivity(),
        FragmentTest_hello.OnFragmentInteractionListener,
        TestFragment_graph_state.OnFragmentInteractionListener,
        TestFragment_graph_graph2.OnFragmentInteractionListener,
        TestFramgent_relation_signs.OnFragmentInteractionListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
