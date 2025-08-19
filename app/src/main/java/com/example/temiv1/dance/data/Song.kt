/** Data model for a single song with its metadata. */

package com.example.temiv1.dance.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // For passing objects between fragments
data class Song(
    val name: String,
    val level: DifficultyLevel,
    val bpm: Int,
    val genre: String,
    val audioResId: Int
) : Parcelable
