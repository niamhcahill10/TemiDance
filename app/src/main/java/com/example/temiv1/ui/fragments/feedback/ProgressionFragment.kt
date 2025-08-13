package com.example.temiv1.ui.fragments.feedback

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.temiv1.viewmodel.DanceSessionViewModel
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.dance.data.DifficultyLevel

class ProgressionFragment : BaseFragment() {
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progression, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.progression)
        textView.textSize = sessionViewModel.textSizeSp

        fragmentScope.launch {
            delay(1000)
            val q1feedback =
                TtsRequest.create("The next level is slightly faster and has 12 reps per move. Would you like to try it?", false)
            robot?.speak(q1feedback)
        }

        val radioProgress: RadioButton = view.findViewById(R.id.radioProgress)
        val radioMaintain: RadioButton = view.findViewById(R.id.radioMaintain)
        val radioRegress: RadioButton = view.findViewById(R.id.radioRegress)
        val radioGroup: RadioGroup = view.findViewById(R.id.progressionOptions)
        for (i in 0 until radioGroup.childCount) {
            val child = radioGroup.getChildAt(i)
            if (child is RadioButton) {
                child.textSize = sessionViewModel.textSizeSp
            }
        }

        val continueButton: Button = view.findViewById(R.id.continueButton)
        val endSessionButton: Button = view.findViewById(R.id.endSessionButton)

        val currentLevel = sessionViewModel.currentLevel.value ?: DifficultyLevel.EASY

        when (currentLevel) {
            DifficultyLevel.EASY -> {
                radioProgress.visibility = View.VISIBLE
                radioMaintain.visibility = View.VISIBLE
                radioRegress.visibility = View.GONE
            }
            DifficultyLevel.MEDIUM -> {
                radioProgress.visibility = View.VISIBLE
                radioMaintain.visibility = View.VISIBLE
                radioRegress.visibility = View.VISIBLE
            }
            DifficultyLevel.HARD -> {
                radioProgress.visibility = View.GONE
                radioMaintain.visibility = View.VISIBLE
                radioRegress.visibility = View.VISIBLE
            }
        }

        continueButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId

            if (selectedId == -1) {
                Toast.makeText(
                    requireContext(),
                    "Please select an answer before continuing.",
                    Toast.LENGTH_SHORT
                ).show()
                val reqAnswer =
                    TtsRequest.create("Please select an answer to continue", false)
                robot?.speak(reqAnswer)
                return@setOnClickListener
            }

            val newLevel = when (selectedId) {
                R.id.radioProgress -> when (currentLevel) {
                    DifficultyLevel.EASY -> DifficultyLevel.MEDIUM
                    DifficultyLevel.MEDIUM -> DifficultyLevel.HARD
                    else -> currentLevel
                }
                R.id.radioRegress -> when (currentLevel) {
                    DifficultyLevel.MEDIUM -> DifficultyLevel.EASY
                    DifficultyLevel.HARD -> DifficultyLevel.MEDIUM
                    else -> currentLevel
                }
                else -> currentLevel
            }

            sessionViewModel.currentLevel.value = newLevel

            Log.d("ProgressionFragment", "Current level: $currentLevel, New level: $newLevel")

            Log.d("ProgressionFragment", "Continue button clicked")
            Toast.makeText(requireContext(), "Continue clicked", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_progressionFragment_to_danceMoveSelectionFragment)

        }

        endSessionButton.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("End Session")
                .setMessage("Are you sure you want to end your session?")
                .setPositiveButton("Yes") { dialog, _ ->
                    findNavController().navigate(R.id.action_progressionFragment_to_endSessionFragment)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    CsvLogger.logEvent("recovery", "end_recovery_button", "clicked")
                    dialog.dismiss()
                }
                .show()
        }
    }
}