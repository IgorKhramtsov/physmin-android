package com.example.physmin.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.physmin.R
import com.example.physmin.fragments.FragmentSubjectKinematic
import com.example.physmin.fragments.FragmentSubjects
import kotlinx.android.synthetic.main.activity_testing.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity(), FragmentSubjects.OnFragmentInteractionListener, FragmentSubjectKinematic.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests_subjects)

        menuItemView_concept.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
