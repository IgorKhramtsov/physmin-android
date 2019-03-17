package com.example.physmin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.physmin.R
import kotlinx.android.synthetic.main.activity_testing.*

class TestingActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)



        menuItemView_concept.setOnClickListener {
            var intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
    }
}
