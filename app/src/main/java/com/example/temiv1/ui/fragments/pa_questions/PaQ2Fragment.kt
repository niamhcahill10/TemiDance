/**
 * UI fragment for asking physical capabilities question.
 *
 * - Yes required for dance participation, no will end session
 * - Displays guidance text, plays prompts, and wires button/Asr listeners
 * - Logs user interactions (clicks/Asr) and answer
 */

package com.example.temiv1.ui.fragments.pa_questions

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

class PaQ2Fragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pa_q2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q2)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp) // Keep user's specified text size preference

        fragmentScope.launch {
            delay(1000)
            val paq2 = TtsRequest.create("Are you able to sit-up in bed from a lying position without any pain or discomfort?", false)
            robot?.askQuestion(paq2)
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
        CsvLogger.logEvent("answers","pa_q2","yes") // Exportable log of answer
        findNavController().navigate(R.id.action_paQ2Fragment_to_paQ3Fragment) // Navigate to next question fragment on yes selected
    }

    private fun onNoSelected() {
        CsvLogger.logEvent("answers","pa_q2","no")
        findNavController().navigate(R.id.action_paQ2Fragment_to_endSessionFragment) // Navigate to end of session on no selected
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