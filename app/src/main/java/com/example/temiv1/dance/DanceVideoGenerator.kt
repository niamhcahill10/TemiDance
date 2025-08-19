/**
 * Generates a dance video object with helper methods:
 *
 * - Get durations of each move video and put into a map (required for generating correct length playlist of dance moves)
 * - Get the song duration (use as maximum length for playlist of dance moves)
 * - Build a moves playlist with:
 *      - rests between each move
 *      - balanced repeats of each move
 *      - no consecutive duplicate moves
 *      - total length within song duration
 */

package com.example.temiv1.dance

import android.content.Context
import android.media.MediaMetadataRetriever
import com.example.temiv1.dance.data.DanceMove
import androidx.core.net.toUri
import com.example.temiv1.dance.data.MoveTime
import com.example.temiv1.dance.data.MoveType

// Generate a dance for the user given their selected moves
object DanceVideoGenerator {

    // Takes list of dance move objects and maps them by id and clip length, this is returned as key value pairs, the associate function transforms the list into a map
    fun getClipDurations(context: Context, moves: List<DanceMove>): Map<Int, Long> {
        return moves.associate { move ->
            move.videoResId to getDurationFromRaw(context, move.videoResId)
        }
    }

    // Uses the resId generated in DanceMove class to find the dance video for the selected moves and uses imported MediaMetadataRetriever to get the clip duration
    private fun getDurationFromRaw(context: Context, resId: Int): Long {
        val retriever = MediaMetadataRetriever()
        val uri = "android.resource://${context.packageName}/$resId".toUri() // outputs a uri e.g. android.resource://com.example.temiv1/2131623936
        retriever.setDataSource(context, uri) // uri now the data source
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        return durationStr?.toLongOrNull() ?: 5000L // fallback 5s
    }

    // Get song duration
    fun getAudioDurationFromRaw(context: Context, resId: Int): Long {
        val retriever = MediaMetadataRetriever()
        val uri = "android.resource://${context.packageName}/$resId".toUri()
        retriever.setDataSource(context, uri)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        return durationStr?.toLongOrNull() ?: 60000L // fallback 60s
    }

    // Builds a list of dance moves to be performed, returns the moves with their start and end times during the song
    fun buildDanceMovesPlaylist(
        selectedMoves: List<DanceMove>,
        moveDurations: Map<Int, Long>,
        targetDurationMillis: Long,
        restMove4BeatsArms: DanceMove,
        restMove6BeatsArms: DanceMove,
        restMove4BeatsLegs: DanceMove,
        restMove6BeatsLegs: DanceMove,
    ): List<MoveTime> {
        if (selectedMoves.isEmpty()) return emptyList()

        val timedPlaylist = mutableListOf<MoveTime>() // Create playlist of dance moves with start and end time
        val usageCount = mutableMapOf<DanceMove, Int>().withDefault { 0 } // Count repetitions of each move for balance
        var totalDuration = 0L
        val random = java.util.Random()

        val firstMove = selectedMoves.random()
        val firstMoveDuration = moveDurations[firstMove.videoResId] ?: 5000L
        usageCount[firstMove] = usageCount.getValue(firstMove) + 1

        val firstRestMove = if (firstMove.type == MoveType.ARM) restMove6BeatsArms else restMove6BeatsLegs // Different rest video before leg and arm movements to improve continuity between rest and next move
        val firstRestDuration = moveDurations[firstRestMove.videoResId] ?: 3000L
        timedPlaylist += MoveTime(firstRestMove, totalDuration, firstRestDuration) // Add the dance move to the timed playlist with its start and end times
        totalDuration += firstRestDuration

        val firstMoveEnd = totalDuration + firstMoveDuration
        timedPlaylist += MoveTime(firstMove, totalDuration, firstMoveEnd)
        totalDuration += firstMoveDuration

        // Keep adding moves until the playlist is within 14 seconds of the song length
        while(totalDuration < targetDurationMillis - 14_000L) {
            val lastMove = timedPlaylist.lastOrNull { !it.move.name.contains("rest", ignoreCase = true) }

            // Ensure next move is a different move to the last move if more than one move was selected by the user
            val choices = if (lastMove != null && selectedMoves.size > 1) {
                selectedMoves.filter { it.name != lastMove.move.name }
            } else {
                selectedMoves
            }

            // Get least used move to ensure repetitions are balanced
            val minUsage = choices.minOf { usageCount.getValue(it) }
            val balancedChoices = choices.filter { usageCount.getValue(it) == minUsage }

            val nextMove = balancedChoices[random.nextInt(balancedChoices.size)] // Randomly select from the list of least repeated dance moves
            val nextMoveDuration = moveDurations[nextMove.videoResId] ?: 5000L

            val nextRestMove = if (nextMove.type == MoveType.ARM) restMove4BeatsArms else restMove4BeatsLegs
            val nextRestDuration = moveDurations[nextRestMove.videoResId] ?: 2000L

            val nextRestEnd = totalDuration + nextRestDuration
            timedPlaylist += MoveTime(nextRestMove, totalDuration, nextRestEnd)
            totalDuration += nextRestDuration

            val nextMoveEnd = totalDuration + nextMoveDuration
            timedPlaylist += MoveTime(nextMove, totalDuration, nextMoveEnd) // add moves to the playlist
            usageCount[nextMove] =
                usageCount.getValue(nextMove) + 1 // update the number of times the move is used
            totalDuration += nextMoveDuration // update the clip duration

        }

        return timedPlaylist // Returns a timed playlist where each item is a MoveTime object containing a DanceMove object and its start and end times in the dance
    }
}