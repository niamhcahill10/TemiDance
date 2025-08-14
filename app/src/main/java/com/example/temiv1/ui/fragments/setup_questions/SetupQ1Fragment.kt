package com.example.temiv1.ui.fragments.setup_questions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.provider.Settings
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment

class SetupQ1Fragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentScope.launch {
            delay(1000)
            val sq1 = TtsRequest.create("The first few questions will get the brightness, volume, and text size adjusted to your preferences. Would you like the screen brighter?", false)
            robot?.askQuestion(sq1)
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

        CsvLogger.logEvent("answers","setup_q1","yes")

        val currentBrightness = Settings.System.getInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            125 // fallback default if not set
        )

        val newBrightness = (currentBrightness + 10).coerceAtMost(255) // max 255

        Settings.System.putInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            newBrightness
        )
        findNavController().navigate(R.id.action_setupQ1Fragment_to_setupQ2Fragment)
    }

    private fun onNoSelected() {
        CsvLogger.logEvent("answers","setup_q1","no")
        findNavController().navigate(R.id.action_setupQ1Fragment_to_setupQ3Fragment)
    }

    override fun handleAsr(command: String) {
        if (!isTemiDevice) return
        when (command) {
            "yes" -> onYesSelected()
            "no" -> onNoSelected()
            else -> robot?.askQuestion(TtsRequest.create("Please say yes or no.", false))
        }
    }
}