package com.example.physmin.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.physmin.R
import kotlinx.android.synthetic.main.activity_testing.*

class TestingActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)



        menuItemView_concept.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            intent.putExtra("GetTestFunctionName", "getTestDev")
            startActivity(intent)
        }
    }
}
