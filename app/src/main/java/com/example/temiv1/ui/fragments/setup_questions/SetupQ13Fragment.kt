/**
 * UI fragment for ending setup preferences section and introducing upcoming sections.
 *
 * - Upcoming sections covered: physical capabilities questions, dance move selection, perform dance holding maracas, feedback questions
 * - Displays guidance text, plays prompts, and wires button/Asr listeners
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

class SetupQ13Fragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q13, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q13)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp) // Keep user's specified text size preference

        fragmentScope.launch {
            delay(1000)
            val sq13 = TtsRequest.create("Great your preferences have been set! The following questions will focus on your movement capabilities. Then you will be asked to select some dance moves and a song that will be used to generate a dance video for you to follow. You will be given maracas to hold for the dance to track your movements. After the dance you will be asked some feedback questions. Can you confirm you are happy to proceed?", false)
            robot?.askQuestion(sq13)
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
        CsvLogger.logEvent("answers","setup_q13","yes")
        findNavController().navigate(R.id.action_setupQ13Fragment_to_paQ1Fragment) // Navigate to first physical capability question on yes selected
    }

    private fun onNoSelected() {
        CsvLogger.logEvent("answers","setup_q13","no")
        findNavController().navigate(R.id.action_setupQ13Fragment_to_endSessionFragment) // Navigate to end of session if user not happy to proceed on no selected
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