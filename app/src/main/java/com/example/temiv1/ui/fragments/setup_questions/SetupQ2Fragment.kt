package com.example.temiv1.ui.fragments.setup_questions

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
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SetupQ2Fragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentScope.launch {
            delay(1000)
            val sq2 = TtsRequest.create("Is that bright enough?", false)
            robot?.askQuestion(sq2)
        }

        val yesButton: Button = view.findViewById(R.id.yesButton)
        yesButton.setOnClickListener {
            onYesSelected()
        }

        val noButton: Button = view.findViewById(R.id.noButton)
        noButton.setOnClickListener{
            onNoSelected()
        }

        val backButton: ImageButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun onYesSelected() {
        findNavController().navigate(R.id.action_setupQ2Fragment_to_setupQ5Fragment)
    }

    private fun onNoSelected() {

        val contentResolver = requireContext().contentResolver

        val currentBrightness = Settings.System.getInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            125 // fallback default if not set
        )

        val newBrightness = (currentBrightness + 10).coerceAtMost(255) // max 255
        CsvLogger.logEvent("settings", "brightness_adjust", newBrightness.toString())

        Settings.System.putInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            newBrightness
        )

        val q3 = TtsRequest.create("Is that bright enough?", false)
        robot?.askQuestion(q3)
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