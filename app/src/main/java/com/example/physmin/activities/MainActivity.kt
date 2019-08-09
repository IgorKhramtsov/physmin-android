package com.example.physmin.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.physmin.R
import com.example.physmin.fragments.FragmentSubjectKinematic
import kotlinx.android.synthetic.main.activity_testing.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests_subjects)

        menuItemView_concept.onTestButtonClick = {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }

        menuItemView_rotation.isDisabled = true
        menuItemView_graphics.isDisabled = true
    }
}
