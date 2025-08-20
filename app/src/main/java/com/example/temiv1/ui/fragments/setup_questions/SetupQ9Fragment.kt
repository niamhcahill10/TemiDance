/**
 * UI fragment for setting up text size preference.
 *
 * - Yes increases text size one increment and navigates to text size increase fragment
 * - No moves to text size decrease fragment
 * - Displays guidance text, plays prompts, and wires button/Asr listeners
 * - Logs text size if chosen to increase from initial setting
 */

package com.example.temiv1.ui.fragments.setup_questions

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.viewmodel.DanceSessionViewModel

class SetupQ9Fragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q9, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q9)
        sessionViewModel.textSizeSp = 38f // Adds the starting text size to the sessionViewModel so it can be accessed across fragments
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp)

        fragmentScope.launch {
            delay(1000)
            val sq9 = TtsRequest.create("Would you like the text bigger?", false)
            robot?.askQuestion(sq9)
        }

        val yesButton: Button = view.findViewById(R.id.yesButton)
        yesButton.setOnClickListener {
            onYesSelected()
        }

        val noButton: Button = view.findViewById(R.id.noButton)
        noButton.setOnClickListener {
            onNoSelected()
        }

    }

    private fun onYesSelected() {
        sessionViewModel.textSizeSp += 2f // Increases text size in view model by 2sp
        CsvLogger.logEvent("settings", "text_adjust", sessionViewModel.textSizeSp) // Exportable log of first text size increase
        findNavController().navigate(R.id.action_setupQ9Fragment_to_setupQ10Fragment) // Navigates to fragment that allows further increase in text size on yes selected
    }

    private fun onNoSelected() {
        findNavController().navigate(R.id.action_setupQ9Fragment_to_setupQ11Fragment) // Navigates to text size decrease fragment on no selected
    }

    // Speech recognition for yes / no answers
    override fun handleAsr(command: String) {
        if (!isTemiDevice) return
        when (command) {
            "yes" -> onYesSelected()
            "no" -> onNoSelected()
            else -> robot?.askQuestion(TtsRequest.create("Please say yes or no.", false))
        }
    }
}