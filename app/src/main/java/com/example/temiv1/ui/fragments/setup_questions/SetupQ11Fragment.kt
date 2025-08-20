/**
 * UI fragment for setting up text size preference.
 *
 * - Yes decreases text size one increment and navigates to text size decrease fragment
 * - No moves to setup Q13 (happy to proceed) fragment
 * - Displays guidance text, plays prompts, and wires button/Asr listeners
 * - Logs text size if chosen to decrease from initial setting
 */

package com.example.temiv1.ui.fragments.setup_questions

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.viewmodel.DanceSessionViewModel
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SetupQ11Fragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q11, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q11)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp) // Keep user's specified text size preference

        fragmentScope.launch {
            delay(1000)
            val sq11 = TtsRequest.create("Would you like the text smaller?", false)
            robot?.askQuestion(sq11)
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
        sessionViewModel.textSizeSp -= 2f // Decreases text size in view model by 2sp
        CsvLogger.logEvent("settings", "text_adjust", sessionViewModel.textSizeSp) // Exportable log of first text size decrease
        findNavController().navigate(R.id.action_setupQ11Fragment_to_setupQ12Fragment)
    }

    private fun onNoSelected() {
        findNavController().navigate(R.id.action_setupQ11Fragment_to_setupQ13Fragment) // Navigates to setup Q13 fragment on no selected
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