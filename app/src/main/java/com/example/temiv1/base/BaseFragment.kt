/**
 * Base class for all fragments with shared robot + UI handling.
 *
 * - Sets up Temi robot and listeners
 * - Provides overridable hooks for child functionality
 * - Cleans up UI, media, and coroutines when the view is destroyed
 */

package com.example.temiv1.base

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.robotemi.sdk.Robot
import com.robotemi.sdk.SttLanguage
import com.robotemi.sdk.listeners.OnRobotReadyListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

open class BaseFragment : Fragment(), OnRobotReadyListener, Robot.AsrListener {

    // Nullable handle to Temi SDK only assigned when using hardware
    protected var robot: Robot? = null

    // Know when to get robot instance
    protected var isTemiDevice = Build.MANUFACTURER.equals("temi", ignoreCase = true)

    // Scopes out tasks for each fragment that run asynchronously, supervisor job ensures others run even if one is cancelled
    protected val fragmentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Get robot connection object to communicate to robot if not in simulation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseFragment", "BaseFragment onCreate called — isTemiDevice = $isTemiDevice")
        if (isTemiDevice) {
            robot = Robot.getInstance()
        }
    }

    // Back button same for every fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton?>(R.id.backButton)?.setOnClickListener {
            CsvLogger.logEvent("recovery", "back_button", this::class.simpleName ?: "unknown_fragment")
            findNavController().popBackStack()
        }
    }

    // Make fragment view interactive once robot ready
    override fun onResume() {
        super.onResume()
        if (isTemiDevice) {
            robot?.addOnRobotReadyListener(this)
            robot?.addAsrListener(this)
        }
    }

    // When leaving fragment unregister listeners to avoid leaks between fragments
    override fun onPause() {
        if (isTemiDevice) {
            robot?.removeOnRobotReadyListener(this)
            robot?.removeAsrListener(this)
        }
        super.onPause()
    }

    // Log and process speech
    override fun onAsrResult(asrResult: String, sttLanguage: SttLanguage) {
        Log.d("ASR", "Temi heard: $asrResult")
        handleAsr(asrResult.lowercase().trim())
    }

    // Child fragments override this if needed to process speech results into actions
    open fun handleAsr(command: String) {
        if (!isTemiDevice) return
    }

    // Once robot initialised hide the top bar
    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            Log.d("Temi", "Robot is ready")
            robot?.hideTopBar()
        }
    }

    // Hook for subclasses to release media resources when the view is destroyed
    open fun releasePlayer() {
    }

    // Stop fragment interaction when navigating away - shut down and release media playing, unregister UI listeners, and cancel coroutines
    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        fragmentScope.coroutineContext.cancelChildren() // Cancels child jobs safely
    }

}