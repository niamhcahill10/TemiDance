package com.example.temiv1.ui.fragments.setup_questions

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.provider.Settings
import androidx.core.net.toUri
import com.example.temiv1.R
import com.example.temiv1.base.BaseFragment


class SetupQ1Fragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup_q1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentScope.launch {
            delay(1000)
            val q1 = TtsRequest.create(
                "Welcome! This app will go through some physical capabilities questions and generate dance videos for you to follow based on the moves you select. First lets make sure the settings are right for you. Can you hear the audio or see the screen?",
                false
            )
            robot?.askQuestion(q1)
        }

        val yesButton: Button = view.findViewById(R.id.yesButton)
        yesButton.setOnClickListener{
            onYesSelected()
        }

        val noButton: Button = view.findViewById(R.id.noButton)
        noButton.setOnClickListener{
            onNoSelected()
        }
    }

    private fun onYesSelected() {
        Log.d("Q1Fragment", "YES button clicked — navigating")
        findNavController().navigate(R.id.action_q1Fragment_to_q2Fragment)
    }

    private fun onNoSelected() {
        if (!isTemiDevice) return

        if (Settings.System.canWrite(requireContext())) {
            Settings.System.putInt(
                requireContext().contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                200
            )
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = "package:${requireContext().packageName}".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val desiredVolume = (maxVolume * 0.75).toInt()

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            desiredVolume,
            0
        )
    }

    override fun handleAsr(command: String) {
        if (!isTemiDevice) return
        when (command) {
            "back" -> {
                robot?.speak(
                    TtsRequest.create("You're already at the start. Please say or select yes or no to proceed", false)
                )
            }
            "yes" -> onYesSelected()
            "no" -> onNoSelected()
            else -> robot?.askQuestion(TtsRequest.create("Please say yes or no.", false))
        }
    }
}
