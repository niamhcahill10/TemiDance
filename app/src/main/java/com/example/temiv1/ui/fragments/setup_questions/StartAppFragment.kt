package com.example.temiv1.ui.fragments.setup_questions

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment


class StartAppFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton: ImageButton = view.findViewById(R.id.backButton)
        backButton.visibility = View.INVISIBLE

        val contentResolver = requireContext().contentResolver

        val newBrightness = (255 * 0.75).toInt()

        Settings.System.putInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            newBrightness
        )

        CsvLogger.logEvent("settings", "brightness_set", newBrightness.toString())

        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val newVolume = (maxVolume * 0.4).toInt().coerceAtMost(maxVolume)

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            newVolume,
            0
        )

        CsvLogger.logEvent("settings", "volume_set", newVolume.toString())

        val startButton: Button = view.findViewById(R.id.startButton)
        startButton.setOnClickListener {
            findNavController().navigate(R.id.action_startAppFragment_to_setupIntroFragment)
        }
    }
}
