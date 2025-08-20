/**
 * UI fragment for setting up text size preference.
 *
 * - Yes keeps text size as is and navigates to setup Q13 (happy to proceed) fragment
 * - No increases text size incrementally on each click until max reached then navigates to setup Q13 fragment
 * - Displays guidance text, plays prompts, and wires button/Asr listeners
 * - Logs any adjustment to the text size
 */

package com.example.temiv1.ui.fragments.setup_questions

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.viewmodel.DanceSessionViewModel
import kotlin.math.min

class SetupQ10Fragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q10, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q10)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp) // Keep user's specified text size preference

        fragmentScope.launch {
            delay(1000)
            val sq10 = TtsRequest.create("Is the text big enough?", false)
            robot?.askQuestion(sq10)
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
        findNavController().navigate(R.id.action_setupQ10Fragment_to_setupQ13Fragment) // Navigates to setup Q13 fragment on yes selected
    }

    private fun onNoSelected() {
        sessionViewModel.textSizeSp = min(sessionViewModel.textSizeSp + 2f, 44f) // Sets new text size in view model, 2sp increment each time no selected
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp) // Update text size in UI
        CsvLogger.logEvent("settings", "text_adjust", sessionViewModel.textSizeSp) // Exportable log of text size
        if (sessionViewModel.textSizeSp < 44f) {
            val sq10 = TtsRequest.create("Is the text big enough?", false)
            robot?.askQuestion(sq10)
        } else {
            Toast.makeText(requireContext(), "Maximum text size reached", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_setupQ10Fragment_to_setupQ13Fragment) // Navigates to setup Q13 fragment once max text size reached on no selected
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