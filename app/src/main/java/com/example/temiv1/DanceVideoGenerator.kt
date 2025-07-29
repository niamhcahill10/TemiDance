package com.example.temiv1

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.temiv1.DanceMove
import androidx.core.net.toUri

// Generate a dance for the user given their selected moves
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

    fun getAudioDurationFromRaw(context: Context, resId: Int): Long {
        val retriever = MediaMetadataRetriever()
        val uri = "android.resource://${context.packageName}/$resId".toUri()
        retriever.setDataSource(context, uri)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        return durationStr?.toLongOrNull() ?: 60000L // fallback 60s
    }

    // function to build a list if dance moves to be performed, each dance move can be performed multiple times but not back to back, the length of the video must not exceed the music video selected (target duration)
    fun buildDanceMovesPlaylist(
        selectedMoves: List<DanceMove>,
        moveDurations: Map<Int, Long>,
        targetDurationMillis: Long,
        restMove4BeatsArms: DanceMove,
        restMove6BeatsArms: DanceMove,
        restMove4BeatsLegs: DanceMove,
        restMove6BeatsLegs: DanceMove,
    ): List<DanceMove> {
        if (selectedMoves.isEmpty()) return emptyList()

        val playlist = mutableListOf<DanceMove>()
        val usageCount = mutableMapOf<DanceMove, Int>().withDefault { 0 }
        var totalDuration = 0L
        val random = java.util.Random()

        val firstMove = selectedMoves.random()
        val firstMoveDuration = moveDurations[firstMove.videoResId] ?: 5000L

        val firstRestMove = if (firstMove.type == MoveType.ARM) restMove6BeatsArms else restMove6BeatsLegs
        val firstRestDuration = moveDurations[firstRestMove.videoResId] ?: 3000L
        playlist.add(firstRestMove)
        totalDuration += firstRestDuration

        playlist.add(firstMove)
        totalDuration += firstMoveDuration

        // keep adding moves until the playlist is the same length as the song selected
        while(totalDuration < targetDurationMillis - 14_000L) {
            val lastMove = playlist.lastOrNull { !it.name.contains("rest", ignoreCase = true) }

            // ensure next move is a different move if more than one is selected
            val choices = if (lastMove != null && selectedMoves.size > 1) {
                selectedMoves.filter { it != lastMove }
            } else {
                selectedMoves
            }

            // get least used move to ensure moves are repeated as close to an equal number of times as possible given duration constraints
            val minUsage = choices.minOf { usageCount.getValue(it) }
            val balancedChoices = choices.filter { usageCount.getValue(it) == minUsage }

            val nextMove = balancedChoices[random.nextInt(balancedChoices.size)] // randomly select from list of least repeated dance moves
            val nextMoveDuration = moveDurations[nextMove.videoResId] ?: 5000L // get clip duration, assume 5 seconds if not able to obtain - maybe should be updated to an error

            val nextRestMove = if (nextMove.type == MoveType.ARM) restMove4BeatsArms else restMove4BeatsLegs
            val nextRestDuration = moveDurations[nextRestMove.videoResId] ?: 2000L

            playlist.add(nextRestMove)
            totalDuration += nextRestDuration

            playlist.add(nextMove) // add moves to the playlist
            usageCount[nextMove] =
                usageCount.getValue(nextMove) + 1 // update the number of times the move is used
            totalDuration += nextMoveDuration // update the clip duration

        }

        return playlist
    }
}