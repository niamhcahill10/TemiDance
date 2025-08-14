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
import androidx.fragment.app.activityViewModels
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.viewmodel.DanceSessionViewModel

class SetupQ9Fragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q9, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q10)
        sessionViewModel.textSizeSp = 38f
        textView.textSize = sessionViewModel.textSizeSp

        fragmentScope.launch {
            delay(1000)
            val sq9 = TtsRequest.create("Would you like the text bigger?", false)
            robot?.askQuestion(sq9)
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
        sessionViewModel.textSizeSp += 2f
        CsvLogger.logEvent("settings", "text_adjust", sessionViewModel.textSizeSp.toString())
        findNavController().navigate(R.id.action_setupQ9Fragment_to_setupQ10Fragment)
    }

    private fun onNoSelected() {
        findNavController().navigate(R.id.action_setupQ9Fragment_to_setupQ11Fragment)
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