package com.cnam.quizzapp_progmobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionsFragment : Fragment() {

    private val args: QuestionsFragmentArgs by navArgs()

    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var correctAnswers = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            questions = getQuestionsForCategory(args.category)
            Log.d("QuestionsFragment", "Fetched ${questions.size} questions for category ${args.category}")
            if (questions.isNotEmpty()) {
                displayQuestion(view)
            } else {
                Log.d("QuestionsFragment", "No questions found for category ${args.category}")
            }
        }

        view.findViewById<Button>(R.id.btnNextQuestion).setOnClickListener {
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                displayQuestion(view)
            } else {
                saveScore(args.category, correctAnswers)
                val action = QuestionsFragmentDirections.actionQuestionsFragmentToResultFragment(correctAnswers, args.username)
                findNavController().navigate(action)
                Log.d("QuestionsFragment", "Quiz finished, navigating to results with score: $correctAnswers")
            }
        }
    }

    private fun displayQuestion(view: View) {
        val currentQuestion = questions[currentQuestionIndex]
        view.findViewById<TextView>(R.id.tvQuestion).text = currentQuestion.text
        Log.d("QuestionsFragment", "Displaying question: ${currentQuestion.text}")

        val answers = listOf(currentQuestion.correctAnswer) + currentQuestion.wrongAnswers
        val shuffledAnswers = answers.shuffled()

        val btnAnswer1 = view.findViewById<Button>(R.id.btnAnswer1)
        val btnAnswer2 = view.findViewById<Button>(R.id.btnAnswer2)
        val btnAnswer3 = view.findViewById<Button>(R.id.btnAnswer3)
        val btnAnswer4 = view.findViewById<Button>(R.id.btnAnswer4)

        btnAnswer1.text = shuffledAnswers[0]
        btnAnswer2.text = shuffledAnswers[1]
        btnAnswer3.text = shuffledAnswers[2]
        btnAnswer4.text = shuffledAnswers[3]

        val answerButtons = listOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4)

        // Re-enable the answer buttons for the new question
        answerButtons.forEach { it.isEnabled = true }

        answerButtons.forEach { button ->
            button.setOnClickListener {
                if (button.text == currentQuestion.correctAnswer) {
                    correctAnswers++
                }
                answerButtons.forEach { it.isEnabled = false } // Disable buttons after an answer is selected
            }
        }
    }

    private suspend fun getQuestionsForCategory(category: String): List<Question> {
        return withContext(Dispatchers.IO) {
            val db = QuizDatabase.getDatabase(requireContext(), viewLifecycleOwner.lifecycleScope)
            db.questionDao().getQuestionsByCategory(category)
        }
    }

    private fun saveScore(category: String, score: Int) {
        val sharedPref = activity?.getSharedPreferences("QuizApp", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            val key = "${args.username}_${category}"
            Log.d("QuestionsFragment", "Saving score: $score for key: $key")
            putInt(key, score)
            apply()
        }
    }
}
