package com.example.temiv1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.core.net.toUri
import androidx.media3.common.Player

class VideoPlaying : BaseFragment() {

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

        Log.d("VideoPlaying", "Upcoming moves: ${movesPlaylist.map { it.name }}")
        Log.d("VideoPlaying", "Selected song: ${selectedSong?.genre}}")

        val playerView = view.findViewById<PlayerView>(R.id.playerView)
        val upNextTextOverlay = view.findViewById<TextView>(R.id.upNextTextOverlay)
        val context = requireContext()

        // Video player
        videoPlayer = ExoPlayer.Builder(context).build().also { player ->
            playerView.player = player // should appear in playerView

            val mediaItems = movesPlaylist.map { move ->
                val uri = "android.resource://${context.packageName}/${move.videoResId}".toUri()
                MediaItem.fromUri(uri)
            }

            player.setMediaItems(mediaItems)
            player.prepare()

            player.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val currentIndex = player.currentMediaItemIndex
                    val currentMove = movesPlaylist.getOrNull(currentIndex)

                    val isRestMove = currentMove != null && currentMove.name.contains("rest", ignoreCase = true)

                    if (isRestMove) {
                        val nextMove = movesPlaylist
                            .subList(currentIndex + 1, movesPlaylist.size)
                            .firstOrNull { !it.name.contains("rest", ignoreCase = true) }

                        val nextMoveName = nextMove?.name ?: "Next move coming..."
                        upNextTextOverlay.text = getString(R.string.next_move_label, nextMoveName)
                        upNextTextOverlay.visibility = View.VISIBLE
                    } else {
                        upNextTextOverlay.visibility = View.INVISIBLE
                    }
                }
            })

            val nextMove = movesPlaylist
                .drop(1)
                .firstOrNull { !it.name.contains("rest", ignoreCase = true) }

            val nextMoveName = nextMove?.name ?: "Next move coming..."
            upNextTextOverlay.text = getString(R.string.next_move_label, nextMoveName)
            upNextTextOverlay.visibility = View.VISIBLE

        }

        // Audio player
        if (selectedSong != null) {
            audioPlayer = ExoPlayer.Builder(context).build().also { player ->
                val uri = "android.resource://${context.packageName}/${selectedSong.audioResId}".toUri()
                val mediaItem = MediaItem.fromUri(uri)

                player.setMediaItem(mediaItem)
                player.prepare()
            }
        }

        videoPlayer?.playWhenReady = true
        audioPlayer?.playWhenReady = true

        }



    }