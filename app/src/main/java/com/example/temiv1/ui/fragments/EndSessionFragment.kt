package com.example.temiv1.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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

        CsvLogger.logUseTime("time", "end_app")

        textView = view.findViewById(R.id.endText)
        textView.textSize = sessionViewModel.textSizeSp

        fragmentScope.launch {
            delay(1000)
            val endText = TtsRequest.create("Thank you for interacting with me today. The session has now ended. Enjoy your day!", false)
            robot?.speak(endText)
        }

        val uri = CsvLogger.exportToDownloads(requireContext(), fixedName = "temi_latest.csv")
        Log.i("Export", "Saved to Downloads/TemiLogs/temi_latest.csv")
        Toast.makeText(requireContext(), "Exported: $uri", Toast.LENGTH_LONG).show()
    }
}