/**
 * UI fragment for level selection.
 *
 * - User starts at easy and can progress one-level at a time to medium and then hard,
 *   user can also stay at the same level or regress (once at medium or hard)
 * - Displays guidance text, plays prompts, and wires button listeners
 * - Logs user interactions (clicks) and answer
 */

package com.example.temiv1.ui.fragments.feedback

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
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
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp) // Keep user's specified text size preference

        fragmentScope.launch {
            delay(1000)
            val progression =
                TtsRequest.create("Would you like to change your level? See the descriptions for each level below.", false)
            robot?.speak(progression)
        }

        val radioProgress: RadioButton = view.findViewById(R.id.radioProgress)
        val radioMaintain: RadioButton = view.findViewById(R.id.radioMaintain)
        val radioRegress: RadioButton = view.findViewById(R.id.radioRegress)
        val radioGroup: RadioGroup = view.findViewById(R.id.progressionOptions)
        for (i in 0 until radioGroup.childCount) {
            val child = radioGroup.getChildAt(i)
            if (child is RadioButton) {
                child.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp)
            }
        }

        val continueButton: Button = view.findViewById(R.id.continueButton)
        val endSessionButton: Button = view.findViewById(R.id.endSessionButton)

        val currentLevel = sessionViewModel.currentLevel.value ?: DifficultyLevel.EASY

        // Only display available level options
        when (currentLevel) {
            // Cannot regress from easy
            DifficultyLevel.EASY -> {
                radioRegress.visibility = View.GONE
                radioMaintain.visibility = View.VISIBLE
                radioProgress.visibility = View.VISIBLE
            }
            DifficultyLevel.MEDIUM -> {
                radioRegress.visibility = View.VISIBLE
                radioMaintain.visibility = View.VISIBLE
                radioProgress.visibility = View.VISIBLE
            }
            // Cannot progress from hard
            DifficultyLevel.HARD -> {
                radioRegress.visibility = View.VISIBLE
                radioMaintain.visibility = View.VISIBLE
                radioProgress.visibility = View.GONE
            }
        }

        continueButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId

            if (selectedId == -1) {
                Toast.makeText(
                    requireContext(),
                    "Please select an answer",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val selectedButton: RadioButton = view.findViewById(selectedId)
            val answerText = selectedButton.text.toString()
            CsvLogger.logEvent("answers", "progression", answerText) // Exportable log of selected level

            // Determine newLevel based on user selection
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

            Log.d("ProgressionFragment", "Current level: $currentLevel, New level: $newLevel") // Debugging logs
            Log.d("ProgressionFragment", "Continue button clicked")

            Toast.makeText(requireContext(), "Continue clicked", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_progressionFragment_to_danceMoveSelectionFragment) // Navigate to select dance moves for next dance

        }

        endSessionButton.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext()) // Alert to check user really wants to end session (recovery)
                .setTitle("End Session")
                .setMessage("Are you sure you want to end your session?")
                .setPositiveButton("Yes") { dialog, _ ->
                    findNavController().navigate(R.id.action_progressionFragment_to_endSessionFragment) // Navigate to end session fragment
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    CsvLogger.logEvent("recovery", "end_recovery_button", "clicked") // Exportable log of recovery button usage
                    dialog.dismiss()
                }
                .show()
        }
    }
}