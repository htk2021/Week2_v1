package com.example.week2_v1.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.week2_v1.Addpage_activity
import com.example.week2_v1.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val quit: TextView = findViewById(R.id.quit)
        quit.setOnClickListener {
            val intent = Intent(Setting@ this, ProfileFragment::class.java)
            startActivity(intent)
        }
        val end: TextView = findViewById(R.id.end)
        end.setOnClickListener {
            val intent = Intent(Setting@ this, ProfileFragment::class.java)
            startActivity(intent)
        }
    }
}