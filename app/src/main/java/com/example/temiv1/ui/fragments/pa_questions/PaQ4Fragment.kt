package com.example.temiv1.ui.fragments.pa_questions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.viewmodel.DanceSessionViewModel
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaQ4Fragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pa_q4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q4)
        textView.textSize = sessionViewModel.textSizeSp

        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        for (i in 0 until radioGroup.childCount) {
            val child = radioGroup.getChildAt(i)
            if (child is RadioButton) {
                child.textSize = sessionViewModel.textSizeSp
            }
        }

        fragmentScope.launch {
            delay(1000)
            val paq4 = TtsRequest.create("The remaining questions do not have voice recognition so you will need to select an answer on the screen and then press continue. How do you typically stand up from a chair?", false)
            robot?.speak(paq4)
        }

        val continueButton: Button = view.findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedButton: RadioButton = view.findViewById(selectedId)
                val answerText = selectedButton.text.toString()
                CsvLogger.logEvent("answers", "pa_q4", answerText)
                findNavController().navigate(R.id.action_paQ4Fragment_to_paQ5Fragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select an answer",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}