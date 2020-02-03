package com.physmin.android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.physmin.android.R
import kotlinx.android.synthetic.main.activity_tests_subjects.*

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests_subjects)

        menuItemView_progressive_concepts.setAction("Тестирование") {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
        menuItemView_progressive_concepts.setAction("Обучение") {}
    }
}
