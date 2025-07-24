package com.example.temiv1

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DanceMove(
    val name: String,
    val imageResId: Int,
    val level: DifficultyLevel,
    val videoResId: Int
) : Parcelable
