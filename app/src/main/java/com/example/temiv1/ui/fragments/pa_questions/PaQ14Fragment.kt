package com.example.temiv1.ui.fragments.pa_questions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.ImageButton
import android.widget.RadioButton
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

class PaQ14Fragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pa_q14, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q14)
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
            val paq14 = TtsRequest.create("How many times a week do you participate in moderate exercise? (e.g. gardening, moving a table, pushing a vacuum cleaner, bowling, golf)", false)
            robot?.speak(paq14)
        }

        val continueButton: Button = view.findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                findNavController().navigate(R.id.action_paQ14Fragment_to_danceMoveSelectionFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select an answer before continuing.",
                    Toast.LENGTH_SHORT
                ).show()
                val reqAnswer =
                    TtsRequest.create("Please select an answer to continue", false)
                robot?.speak(reqAnswer)
            }
        }
    }
}