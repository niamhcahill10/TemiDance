/**
 * UI fragment for setting up volume preference.
 *
 * - Yes keeps volume as is and navigates to text size preference fragment
 * - No decreases volume incrementally on each click until min reached then navigates to text size preference fragment
 * - Displays guidance text, plays prompts, and wires button/Asr listeners
 * - Logs any adjustment to the volume
 */

package com.example.temiv1.ui.fragments.setup_questions

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SetupQ8Fragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q8, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentScope.launch {
            delay(1000)
            val sq8 = TtsRequest.create("Is that quiet enough?", false)
            robot?.askQuestion(sq8)
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
        findNavController().navigate(R.id.action_setupQ8Fragment_to_setupQ9Fragment) // Navigates to text size preferences fragment on yes selected
    }

    private fun onNoSelected() {
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        val newVolume = (currentVolume - 1).coerceAtLeast(0) // Sets new volume level, 1 point increment (decreasing) each time no selected
        CsvLogger.logEvent("settings", "volume_adjust", newVolume) // Exportable log of volume level

        // Decreases volume on no selected or recognised via speech
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            newVolume,
            0
        )

        if (newVolume != 0) {
            val sq8 = TtsRequest.create("Is that quiet enough?", false)
            robot?.askQuestion(sq8)
        } else {
            Toast.makeText(requireContext(), "Robot muted", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_setupQ8Fragment_to_setupQ9Fragment) // Navigates to text size preferences fragment once min volume (muted) reached on no selected
        }
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