package com.example.temiv1

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val name: String,
    val level: DifficultyLevel,
    val bpm: Int,
    val genre: String,
    val audioResId: Int
) : Parcelable
