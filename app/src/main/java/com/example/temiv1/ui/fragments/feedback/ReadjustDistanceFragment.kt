/**
 * UI fragment for re-adjusting the robot distance after the dance for the user to answer feedback questions.
 *
 * - Separate distances for Q&A vs seated dance
 * - Researcher manually moves robot and measures distance when fragment displays
 * - Displays guidance text, plays prompts, and wires button listeners
 * - Logs user interactions (clicks)
 */

package com.example.temiv1.ui.fragments.feedback

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.viewmodel.DanceSessionViewModel
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReadjustDistanceFragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_readjust_distance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.textView)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp) // Keep user's specified text size preference

        fragmentScope.launch {
            delay(1000)
            val adjustReminder = TtsRequest.create("Now lets re-adjust the distance of the robot so that you can answer the feedback questions. The researcher will take the maracas, move the robot, measure the distance, and then click continue.", false)
            robot?.speak(adjustReminder)
        }

        val continueButton: Button = view.findViewById(R.id.continueButton)

        continueButton.setOnClickListener {
            findNavController().navigate(R.id.action_readjustDistanceFragment_to_feedbackQ1Fragment) // Navigates to first feedback question fragment
        }

    }

}