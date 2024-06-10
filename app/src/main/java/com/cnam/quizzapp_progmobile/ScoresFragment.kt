package com.cnam.quizzapp_progmobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

class ScoresFragment : Fragment() {

    private val args: ScoresFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scores, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getSharedPreferences("QuizApp", Context.MODE_PRIVATE) ?: return
        val allEntries = sharedPref.all
        val scoresTextView = view.findViewById<TextView>(R.id.tvScores)

        val filteredEntries = allEntries.entries.filter { it.key.startsWith(args.username) }
        val scores = filteredEntries.joinToString("\n") { "${it.key}: ${it.value}" }

        Log.d("ScoresFragment", "All entries: $allEntries")
        Log.d("ScoresFragment", "Filtered entries for ${args.username}: $filteredEntries")

        scoresTextView.text = scores

        if (filteredEntries.isEmpty()) {
            Log.d("ScoresFragment", "No scores found for ${args.username}")
        }
    }
}
