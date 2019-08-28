package com.physmin.android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.physmin.android.R
import kotlinx.android.synthetic.main.activity_testing.*

class TestingActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        menuItemView_concepts.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
    }
}
