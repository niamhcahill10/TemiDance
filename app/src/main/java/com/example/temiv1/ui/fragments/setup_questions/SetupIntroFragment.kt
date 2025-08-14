package com.example.temiv1.ui.fragments.setup_questions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SetupIntroFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CsvLogger.logUseTime("time", "start_app")

        fragmentScope.launch {
            delay(1000)
            val intro = TtsRequest.create("Welcome! This app will ask some physical capabilities questions and generate dance videos for you to follow based on the moves you select. On screens where you can see yes or no buttons you can either verbally respond yes or no once the question has been asked and you see the blue listener eyes pop-up in the bottom left corner, or you can tap the buttons instead. Questions with multiple answers must be selected by hand as no voice recognition is enabled for these.", false)
            robot?.speak(intro)
        }

        val startButton: Button = view.findViewById(R.id.continueButton)
        startButton.setOnClickListener {
            findNavController().navigate(R.id.action_setupIntroFragment_to_setupQ1Fragment)
        }
    }
}
