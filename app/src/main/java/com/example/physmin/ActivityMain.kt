package com.example.physmin

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ActivityMain : AppCompatActivity(), FragmentSubjects.OnFragmentInteractionListener, FragmentSubjectKinematic.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests_subjects)
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
