/**
 * UI fragment for song selection.
 *
 * - Filters selectable songs to the user's current level
 * - Displays guidance text, plays prompts, and wires button listeners
 * - Once user continues generates the dance moves playlist for the video
 * - Logs user interactions (clicks) and logs the start and end time of each move in the dance to
*    aid labelling of user study test data from IMU sensors for move prediction
 */

package com.example.temiv1.ui.fragments.dance_session

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temiv1.viewmodel.DanceSessionViewModel
import com.example.temiv1.dance.DanceVideoGenerator
import com.example.temiv1.R
import com.example.temiv1.adapters.SongAdapter
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.dance.data.DifficultyLevel
import com.example.temiv1.dance.data.MoveType
import com.example.temiv1.dance.data.Song
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SongSelectionFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SongAdapter
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()
    private lateinit var textView: TextView

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
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.songSelectionTitle)
        textView.textSize = sessionViewModel.textSizeSp // Keep user's specified text size preference

        recyclerView = view.findViewById(R.id.recycler_songs)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // Populate the recycler view with 3 items per row

        fragmentScope.launch {
            delay(1000)
            val requestSelection = TtsRequest.create("Select your song.", false)
            robot?.speak(requestSelection)
        }

        val currentLevel = sessionViewModel.currentLevel.value
        val filteredSongs = allSongs.filter { it.level == currentLevel }
        adapter = SongAdapter(filteredSongs)
        recyclerView.adapter = adapter // Display only the songs for the user's current level

        val continueButton: Button = view.findViewById(R.id.songContinueButton)

        continueButton.setOnClickListener {
            val selectedSong = adapter.getSelectedSong()
            if (selectedSong == null) {
                Toast.makeText(requireContext(), "Select a song to continue", Toast.LENGTH_SHORT).show()
            } else {
                val songString = selectedSong.name
                CsvLogger.logEvent("moves", "song_selection", songString) // Exportable log of selected song

                sessionViewModel.selectedSong.value = selectedSong // Store selected song in sessionViewModel for access in other fragments

                val songDuration = DanceVideoGenerator.getAudioDurationFromRaw(
                    requireContext(), selectedSong.audioResId
                )

                val selectedMoves = sessionViewModel.selectedMoves.value ?: emptyList()
                val allMoves = sessionViewModel.allMoves.value ?: emptyList()

                val moveDurations = DanceVideoGenerator.getClipDurations(
                    requireContext(), selectedMoves
                )

                // Find the rest moves for the user's current level to build the dance video
                val restMove4BeatsArms =
                    allMoves.find { it.level == currentLevel && it.name == "Rest Move x4" && it.type == MoveType.ARM }!! // non-null assertion
                val restMove6BeatsArms =
                    allMoves.find { it.level == currentLevel && it.name == "Rest Move x6" && it.type == MoveType.ARM }!!
                val restMove4BeatsLegs =
                    allMoves.find { it.level == currentLevel && it.name == "Rest Move x4" && it.type == MoveType.LEG }!!
                val restMove6BeatsLegs =
                    allMoves.find { it.level == currentLevel && it.name == "Rest Move x6" && it.type == MoveType.LEG }!!

                // Build the playlist of dance moves for the video given the selected moves, duration of each move and the song, and the rest moves relevant to that level
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

                val movesPlaylistString = movesPlaylist.joinToString("|") { it.move.name }
                val level = sessionViewModel.currentLevel.value?.name ?: "UNKNOWN"
                val dsid = (sessionViewModel.currentDanceSessionId.value ?: 0) + 1 // Keep track of dance session to assist with IMU data labelling
                sessionViewModel.currentDanceSessionId.value = dsid

                CsvLogger.logEvent("moves", "moves_playlist", movesPlaylistString, songDurationMs = songDuration, currentLevel = level, danceSessionId = dsid) // Exportable log of moves playlist, song duration, level, and dance session

                // Exportable log of the breakdown of each move and its start and end time, relevant for labelling the user study test data from IMU sensors to predict dance move
                movesPlaylist.forEachIndexed { index, moveTime ->
                    CsvLogger.logEvent(
                        stream = "moves",
                        eventId = "move_time",
                        value = moveTime.move.name,
                        moveIndex = index,
                        moveStartMs = moveTime.startTimeMs,
                        moveEndMs = moveTime.endTimeMs,
                        moveDurationMs = moveTime.endTimeMs - moveTime.startTimeMs,
                        danceSessionId = dsid
                    )
                }

                Log.d("Dance Video", "Generated playlist: ${movesPlaylist.map { it.move.name }}")
                val counts = movesPlaylist.groupingBy { it.move.name }.eachCount()
                Log.d("DanceVideo", "Move usage counts: $counts") // Debugging logs

                Toast.makeText(
                    context,
                    "Selected ${selectedSong.name} song",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_songSelectionFragment_to_adjustDistanceFragment) // Navigate to next fragment
            }
            }
        }
}
