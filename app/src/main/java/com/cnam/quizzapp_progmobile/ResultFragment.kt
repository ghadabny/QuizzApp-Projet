package com.cnam.quizzapp_progmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class ResultFragment : Fragment() {

    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvResult).text = "Correct Answers: ${args.correctAnswers}"

        view.findViewById<Button>(R.id.btnBackToCategories).setOnClickListener {
            val action = ResultFragmentDirections.actionResultFragmentToCategoriesFragment(args.username)
            findNavController().navigate(action)
        }

        view.findViewById<Button>(R.id.btnViewScore).setOnClickListener {
            val action = ResultFragmentDirections.actionResultFragmentToScoresFragment(args.username)
            findNavController().navigate(action)
        }
    }
}
