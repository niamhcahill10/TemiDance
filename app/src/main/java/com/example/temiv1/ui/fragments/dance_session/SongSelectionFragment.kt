package com.example.temiv1.ui.fragments.dance_session

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
import com.example.temiv1.viewmodel.DanceSessionViewModel
import com.example.temiv1.dance.DanceVideoGenerator
import com.example.temiv1.R
import com.example.temiv1.adapters.SongAdapter
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.dance.data.DifficultyLevel
import com.example.temiv1.dance.data.Song
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SongSelectionFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SongAdapter
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    private val allSongs = listOf(
        Song("Electronic", DifficultyLevel.EASY, bpm = 100, genre = "Electronic",
            R.raw.electronic100bpm
        ),
        Song("Jazz", DifficultyLevel.EASY, bpm = 100, genre = "Jazz", R.raw.jazz100bpm),
        Song("Jazz", DifficultyLevel.MEDIUM, bpm = 110, genre = "Jazz", R.raw.jazz110bpm),
        Song("Salsa", DifficultyLevel.MEDIUM, bpm = 110, genre = "Salsa", R.raw.salsa110bpm),
        Song("Pop", DifficultyLevel.HARD, bpm = 120, genre = "Pop", R.raw.pop120bpm),
        Song("Salsa", DifficultyLevel.HARD, bpm = 120, genre = "Salsa", R.raw.salsa120bpm)

    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            return inflater.inflate(R.layout.fragment_song_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = view.findViewById(R.id.recycler_songs)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        fragmentScope.launch {
            delay(1000)
            val requestSelection = TtsRequest.create("Please select a song.", false)
            robot?.speak(requestSelection)
        }

        val currentLevel = sessionViewModel.currentLevel.value
        val filteredSongs = allSongs.filter { it.level == currentLevel }
        adapter = SongAdapter(filteredSongs)
        recyclerView.adapter = adapter

        val skipButton: Button = view.findViewById(R.id.skipButton)
        val continueButton: Button = view.findViewById(R.id.songContinueButton)

        continueButton.setOnClickListener {
            val selectedSong = adapter.getSelectedSong()
            if (selectedSong == null) {
                Toast.makeText(requireContext(), "Please select a song", Toast.LENGTH_SHORT).show()
                robot?.speak(TtsRequest.create("Please select a song", false))
            } else {
                sessionViewModel.selectedSong.value = selectedSong

                val songDuration = DanceVideoGenerator.getAudioDurationFromRaw(
                    requireContext(), selectedSong.audioResId
                )

                val selectedMoves = sessionViewModel.selectedMoves.value ?: emptyList()
                val allMoves = sessionViewModel.allMoves.value ?: emptyList()

                val moveDurations = DanceVideoGenerator.getClipDurations(
                    requireContext(), selectedMoves
                )

                val restMove4BeatsArms =
                    allMoves.find { it.videoResId == R.raw.easy100bpm_rest_move_arms_x4 }!! // non-null assertion
                val restMove6BeatsArms =
                    allMoves.find { it.videoResId == R.raw.easy100bpm_rest_move_arms_x6 }!!
                val restMove4BeatsLegs =
                    allMoves.find { it.videoResId == R.raw.easy100bpm_rest_move_legs_x4 }!!
                val restMove6BeatsLegs =
                    allMoves.find { it.videoResId == R.raw.easy100bpm_rest_move_legs_x6 }!!

                val movesPlaylist = DanceVideoGenerator.buildDanceMovesPlaylist(
                    selectedMoves = selectedMoves,
                    moveDurations = moveDurations,
                    targetDurationMillis = songDuration,
                    restMove4BeatsArms = restMove4BeatsArms,
                    restMove6BeatsArms = restMove6BeatsArms,
                    restMove4BeatsLegs = restMove4BeatsLegs,
                    restMove6BeatsLegs = restMove6BeatsLegs
                )

                sessionViewModel.movesPlaylist.value = movesPlaylist

                Log.d("Dance Video", "Generated playlist: ${movesPlaylist.map { it.name }}")
                val counts = movesPlaylist.groupingBy { it.name }.eachCount()
                Log.d("DanceVideo", "Move usage counts: $counts")

                Toast.makeText(
                    context,
                    "Selected ${selectedSong.name} song",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_songSelectionFragment_to_videoPlaying)
            }
            }

        skipButton.setOnClickListener {
            findNavController().navigate(R.id.action_songSelectionFragment_to_feedbackQ1Fragment)
        }
        }
}
