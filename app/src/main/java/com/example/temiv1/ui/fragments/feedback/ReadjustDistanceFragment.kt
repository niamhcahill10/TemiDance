package com.example.temiv1.ui.fragments.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.base.BaseFragment
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReadjustDistanceFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_readjust_distance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentScope.launch {
            delay(1000)
            val adjustReminder = TtsRequest.create("Now lets re-adjust the distance of the robot so that you can answer the feedback questions. The researcher will move the robot, measure the distance, and then click continue.", false)
            robot?.speak(adjustReminder)
        }

        val continueButton: Button = view.findViewById(R.id.continueButton)

        continueButton.setOnClickListener {
            findNavController().navigate(R.id.action_readjustDistanceFragment_to_feedbackQ1Fragment)
        }

    }

}