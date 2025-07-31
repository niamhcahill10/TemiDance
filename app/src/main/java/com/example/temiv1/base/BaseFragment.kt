package com.example.temiv1.base

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.robotemi.sdk.Robot
import com.robotemi.sdk.SttLanguage
import com.robotemi.sdk.listeners.OnRobotReadyListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

open class BaseFragment : Fragment(), OnRobotReadyListener, Robot.AsrListener {
    companion object {
        var globalTextSizeSp: Float = 38f
    }

    protected var robot: Robot? = null

    protected var isTemiDevice = Build.MANUFACTURER.equals("temi", ignoreCase = true)

    protected val fragmentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseFragment", "BaseFragment onCreate called — isTemiDevice = $isTemiDevice")
        if (isTemiDevice) {
            robot = Robot.getInstance()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton?>(R.id.backButton)?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isTemiDevice) {
            robot?.addOnRobotReadyListener(this)
            robot?.addAsrListener(this)
        }
    }

    override fun onPause() {
        if (isTemiDevice) {
            robot?.removeOnRobotReadyListener(this)
            robot?.removeAsrListener(this)
        }
        super.onPause()
    }

    override fun onAsrResult(asrResult: String, sttLanguage: SttLanguage) {
        Log.d("ASR", "Temi heard: $asrResult")
        handleAsr(asrResult.lowercase().trim())
    }

    // Child fragments override this if needed
    open fun handleAsr(command: String) {
        if (!isTemiDevice) return

        when (command) {
            "back" -> {
                Log.d("ASR", "Voice command: back")
                findNavController().popBackStack()
            }
        }
    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            Log.d("Temi", "Robot is ready")
        }
    }

    open fun releasePlayer() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        fragmentScope.coroutineContext.cancelChildren() // Cancels child jobs safely
    }

    open fun updateTextSize(newSize: Float) {
        globalTextSizeSp += newSize
    }

}