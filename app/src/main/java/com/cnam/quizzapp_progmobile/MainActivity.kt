package com.cnam.quizzapp_progmobile

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)

        val db = QuizDatabase.getDatabase(applicationContext, lifecycleScope)
        verifyDatabasePopulation(db)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun verifyDatabasePopulation(db: QuizDatabase) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val categories = db.categoryDao().getAllCategories()
                    categories.forEach { category ->
                        val questions = db.questionDao().getQuestionsByCategory(category.name)
                        Log.d("MainActivity", "Questions in ${category.name}: ${questions.map { it.text }}")
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error verifying database population", e)
                }
            }
        }
    }
}
