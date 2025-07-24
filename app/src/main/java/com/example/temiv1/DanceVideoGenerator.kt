package com.example.temiv1

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.temiv1.DanceMove
import androidx.core.net.toUri

object DanceVideoGenerator {

    // function takes list of dance move objects and maps them by id and clip length, this is returned as key value pairs, the associate function transforms the list into a map
    fun getClipDurations(context: Context, moves: List<DanceMove>): Map<Int, Long> {
        return moves.associate { move ->
            move.videoResId to getDurationFromRaw(context, move.videoResId)
        }
    }

    // function uses the resId generated in DanceMove class to find the dance video for the selected moves and uses imported MediaMetadataRetriever to get the clip duration
    private fun getDurationFromRaw(context: Context, resId: Int): Long {
        val retriever = MediaMetadataRetriever()
        val uri = "android.resource://${context.packageName}/$resId".toUri() // output e.g. android.resource://com.example.temiv1/2131623936
        retriever.setDataSource(context, uri) // uri now the data source
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        return durationStr?.toLongOrNull() ?: 5000L
    }

    fun buildDanceVideo(
        selectedMoves: List<DanceMove>,
        moveDurations: Map<Int, Long>,
        targetDurationMillis: Long
    ): List<DanceMove> {
        if (selectedMoves.isEmpty()) return emptyList()

        val playlist = mutableListOf<DanceMove>()
        val usageCount = mutableMapOf<DanceMove, Int>().withDefault { 0 }
        var totalDuration = 0L
        val random = java.util.Random()

        while(totalDuration < targetDurationMillis) {
            val lastMove = playlist.lastOrNull()

            val choices = if (lastMove != null && selectedMoves.size > 1) {
                selectedMoves.filter { it != lastMove }
            } else {
                selectedMoves
            }

            val minUsage = choices.minOf { usageCount.getValue(it) }
            val balancedChoices = choices.filter { usageCount.getValue(it) == minUsage }

            val nextMove = balancedChoices[random.nextInt(balancedChoices.size)]
            val clipDuration = moveDurations[nextMove.videoResId] ?: 5000L

            playlist.add(nextMove)
            usageCount[nextMove] = usageCount.getValue(nextMove) + 1
            totalDuration += clipDuration

        }

        return playlist
    }
}