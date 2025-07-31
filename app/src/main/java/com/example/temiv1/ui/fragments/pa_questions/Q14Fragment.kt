package com.example.temiv1.ui.fragments.pa_questions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.temiv1.R
import com.example.temiv1.base.BaseFragment

class Q14Fragment : BaseFragment() {
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_q14, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.q14)
        textView.textSize = globalTextSizeSp
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val q1Button: Button = view.findViewById(R.id.q1Button)
//        q1Button.setOnClickListener {
//            findNavController().navigate(R.id.action_q1Fragment_to_q2Fragment)
//        }
//    }
}
