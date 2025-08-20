/**
 * UI fragment for setting up volume preference.
 *
 * - Yes keeps volume as is and navigates to text size preference fragment
 * - No increases volume incrementally on each click until max reached then navigates to text size preference fragment
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

class SetupQ6Fragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q6, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentScope.launch {
            delay(1000)
            val sq6 = TtsRequest.create("Is that loud enough?", false)
            robot?.askQuestion(sq6)
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
        findNavController().navigate(R.id.action_setupQ6Fragment_to_setupQ9Fragment) // Navigates to text size preferences fragment on yes selected
    }

    private fun onNoSelected() {
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        val newVolume = (currentVolume + 1).coerceAtMost(maxVolume) // Sets new volume level, 1 point increment each time no selected
        CsvLogger.logEvent("settings", "volume_adjust", newVolume) // Exportable log of volume level

        // Increases volume on no selected or recognised via speech
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            newVolume,
            0
        )

        if (newVolume < maxVolume) {
            val sq6 = TtsRequest.create("Is that loud enough?", false)
            robot?.askQuestion(sq6)
        } else {
            Toast.makeText(requireContext(), "Maximum volume reached", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_setupQ6Fragment_to_setupQ9Fragment) // Navigates to text size preferences fragment once max volume reached on no selected
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