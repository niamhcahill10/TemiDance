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

        textView = view.findViewById(R.id.q11)
        textView.textSize = sessionViewModel.textSizeSp

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
        findNavController().navigate(R.id.action_setupQ10Fragment_to_setupQ13Fragment)
    }

    private fun onNoSelected() {
        sessionViewModel.textSizeSp = min(sessionViewModel.textSizeSp + 2f, 44f)
        textView.textSize = sessionViewModel.textSizeSp
        CsvLogger.logEvent("settings", "text_adjust", sessionViewModel.textSizeSp)
        if (sessionViewModel.textSizeSp < 44f) {
            val sq10 = TtsRequest.create("Is the text big enough?", false)
            robot?.askQuestion(sq10)
        } else {
            Toast.makeText(requireContext(), "Maximum text size reached", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_setupQ10Fragment_to_setupQ13Fragment)
        }

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