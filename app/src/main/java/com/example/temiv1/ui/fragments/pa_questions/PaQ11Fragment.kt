/**
 * UI fragment for asking physical capabilities question.
 *
 * - Single-select from five options
 * - Displays guidance text, plays prompts, and wires button listeners
 * - Logs user interactions (clicks) and answer
 */

package com.example.temiv1.ui.fragments.pa_questions

import android.os.Bundle
import android.util.TypedValue
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

class PaQ11Fragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pa_q11, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q11)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp) // Keep user's specified text size preference

        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        for (i in 0 until radioGroup.childCount) {
            val child = radioGroup.getChildAt(i)
            if (child is RadioButton) {
                child.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp)
            }
        }

        fragmentScope.launch {
            delay(1000)
            val paq11 = TtsRequest.create("How many times a week do you participate in vigorous exercise? (e.g. running, lifting heavy objects, strenuous sports)", false)
            robot?.speak(paq11)
        }

        val continueButton: Button = view.findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedButton: RadioButton = view.findViewById(selectedId)
                val answerText = selectedButton.text.toString()
                CsvLogger.logEvent("answers", "pa_q11", answerText)
                findNavController().navigate(R.id.action_paQ11Fragment_to_paQ12Fragment) // Navigate to next fragment once an answer has been selected and user clicks continue
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