package com.example.temiv1.ui.fragments.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.viewmodel.DanceSessionViewModel

class FeedbackQ2Fragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback_q2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.textView)
        textView.textSize = sessionViewModel.textSizeSp

        fragmentScope.launch {
            delay(1000)
            val q2feedback =
                TtsRequest.create("How well were you able to follow the moves during the dance?", false)
            robot?.speak(q2feedback)
        }

        val continueButton: Button = view.findViewById(R.id.continueButton)
        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        for (i in 0 until radioGroup.childCount) {
            val child = radioGroup.getChildAt(i)
            if (child is RadioButton) {
                child.textSize = sessionViewModel.textSizeSp
            }
        }

        continueButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedButton: RadioButton = view.findViewById(selectedId)
                val answerText = selectedButton.text.toString()
                CsvLogger.logEvent("answers", "feedback_q2", answerText)
                findNavController().navigate(R.id.action_feedbackQ2Fragment_to_progressionFragment)
            } else {
                Toast.makeText(requireContext(), "Please select an answer before continuing.", Toast.LENGTH_SHORT).show()
                val reqAnswer =
                    TtsRequest.create("Please select an answer to continue", false)
                robot?.speak(reqAnswer)
            }
        }
    }
}