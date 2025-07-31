package com.example.temiv1.ui.fragments.pa_questions

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
import com.example.temiv1.R
import com.example.temiv1.base.BaseFragment

class Q11Fragment : BaseFragment() {
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_q11, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q11)
        textView.textSize = globalTextSizeSp

        fragmentScope.launch {
            delay(1000)
            val q11 = TtsRequest.create("Is the text big enough?", false)
            robot?.askQuestion(q11)
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
        findNavController().navigate(R.id.action_q11Fragment_to_q14Fragment)
    }

    private fun onNoSelected() {
        updateTextSize(8f)
        textView.textSize = globalTextSizeSp
        robot?.askQuestion("Is the text big enough?")

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