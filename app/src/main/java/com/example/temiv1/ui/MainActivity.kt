package com.example.temiv1.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.temiv1.R
import com.robotemi.sdk.Robot

class MainActivity : AppCompatActivity() {

    // robot declaration
    // robot variable for use in program
    private lateinit var robot: Robot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("MainActivity", "MainActivity launched")
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

//        robot = getInstance()
//
//        // yes Button
//        val yesButton: Button = findViewById(R.id.yesButton)
//
//        // no Button
//        val noButton: Button = findViewById(R.id.noButton)
//
//        yesButton.setOnClickListener {
//            val hello = TtsRequest.create("So do I", false)
//            robot.speak(hello)
//            // what happens when the button is clicked
//        }
//        noButton.setOnClickListener {
//            robot.wakeup()
//            // robot. brings up lots of actions
//            // what happens when the button is clicked
//        }
//
//        // Kotlin's version of a thread, firing a thread and delaying it and trying again, better way of doing it addOnRobotReadyListener()
//        val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
//        coroutineScope.launch {
//            delay(1000)
//            val greet = TtsRequest.create("Hello my name is Temi and I'll be finding fun dances for you today. Before we start lets make sure the settings are right for you. Can you hear me?", false)
//            robot.speak(greet)
//        }
    }
}
