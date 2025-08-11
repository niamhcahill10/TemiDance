package com.example.temiv1.ui.fragments.setup_questions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.viewmodel.DanceSessionViewModel
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

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
        textView.textSize = sessionViewModel.textSizeSp

        fragmentScope.launch {
            delay(1000)
            val q13 = TtsRequest.create("Is the text small enough?", false)
            robot?.askQuestion(q13)
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
        findNavController().navigate(R.id.action_q13Fragment_to_q14Fragment)
    }

    private fun onNoSelected() {
        sessionViewModel.textSizeSp = max(sessionViewModel.textSizeSp - 2f, 32f)
        textView.textSize = sessionViewModel.textSizeSp
        if (sessionViewModel.textSizeSp > 32f) {
            robot?.askQuestion("Is the text small enough?")
        } else {
            Toast.makeText(requireContext(), "Minimum text size reached", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_q13Fragment_to_q14Fragment)
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