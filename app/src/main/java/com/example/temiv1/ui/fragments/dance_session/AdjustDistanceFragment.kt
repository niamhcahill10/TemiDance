/**
 * UI fragment for adjusting the robot distance before the dance.
 *
 * - Separate distances for Q&A vs seated dance
 * - Displays guidance text, plays prompts, and wires button listeners
 * - Logs user interactions (clicks)
 */

package com.example.temiv1.ui.fragments.dance_session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.viewmodel.DanceSessionViewModel
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdjustDistanceFragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_adjust_distance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.textView)
        textView.textSize = sessionViewModel.textSizeSp

        fragmentScope.launch {
            delay(1000)
            val adjustReminder = TtsRequest.create("Before the dance begins lets make sure you are happy with the distance of the robot. The researcher will move the robot for you, measure the distance, pass you the maracas, and then press play. Only do the range of movement you are comfortable with when performing the dance moves.", false)
            robot?.speak(adjustReminder)
        }

        val playButton: Button = view.findViewById(R.id.playButton)

        playButton.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Begin Dance")
                .setMessage("Are you sure you want to begin the dance?")
                .setPositiveButton("Yes") { dialog, _ ->
                    findNavController().navigate(R.id.action_adjustDistanceFragment_to_videoPlayingFragment)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    CsvLogger.logEvent("recovery", "dance_recovery_button", "clicked")
                    dialog.dismiss()
                }
                .show()
        }

    }

}