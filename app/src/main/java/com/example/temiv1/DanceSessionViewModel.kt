package com.example.temiv1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.temiv1.DanceMove

// ViewModel can be accessed across fragments allowing information to be passed across
class DanceSessionViewModel : ViewModel() {
    // live data can be updated at any point across fragments
    val selectedMoves = MutableLiveData<List<DanceMove>>()
    val movesPlaylist = MutableLiveData<List<DanceMove>>()

}
