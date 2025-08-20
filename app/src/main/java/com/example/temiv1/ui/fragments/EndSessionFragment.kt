/**
 * UI fragment for ending the session.
 *
 * - Displays text and plays prompt notifying the user their session has ended and thanking them for their participation
 * - Logs the end time to determine app usage time span and exports csv of full app logs to Temi downloads
 * - Note if using ADB port run "./adb -s [adb device address] pull /sdcard/Download/TemiLogs/temi_latest.csv ."
 *   in terminal inside platform-tools folder to get download from Temi robot onto your computer
 */

package com.example.temiv1.ui.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.temiv1.R
import com.example.temiv1.analytics.CsvLogger
import com.example.temiv1.base.BaseFragment
import com.example.temiv1.viewmodel.DanceSessionViewModel
import com.robotemi.sdk.TtsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EndSessionFragment : BaseFragment() {
    private lateinit var textView: TextView
    private val sessionViewModel: DanceSessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_end_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CsvLogger.logUseTime("time", "end_app") // Exportable log of end time of app usage

        textView = view.findViewById(R.id.endText)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sessionViewModel.textSizeSp) // Keep user's specified text size preference

        fragmentScope.launch {
            delay(1000)
            val endText = TtsRequest.create("The session has now ended. Enjoy the rest of your day!", false)
            robot?.speak(endText)
        }

        val uri = CsvLogger.exportToDownloads(requireContext(), fixedName = "temi_latest.csv") // Export csv of all app logs to Temi downloads
        Log.i("Export", "Saved to Downloads/TemiLogs/temi_latest.csv") // Debugging log to check export ran
        Toast.makeText(requireContext(), "Exported: $uri", Toast.LENGTH_LONG).show()
    }
}