package com.example.temiv1.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.temiv1.R
import com.robotemi.sdk.Robot
import com.example.temiv1.analytics.CsvLogger

class MainActivity : AppCompatActivity() {

    // robot declaration
    // robot variable for use in program
    private lateinit var robot: Robot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CsvLogger.init(applicationContext)

        enableEdgeToEdge()
        Log.d("MainActivity", "MainActivity launched")
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
    }
}