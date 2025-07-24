package com.example.temiv1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DanceMoveSelection : BaseFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DanceMoveAdapter

    private val allMoves = listOf(
        DanceMove("Arm Raises", R.drawable.arm_raises_x4, DifficultyLevel.EASY, R.raw.mp_arm_raises_x4),
        DanceMove("Arm Rolls Side to Side", R.drawable.arm_rolls_side_to_side_x4, DifficultyLevel.EASY, R.raw.mp_arm_rolls_side_to_side_x4),
        DanceMove("Arm Swings", R.drawable.arm_swings_x4, DifficultyLevel.MEDIUM, R.raw.mp_arm_swings_x4),
        DanceMove("Forward Arm Extensions", R.drawable.forward_arm_extensions_x4, DifficultyLevel.HARD, R.raw.mp_forward_arm_extensions_x4),
        DanceMove("Side Steps", R.drawable.side_steps_x4, DifficultyLevel.EASY, R.raw.mp_side_steps_x4),
        DanceMove("Single Marches", R.drawable.single_marches_x4, DifficultyLevel.EASY, R.raw.mp_single_marches_x4),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dance_move_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_moves)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        fragmentScope.launch {
            delay(1000)
            val requestSelection = TtsRequest.create("Please select dance moves you would like to perform.", false)
            robot?.speak(requestSelection)
        }

        val filteredMoves = allMoves.filter { it.level == DifficultyLevel.EASY }
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
            }
        }

    }

}