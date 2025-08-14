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

class PaQ12Fragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pa_q12, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q12)
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
            val paq12 = TtsRequest.create("Approximately how much total time do you spend exercising or being physically active in a typical week?", false)
            robot?.speak(paq12)
        }

        val continueButton: Button = view.findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                findNavController().navigate(R.id.action_paQ12Fragment_to_paQ13Fragment)
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