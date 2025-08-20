/**
 * UI fragment for setting up volume preference.
 *
 * - Yes decreases volume one increment and navigates to volume decrease fragment
 * - No moves to text size preference fragment
 * - Displays guidance text, plays prompts, and wires button/Asr listeners
 * - Logs volume if chosen to decrease from initial setting
 */

package com.example.temiv1.ui.fragments.setup_questions

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SetupQ7Fragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q7, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentScope.launch {
            delay(1000)
            val sq7 = TtsRequest.create("Would you like the volume quieter?", false)
            robot?.askQuestion(sq7)
        }

        val yesButton: Button = view.findViewById(R.id.yesButton)
        yesButton.setOnClickListener {
            onYesSelected()
        }

        val noButton: Button = view.findViewById(R.id.noButton)
        noButton.setOnClickListener{
            onNoSelected()
        }
    }

    private fun onYesSelected() {
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        val newVolume = (currentVolume - 1).coerceAtLeast(0) // Sets new volume level, 1 point increment (decreasing)
        CsvLogger.logEvent("settings", "volume_adjust", newVolume) // Exportable log of first volume decrease

        // Decreases volume on yes selected or recognised via speech
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            newVolume,
            0
        )
        findNavController().navigate(R.id.action_setupQ7Fragment_to_setupQ8Fragment) // Navigates to fragment that allows further decrease in volume on yes selected
    }

    private fun onNoSelected() {
        findNavController().navigate(R.id.action_setupQ7Fragment_to_setupQ9Fragment) // Navigates to text size preference fragment on no selected
    }

    // Speech recognition for yes / no answers
    override fun handleAsr(command: String) {
        if (!isTemiDevice) return
        when (command) {
            "yes" -> onYesSelected()
            "no" -> onNoSelected()
            else -> robot?.askQuestion(TtsRequest.create("Please say yes or no.", false))
        }
    }
}