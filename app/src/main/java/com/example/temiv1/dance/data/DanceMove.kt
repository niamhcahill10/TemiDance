/** Data model for a single dance move with its metadata. */

package com.example.temiv1.dance.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class MoveType { ARM, LEG }

@Parcelize
data class DanceMove(
    val name: String,
    val imageResId: Int,
    val level: DifficultyLevel,
    val type: MoveType,
    val videoResId: Int
) : Parcelable
