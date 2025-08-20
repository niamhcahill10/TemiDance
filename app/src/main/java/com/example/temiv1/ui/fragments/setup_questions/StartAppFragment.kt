/**
 * UI fragment for starting the app.
 *
 * - Displays 'Let's Dance' start button and navigates to SetupIntroFragment on click
 * - Adjusts and logs the start volume and brightness settings
 */

package com.example.temiv1.ui.fragments.setup_questions

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment


class StartAppFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide back button as first fragment
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        backButton.visibility = View.INVISIBLE

        // If connected to a robot (not in simulation) set the brightness and volume
        if (isTemiDevice) {
            val contentResolver = requireContext().contentResolver

            val newBrightness = (255 * 0.75).toInt() // Brightness set to 75% of max
            Settings.System.putInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                newBrightness
            )
            CsvLogger.logEvent("settings", "brightness_set", newBrightness) // Exportable log of initial brightness setting

            val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val newVolume = (maxVolume * 0.4).toInt().coerceAtMost(maxVolume) // Volume set to 40% of max
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                newVolume,
                0
            )
            CsvLogger.logEvent("settings", "volume_set", newVolume) // Exportable log of initial volume setting
        }

        val startButton: Button = view.findViewById(R.id.startButton)
        startButton.setOnClickListener {
            findNavController().navigate(R.id.action_startAppFragment_to_setupIntroFragment) // Navigate to intro fragment on start button click
        }
    }
}
