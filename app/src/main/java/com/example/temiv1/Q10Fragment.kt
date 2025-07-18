package com.example.temiv1

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.provider.Settings
import android.widget.ImageButton
import android.widget.TextView

class Q10Fragment : BaseFragment() {
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_q10, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q10)

        fragmentScope.launch {
            delay(1000)
            val q10 = TtsRequest.create("Would you like the text bigger?", false)
            robot?.askQuestion(q10)
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

        updateTextSize(8f)
        textView.textSize = globalTextSizeSp
        findNavController().navigate(R.id.action_q10Fragment_to_q11Fragment)
    }

    private fun onNoSelected() {
        findNavController().navigate(R.id.action_q10Fragment_to_q12Fragment)
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