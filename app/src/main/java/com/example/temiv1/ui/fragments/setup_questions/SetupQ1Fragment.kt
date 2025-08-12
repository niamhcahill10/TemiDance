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
            val q2 = TtsRequest.create("Would you like the screen brighter?", false)
            robot?.askQuestion(q2)
        }

        val yesButton: Button = view.findViewById(R.id.yesButton)
        yesButton.setOnClickListener {
            onYesSelected()
        }

        val noButton: Button = view.findViewById(R.id.noButton)
        noButton.setOnClickListener {
            onNoSelected()
        }

//        val backButton: ImageButton = view.findViewById(R.id.backButton)
//        backButton.setOnClickListener {
//            onBackSelected()
//        }

    }

    private fun onYesSelected() {
        val contentResolver = requireContext().contentResolver

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
        findNavController().navigate(R.id.action_setupQ1Fragment_to_setupQ3Fragment)
    }

//    private fun onBackSelected() {
//        findNavController().popBackStack()
//    }

    override fun handleAsr(command: String) {
        if (!isTemiDevice) return
        when (command) {
            "yes" -> onYesSelected()
            "no" -> onNoSelected()
            else -> robot?.askQuestion(TtsRequest.create("Please say yes or no.", false))
        }
    }
}