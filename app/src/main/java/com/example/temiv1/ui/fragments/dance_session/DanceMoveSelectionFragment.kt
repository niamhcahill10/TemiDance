package com.example.temiv1.ui.fragments.dance_session

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temiv1.viewmodel.DanceSessionViewModel
import com.example.temiv1.R
import com.example.temiv1.adapters.DanceMoveAdapter
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.dance.data.DanceMove
import com.example.temiv1.dance.data.DifficultyLevel
import com.example.temiv1.dance.data.MoveType
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DanceMoveSelectionFragment : BaseFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DanceMoveAdapter
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()
    private lateinit var textView: TextView

    private val allMoves = listOf(
        DanceMove("Rest Move x4",
            R.drawable.rest, DifficultyLevel.EASY, MoveType.ARM,
            R.raw.easy100bpm_rest_move_arms_x4
        ),
        DanceMove("Rest Move x6",
            R.drawable.rest, DifficultyLevel.EASY, MoveType.ARM,
            R.raw.easy100bpm_rest_move_arms_x6
        ),
        DanceMove("Rest Move x4",
            R.drawable.rest, DifficultyLevel.EASY, MoveType.LEG,
            R.raw.easy100bpm_rest_move_legs_x4
        ),
        DanceMove("Rest Move x6",
            R.drawable.rest, DifficultyLevel.EASY, MoveType.LEG,
            R.raw.easy100bpm_rest_move_legs_x6
        ),
        DanceMove("Rest Move x4",
            R.drawable.rest, DifficultyLevel.MEDIUM, MoveType.ARM,
            R.raw.medium110bpm_rest_move_arms_x4
        ),
        DanceMove("Rest Move x6",
            R.drawable.rest, DifficultyLevel.MEDIUM, MoveType.ARM,
            R.raw.medium110bpm_rest_move_arms_x6
        ),
        DanceMove("Rest Move x4",
            R.drawable.rest, DifficultyLevel.MEDIUM, MoveType.LEG,
            R.raw.medium110bpm_rest_move_legs_x4
        ),
        DanceMove("Rest Move x6",
            R.drawable.rest, DifficultyLevel.MEDIUM, MoveType.LEG,
            R.raw.medium110bpm_rest_move_legs_x6
        ),
        DanceMove("Rest Move x4",
            R.drawable.rest, DifficultyLevel.HARD, MoveType.ARM,
            R.raw.hard120bpm_rest_move_arms_x4
        ),
        DanceMove("Rest Move x6",
            R.drawable.rest, DifficultyLevel.HARD, MoveType.ARM,
            R.raw.hard120bpm_rest_move_arms_x6
        ),
        DanceMove("Rest Move x4",
            R.drawable.rest, DifficultyLevel.HARD, MoveType.LEG,
            R.raw.hard120bpm_rest_move_legs_x4
        ),
        DanceMove("Rest Move x6",
            R.drawable.rest, DifficultyLevel.HARD, MoveType.LEG,
            R.raw.hard120bpm_rest_move_legs_x6
        ),
        DanceMove("Arm Raises x 8",
            R.drawable.arm_raises, DifficultyLevel.EASY, MoveType.LEG,
            R.raw.easy100bpm_arm_raises_x8
        ),
        DanceMove("Arm Rolls x 8",
            R.drawable.arm_rolls_side_to_side, DifficultyLevel.EASY, MoveType.ARM,
            R.raw.easy100bpm_arm_rolls_side_to_side_x8
        ),
        DanceMove("Arm Swings x 8",
            R.drawable.arm_swings, DifficultyLevel.EASY, MoveType.ARM,
            R.raw.easy100bpm_arm_swings_x8
        ),
        DanceMove("Arm Extensions x 8",
            R.drawable.fwd_arm_extensions, DifficultyLevel.EASY, MoveType.ARM,
            R.raw.easy100bpm_fwd_arm_extensions_x8
        ),
        DanceMove("Box Steps x 8",
            R.drawable.box_steps, DifficultyLevel.EASY, MoveType.LEG,
            R.raw.easy100bpm_box_steps_x8
        ),
        DanceMove("Leg Extensions x 8",
            R.drawable.fwd_leg_extensions, DifficultyLevel.EASY, MoveType.LEG,
            R.raw.easy100bpm_fwd_leg_extensions_x8
        ),
        DanceMove("Side Steps x 8",
            R.drawable.side_steps, DifficultyLevel.EASY, MoveType.LEG,
            R.raw.easy100bpm_side_steps_x8
        ),
        DanceMove("Single Marches x 8",
            R.drawable.single_marches, DifficultyLevel.EASY, MoveType.LEG,
            R.raw.easy100bpm_single_marches_x8
        ),
        DanceMove("Arm Raises x 12",
            R.drawable.arm_raises, DifficultyLevel.MEDIUM, MoveType.LEG,
            R.raw.medium110bpm_arm_raises_x12
        ),
        DanceMove("Arm Rolls x 12",
            R.drawable.arm_rolls_side_to_side, DifficultyLevel.MEDIUM, MoveType.ARM,
            R.raw.medium110bpm_arm_rolls_side_to_side_x12
        ),
        DanceMove("Arm Swings x 12",
            R.drawable.arm_swings, DifficultyLevel.MEDIUM, MoveType.ARM,
            R.raw.medium110bpm_arm_swings_12
        ),
        DanceMove("Arm Extensions x 12",
            R.drawable.fwd_arm_extensions, DifficultyLevel.MEDIUM, MoveType.ARM,
            R.raw.medium110bpm_fwd_arm_extensions_12
        ),
        DanceMove("Box Steps x 12",
            R.drawable.box_steps, DifficultyLevel.MEDIUM, MoveType.LEG,
            R.raw.medium110bpm_box_steps_x12
        ),
        DanceMove("Leg Extensions x 12",
            R.drawable.fwd_leg_extensions, DifficultyLevel.MEDIUM, MoveType.LEG,
            R.raw.medium110bpm_fwd_leg_extensions_x12
        ),
        DanceMove("Side Steps x 12",
            R.drawable.side_steps, DifficultyLevel.MEDIUM, MoveType.LEG,
            R.raw.medium110bpm_side_steps_x12
        ),
        DanceMove("Single Marches x 12",
            R.drawable.single_marches, DifficultyLevel.MEDIUM, MoveType.LEG,
            R.raw.medium110bpm_single_marches_x12
        ),
        DanceMove("Arm Raises x 16",
            R.drawable.arm_raises, DifficultyLevel.HARD, MoveType.LEG,
            R.raw.hard120bpm_arm_raises_x16
        ),
        DanceMove("Arm Rolls x 16",
            R.drawable.arm_rolls_side_to_side, DifficultyLevel.HARD, MoveType.ARM,
            R.raw.hard120bpm_arm_rolls_side_to_side_x16
        ),
        DanceMove("Arm Swings x 16",
            R.drawable.arm_swings, DifficultyLevel.HARD, MoveType.ARM,
            R.raw.hard120bpm_arm_swings_x16
        ),
        DanceMove("Arm Extensions x 16",
            R.drawable.fwd_arm_extensions, DifficultyLevel.HARD, MoveType.ARM,
            R.raw.hard120bpm_fwd_arm_extensions_x16
        ),
        DanceMove("Box Steps x 16",
            R.drawable.box_steps, DifficultyLevel.HARD, MoveType.LEG,
            R.raw.hard120bpm_box_steps_x16
        ),
        DanceMove("Leg Extensions x 16",
            R.drawable.fwd_leg_extensions, DifficultyLevel.HARD, MoveType.LEG,
            R.raw.hard120bpm_fwd_leg_extensions
        ),
        DanceMove("Side Steps x 16",
            R.drawable.side_steps, DifficultyLevel.HARD, MoveType.LEG,
            R.raw.hard120bpm_side_steps_x16
        ),
        DanceMove("Single Marches x 16",
            R.drawable.single_marches, DifficultyLevel.HARD, MoveType.LEG,
            R.raw.hard120bpm_single_marches_x16
        ),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dance_move_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionViewModel.allMoves.value = allMoves

        recyclerView = view.findViewById(R.id.recycler_moves)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        textView = view.findViewById(R.id.titleSelection)
        textView.textSize = sessionViewModel.textSizeSp

        fragmentScope.launch {
            delay(1000)
            val requestSelection = TtsRequest.create("Choose your dance moves.", false)
            robot?.speak(requestSelection)
        }

        val currentLevel = sessionViewModel.currentLevel.value

        val filteredMoves = allMoves.filter { it.level == currentLevel && it.name != "Rest Move x4" && it.name != "Rest Move x6"}
        adapter = DanceMoveAdapter(filteredMoves)
        recyclerView.adapter = adapter


        val continueButton: Button = view.findViewById(R.id.continueButton)
        val toggle: SwitchCompat = view.findViewById(R.id.selectClearSwitch)

        toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                adapter.selectAll()
                CsvLogger.logEvent("moves", "select_all", "clicked")
            } else {
                adapter.clearSelection()
                CsvLogger.logEvent("moves", "clear_all", "clicked")
            }
        }

        continueButton.setOnClickListener {
            val selectedMoves = adapter.getSelectedMoves()
            if (selectedMoves.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one move", Toast.LENGTH_SHORT).show()
                robot?.speak(TtsRequest.create("Please select at least one move", false))
            } else {
                Toast.makeText(requireContext(), "Selected ${selectedMoves.size} moves", Toast.LENGTH_SHORT).show()
                Log.d("SelectedMoves", "Moves: ${selectedMoves.map { it.name }}")

                val movesString = selectedMoves.joinToString("|") { it.name }
                CsvLogger.logEvent("moves", "final_selection", movesString)

                findNavController().navigate(R.id.action_danceMoveSelectionFragment_to_songSelectionFragment)
            }

            sessionViewModel.selectedMoves.value = selectedMoves
        }

    }

}