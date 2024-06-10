package com.cnam.quizzapp_progmobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesFragment : Fragment() {

    private val args: CategoriesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            val categories = getCategories()
            val container = view.findViewById<LinearLayout>(R.id.categoriesContainer)

            categories.forEach { category ->
                val button = Button(requireContext()).apply {
                    text = category.name
                    setOnClickListener {
                        navigateToQuestions(category.name)
                    }
                }
                container.addView(button)
            }
            Log.d("CategoriesFragment", "Displayed categories: $categories")
        }
    }

    private suspend fun getCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            val db = QuizDatabase.getDatabase(requireContext(), viewLifecycleOwner.lifecycleScope)
            db.categoryDao().getAllCategories()
        }
    }

    private fun navigateToQuestions(category: String) {
        val action = CategoriesFragmentDirections.actionCategoriesFragmentToQuestionsFragment(category, args.username)
        findNavController().navigate(action)
    }
}
