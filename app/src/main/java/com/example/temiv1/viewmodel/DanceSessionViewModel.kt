package com.example.temiv1.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.temiv1.dance.data.DanceMove
import com.example.temiv1.dance.data.DifficultyLevel
import com.example.temiv1.dance.data.Song

// ViewModel can be accessed across fragments allowing information to be passed across
class DanceSessionViewModel : ViewModel() {
    // live data can be updated at any point across fragments
    val currentLevel = MutableLiveData(DifficultyLevel.EASY)
    val selectedMoves = MutableLiveData<List<DanceMove>>()
    val movesPlaylist = MutableLiveData<List<DanceMove>>()
    val allMoves = MutableLiveData<List<DanceMove>>()
    val selectedSong = MutableLiveData<Song>()

}
