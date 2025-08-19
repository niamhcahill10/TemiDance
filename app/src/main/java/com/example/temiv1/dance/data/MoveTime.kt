/** Start and end time of a given move during a given dance. */

package com.example.temiv1.dance.data

data class MoveTime(
    val move: DanceMove,
    val startTimeMs: Long,
    val endTimeMs: Long
)