package com.example.temiv1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DanceMoveSelection : BaseFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DanceMoveAdapter
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    private val allMoves = listOf(
        DanceMove("Rest Move x4", R.drawable.rest, DifficultyLevel.EASY, R.raw.easy100bpm_rest_move_x4),
        DanceMove("Rest Move x6", R.drawable.rest, DifficultyLevel.EASY, R.raw.easy100bpm_rest_move_x6),
        DanceMove("Arm Raises x8", R.drawable.arm_raises, DifficultyLevel.EASY, R.raw.easy100bpm_arm_raises_x8),
        DanceMove("Arm Rolls Side to Side x8", R.drawable.arm_rolls_side_to_side, DifficultyLevel.EASY, R.raw.easy100bpm_arm_rolls_side_to_side_x8),
        DanceMove("Arm Swings x8", R.drawable.arm_swings, DifficultyLevel.EASY, R.raw.easy100bpm_arm_swings_x8),
        DanceMove("Forward Arm Extensions x8", R.drawable.fwd_arm_extensions, DifficultyLevel.EASY, R.raw.easy100bpm_fwd_arm_extensions_x8),
        DanceMove("Box Steps x8", R.drawable.box_steps, DifficultyLevel.EASY, R.raw.easy100bpm_box_steps_x8),
        DanceMove("Forward Leg Extensions x8", R.drawable.fwd_leg_extensions, DifficultyLevel.EASY, R.raw.easy100bpm_fwd_leg_extensions_x8),
        DanceMove("Side Steps x8", R.drawable.side_steps, DifficultyLevel.EASY, R.raw.easy100bpm_side_steps_x8),
        DanceMove("Single Marches x8", R.drawable.single_marches, DifficultyLevel.EASY, R.raw.easy100bpm_single_marches_x8),
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

        fragmentScope.launch {
            delay(1000)
            val requestSelection = TtsRequest.create("Please select dance moves you would like to perform.", false)
            robot?.speak(requestSelection)
        }

        val filteredMoves = allMoves.filter { it.level == DifficultyLevel.EASY && it.name != "Rest Move x4" && it.name != "Rest Move x6"}
        adapter = DanceMoveAdapter(filteredMoves)
        recyclerView.adapter = adapter

        val continueButton: Button = view.findViewById(R.id.continueButton)
        continueButton.setOnClickListener {
            val selectedMoves = adapter.getSelectedMoves()
            if (selectedMoves.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one move", Toast.LENGTH_SHORT).show()
                robot?.speak(TtsRequest.create("Please select at least one move", false))
            } else {
                // Navigation to SongSelectionFragment would go here
                Toast.makeText(requireContext(), "Selected ${selectedMoves.size} moves", Toast.LENGTH_SHORT).show()
                Log.d("SelectedMoves", "Moves: ${selectedMoves.map { it.name }}")
                findNavController().navigate(R.id.action_danceMoveSelection2_to_songSelectionFragment)
            }

            sessionViewModel.selectedMoves.value = selectedMoves
        }

    }

}