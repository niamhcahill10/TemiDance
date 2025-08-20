/**
 * UI fragment for setting up brightness preference.
 *
 * - Yes keeps brightness as is and navigates to volume preference fragment
 * - No decreases brightness incrementally on each click until min reached then navigates to volume preference fragment
 * - Displays guidance text, plays prompts, and wires button/Asr listeners
 * - Logs any adjustment to the brightness
 */

package com.example.temiv1.ui.fragments.setup_questions

import android.os.Bundle
import android.provider.Settings
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

class SetupQ4Fragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentScope.launch {
            delay(1000)
            val sq4 = TtsRequest.create("Is that dark enough?", false)
            robot?.askQuestion(sq4)
        }

        val yesButton: Button = view.findViewById(R.id.yesButton)
        yesButton.setOnClickListener {
            onYesSelected()
        }

        val noButton: Button = view.findViewById(R.id.noButton)
        noButton.setOnClickListener {
            onNoSelected()
        }
    }

    private fun onYesSelected() {
        findNavController().navigate(R.id.action_setupQ4Fragment_to_setupQ5Fragment) // Navigates to volume preferences fragment on yes selected
    }

    private fun onNoSelected() {

        val contentResolver = requireContext().contentResolver

        val currentBrightness = Settings.System.getInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            191 // fallback default if not set
        )

        val newBrightness = (currentBrightness - 16).coerceAtLeast(127) // Sets new level of brightness, 16 point increment (decreasing) each time no selected
        CsvLogger.logEvent("settings", "brightness_adjust", newBrightness) // Exportable log of brightness level

        // Decreases brightness on no selected or recognised via speech
        Settings.System.putInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            newBrightness)

        if (newBrightness != 127) {
            val sq4 = TtsRequest.create("Is that dark enough?", false)
            robot?.askQuestion(sq4)
        } else {
            Toast.makeText(requireContext(), "Minimum brightness reached", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_setupQ4Fragment_to_setupQ5Fragment) // Navigates to volume preferences fragment once min brightness reached on no selected
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