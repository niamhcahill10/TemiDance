/**
 * UI Main Activity for hosting fragments.
 *
 * - Allows the fragments to be displayed
 * - Initialises the global logger
 */

package com.example.temiv1.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialise global logger (used by all fragments)
        CsvLogger.init(applicationContext)

        // Configure window to draw edge-to-edge (behind status/nav bars)
        enableEdgeToEdge()

        Log.d("MainActivity", "MainActivity launched") // Debugging log

        // Load the activity's main layout (hosts fragments)
        setContentView(R.layout.activity_main)

        // Standard setup logic so fragment UI doesn't overlap status or navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
    }
}