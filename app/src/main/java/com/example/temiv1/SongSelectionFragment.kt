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

class SongSelectionFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SongAdapter
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    private val allSongs = listOf(
        Song("Jazz", DifficultyLevel.EASY, bpm = 100, genre = "Jazz", R.raw.jazz100bpm),
        Song("Electronic", DifficultyLevel.EASY, bpm = 100, genre = "Electronic", R.raw.electronic100bpm)
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

        val filteredSongs = allSongs.filter { it.level == DifficultyLevel.EASY }
        adapter = SongAdapter(filteredSongs)
        recyclerView.adapter = adapter

        val continueButton: Button = view.findViewById(R.id.songContinueButton)
        continueButton.setOnClickListener {
            val selectedSong = adapter.getSelectedSong()
            if (selectedSong == null) {
                Toast.makeText(requireContext(), "Please select a song", Toast.LENGTH_SHORT).show()
                robot?.speak(TtsRequest.create("Please select a song", false))
            } else {
                Toast.makeText(requireContext(), "Selected ${selectedSong.name} song", Toast.LENGTH_SHORT).show()
                Log.d("SelectedSong", "Song: ${selectedSong.name}")
                findNavController().navigate(R.id.action_songSelectionFragment_to_videoPlaying)
            }

            sessionViewModel.selectedSong.value = selectedSong

            val songDuration = selectedSong?.let {
                DanceVideoGenerator.getAudioDurationFromRaw(requireContext(), it.audioResId)
            } ?: (2 * 60 * 1000L)

            val selectedMoves = sessionViewModel.selectedMoves.value ?: emptyList()
            val allMoves = sessionViewModel.allMoves.value ?: emptyList()

            val moveDurations = DanceVideoGenerator.getClipDurations(requireContext(), selectedMoves)

            val restMove4Beats = allMoves.find { it.videoResId == R.raw.easy100bpm_rest_move_x4 }!! // non-null assertion
            val restMove6Beats = allMoves.find { it.videoResId == R.raw.easy100bpm_rest_move_x6 }!!

            val movesPlaylist = DanceVideoGenerator.buildDanceMovesPlaylist(
                selectedMoves = selectedMoves,
                moveDurations = moveDurations,
                targetDurationMillis = songDuration,
                restMove4Beats = restMove4Beats,
                restMove6Beats = restMove6Beats
            )

            sessionViewModel.movesPlaylist.value = movesPlaylist

            Log.d("Dance Video", "Generated playlist: ${movesPlaylist.map { it.name }}")
            val counts = movesPlaylist.groupingBy { it.name }.eachCount()
            Log.d("DanceVideo", "Move usage counts: $counts")
        }
    }
}
