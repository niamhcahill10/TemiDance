/**
 * UI fragment for setting up brightness preference.
 *
 * - Yes decreases brightness one increment and navigates to brightness decrease fragment
 * - No moves to volume settings fragment
 * - Displays guidance text, plays prompts, and wires button/Asr listeners
 * - Logs brightness if chosen to decrease from initial setting
 */

package com.example.temiv1.ui.fragments.setup_questions

import android.os.Bundle
import android.provider.Settings
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

class SetupQ3Fragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentScope.launch {
            delay(1000)
            val sq3 = TtsRequest.create("Would you like the screen darker?", false)
            robot?.askQuestion(sq3)
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

        val contentResolver = requireContext().contentResolver

        val currentBrightness = Settings.System.getInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            191 // fallback default if not set
        )

        val newBrightness = (currentBrightness - 16).coerceAtLeast(127) // Sets new level of brightness, 16 point increment (decreasing) each time yes selected
        CsvLogger.logEvent("settings", "brightness_adjust", newBrightness) // Exportable log of first brightness decrease

        // Decreases brightness on yes selected or recognised via speech
        Settings.System.putInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            newBrightness)

        findNavController().navigate(R.id.action_setupQ3Fragment_to_setupQ4Fragment) // Navigates to fragment that allows further decrease in brightness on yes selected
    }

    private fun onNoSelected() {
        findNavController().navigate(R.id.action_setupQ3Fragment_to_setupQ5Fragment) // Navigates to volume preferences fragment on no selected
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