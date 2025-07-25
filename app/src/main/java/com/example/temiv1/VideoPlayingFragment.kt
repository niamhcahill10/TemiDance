package com.example.temiv1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.core.net.toUri

class VideoPlaying : BaseFragment() {

    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movesPlaylist = sessionViewModel.movesPlaylist.value ?: emptyList()
        Log.d("VideoPlaying", "Upcoming moves: ${movesPlaylist.map { it.name }}")

        val playerView = view.findViewById<PlayerView>(R.id.playerView)
        val context = requireContext()

        val player = ExoPlayer.Builder(context).build()
        playerView.player = player

        val mediaItems = movesPlaylist.map { move ->
            val uri = "android.resource://${context.packageName}/${move.videoResId}".toUri()
            MediaItem.fromUri(uri)
        }

        player.setMediaItems(mediaItems)
        player.prepare()
        player.playWhenReady = true

    }

}