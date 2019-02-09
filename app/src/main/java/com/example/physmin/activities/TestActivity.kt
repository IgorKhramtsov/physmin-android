package com.example.physmin.activities

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.physmin.R
import com.example.physmin.fragments.FragmentTestHello
import com.example.physmin.fragments.tests.*

class TestActivity : AppCompatActivity(),
        FragmentTestHello.OnFragmentInteractionListener,
        FragmentTestGraph2State.OnFragmentInteractionListener,
        FragmentTestGraph2Graph2.OnFragmentInteractionListener,
        FragmentTestSign2Relation.OnFragmentInteractionListener,
        FragmentTestGraph2Graph.OnFragmentInteractionListener
{



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
