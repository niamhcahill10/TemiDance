/**
 * UI fragment for playing the video of dance moves to follow with the selected song.
 *
 * - Uses ExoPlayer to play each move video in the playlist one after the other
 * - Displays overlay text on rest moves to inform the user of the next upcoming move
 * - Plays the selected song at the same time
 */

package com.example.temiv1.ui.fragments.dance_session

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import androidx.core.net.toUri
import androidx.media3.common.Player
import androidx.navigation.fragment.findNavController
import com.example.temiv1.viewmodel.DanceSessionViewModel
import com.example.temiv1.R
import com.example.temiv1.base.BaseFragment

class VideoPlayingFragment : BaseFragment() {

    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    private var videoPlayer: ExoPlayer? = null
    private var audioPlayer: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movesPlaylist = sessionViewModel.movesPlaylist.value ?: emptyList()
        val selectedSong = sessionViewModel.selectedSong.value

        Log.d("VideoPlaying", "Upcoming moves: ${movesPlaylist.map { it.move.name }}")
        Log.d("VideoPlaying", "Selected song: ${selectedSong?.genre}}") // Debugging logs

        val playerView = view.findViewById<PlayerView>(R.id.playerView)
        val upNextTextOverlay = view.findViewById<TextView>(R.id.upNextTextOverlay)
        val context = requireContext()

        // Video player
        videoPlayer = ExoPlayer.Builder(context).build().also { player ->
            playerView.player = player

            val mediaItems = movesPlaylist.map { move ->
                val uri = "android.resource://${context.packageName}/${move.move.videoResId}".toUri()
                MediaItem.fromUri(uri)
            }

            player.setMediaItems(mediaItems) // Player will play dance move videos in playlist order
            player.prepare()

            // Listener for media item transitions to display the next upcoming dance move during the preceding rest move
            player.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val currentIndex = player.currentMediaItemIndex
                    val currentMove = movesPlaylist.getOrNull(currentIndex)

                    val isRestMove = currentMove != null && currentMove.move.name.contains("rest", ignoreCase = true)

                    if (isRestMove) {
                        val nextMove = movesPlaylist
                            .subList(currentIndex + 1, movesPlaylist.size)
                            .firstOrNull { !it.move.name.contains("rest", ignoreCase = true) }

                        val nextMoveName = nextMove?.move?.name ?: "Next move coming..."
                        upNextTextOverlay.text = getString(R.string.next_move_label, nextMoveName)
                        upNextTextOverlay.visibility = View.VISIBLE
                    } else {
                        upNextTextOverlay.visibility = View.INVISIBLE
                    }
                }
            })

            // Initial overlay setup to display next move at the start before any media transition has happened
            val nextMove = movesPlaylist
                .drop(1)
                .firstOrNull { !it.move.name.contains("rest", ignoreCase = true) }

            if (nextMove != null) {
                val nextMoveName = nextMove.move.name
                upNextTextOverlay.text = getString(R.string.next_move_label, nextMoveName)
                upNextTextOverlay.visibility = View.VISIBLE
            } else {
                upNextTextOverlay.visibility = View.INVISIBLE
            }

        }

        // Audio player
        if (selectedSong != null) {
            audioPlayer = ExoPlayer.Builder(context).build().also { player ->
                val uri = "android.resource://${context.packageName}/${selectedSong.audioResId}".toUri()
                val mediaItem = MediaItem.fromUri(uri)

                player.setMediaItem(mediaItem)
                player.prepare()

                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            findNavController().navigate(R.id.action_videoPlayingFragment_to_readjustDistanceFragment) // Navigate to next fragment once song ends (dance video length always within song duration)
                        }
                    }
                })

            }
        }

        videoPlayer?.playWhenReady = true
        audioPlayer?.playWhenReady = true

        }

    // Release the video and audio players to prevent leakage across fragments, called in the base fragment onDestroyView
    override fun releasePlayer() {
        videoPlayer?.release()
        videoPlayer = null

        audioPlayer?.release()
        audioPlayer = null
    }
}