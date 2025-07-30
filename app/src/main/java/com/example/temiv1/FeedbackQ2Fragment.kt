package com.example.temiv1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.RadioGroup
import android.widget.Toast

class FeedbackQ2Fragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback_q2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentScope.launch {
            delay(1000)
            val q1feedback =
                TtsRequest.create("How well were you able to follow the moves during the dance?", false)
            robot?.speak(q1feedback)
        }

        val continueButton: Button = view.findViewById(R.id.continueButton)
        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        continueButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
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