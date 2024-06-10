package com.cnam.quizzapp_progmobile

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Question::class, Category::class], version = 1)
@TypeConverters(Converters::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                )
                    .addCallback(QuizDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class QuizDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        Log.d("QuizDatabase", "Populating database on creation")
                        populateDatabase(database.categoryDao(), database.questionDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(categoryDao: CategoryDao, questionDao: QuestionDao) {
            val categories = listOf(
                Category("Jeux Vidéo"),
                Category("Films"),
                Category("Art & Littérature"),
                Category("Musique"),
                Category("Culture Générale"),
                Category("Sports")
            )
            categoryDao.insertAll(categories)
            Log.d("QuizDatabase", "Inserted categories")

            val questions = listOf(
                // Jeux Vidéo
                Question(
                    category = "Jeux Vidéo",
                    text = "Quel est le nom du personnage principal de la série Legend of Zelda ?",
                    correctAnswer = "Link",
                    wrongAnswers = listOf("Zelda", "Ganon", "Navi")
                ),
                Question(
                    category = "Jeux Vidéo",
                    text = "Quelle entreprise a développé le jeu 'Fortnite' ?",
                    correctAnswer = "Epic Games",
                    wrongAnswers = listOf("Activision", "Electronic Arts", "Ubisoft")
                ),
                Question(
                    category = "Jeux Vidéo",
                    text = "Quel est le nom du personnage principal de la série 'Halo' ?",
                    correctAnswer = "Master Chief",
                    wrongAnswers = listOf("Arbiter", "Cortana", "Sergeant Johnson")
                ),
                Question(
                    category = "Jeux Vidéo",
                    text = "Dans quel jeu vidéo pouvez-vous explorer la ville de 'Vice City' ?",
                    correctAnswer = "Grand Theft Auto",
                    wrongAnswers = listOf("Saints Row", "Mafia", "L.A. Noire")
                ),
                Question(
                    category = "Jeux Vidéo",
                    text = "Quelle entreprise a développé 'The Witcher 3: Wild Hunt' ?",
                    correctAnswer = "CD Projekt Red",
                    wrongAnswers = listOf("Bethesda", "Bioware", "Ubisoft")
                ),
                // Films
                Question(
                    category = "Films",
                    text = "Qui a réalisé le film 'Inception' ?",
                    correctAnswer = "Christopher Nolan",
                    wrongAnswers = listOf("Steven Spielberg", "James Cameron", "Quentin Tarantino")
                ),
                Question(
                    category = "Films",
                    text = "Quel film a remporté l'Oscar du meilleur film en 2020 ?",
                    correctAnswer = "Parasite",
                    wrongAnswers = listOf("1917", "Joker", "Once Upon a Time in Hollywood")
                ),
                Question(
                    category = "Films",
                    text = "Qui joue le rôle principal dans 'Forrest Gump' ?",
                    correctAnswer = "Tom Hanks",
                    wrongAnswers = listOf("Brad Pitt", "Johnny Depp", "Leonardo DiCaprio")
                ),
                Question(
                    category = "Films",
                    text = "Quel est le titre du premier film de la saga 'Harry Potter' ?",
                    correctAnswer = "Harry Potter à l'école des sorciers",
                    wrongAnswers = listOf("Harry Potter et la Chambre des Secrets", "Harry Potter et le Prisonnier d'Azkaban", "Harry Potter et la Coupe de Feu")
                ),
                Question(
                    category = "Films",
                    text = "Qui a réalisé 'Pulp Fiction' ?",
                    correctAnswer = "Quentin Tarantino",
                    wrongAnswers = listOf("Martin Scorsese", "Francis Ford Coppola", "Ridley Scott")
                ),
                // Art & Littérature
                Question(
                    category = "Art & Littérature",
                    text = "Qui a peint la Joconde ?",
                    correctAnswer = "Leonardo da Vinci",
                    wrongAnswers = listOf("Vincent van Gogh", "Pablo Picasso", "Claude Monet")
                ),
                Question(
                    category = "Art & Littérature",
                    text = "Qui a écrit 'Orgueil et Préjugés' ?",
                    correctAnswer = "Jane Austen",
                    wrongAnswers = listOf("Charlotte Brontë", "Mary Shelley", "Charles Dickens")
                ),
                Question(
                    category = "Art & Littérature",
                    text = "Quel est le titre du roman célèbre écrit par George Orwell ?",
                    correctAnswer = "1984",
                    wrongAnswers = listOf("Brave New World", "Fahrenheit 451", "The Catcher in the Rye")
                ),
                Question(
                    category = "Art & Littérature",
                    text = "Qui a sculpté 'David' ?",
                    correctAnswer = "Michel-Ange",
                    wrongAnswers = listOf("Donatello", "Raphael", "Leonardo da Vinci")
                ),
                Question(
                    category = "Art & Littérature",
                    text = "Qui a écrit 'Les Misérables' ?",
                    correctAnswer = "Victor Hugo",
                    wrongAnswers = listOf("Alexandre Dumas", "Émile Zola", "Gustave Flaubert")
                ),
                // Musique
                Question(
                    category = "Musique",
                    text = "Qui est connu comme le Roi de la Pop ?",
                    correctAnswer = "Michael Jackson",
                    wrongAnswers = listOf("Elvis Presley", "Prince", "Madonna")
                ),
                Question(
                    category = "Musique",
                    text = "Quel groupe a sorti l'album 'Dark Side of the Moon' ?",
                    correctAnswer = "Pink Floyd",
                    wrongAnswers = listOf("The Beatles", "Led Zeppelin", "The Rolling Stones")
                ),
                Question(
                    category = "Musique",
                    text = "Qui a écrit la symphonie 'Eroica' ?",
                    correctAnswer = "Beethoven",
                    wrongAnswers = listOf("Mozart", "Bach", "Handel")
                ),
                Question(
                    category = "Musique",
                    text = "Quel groupe est connu pour la chanson 'Bohemian Rhapsody' ?",
                    correctAnswer = "Queen",
                    wrongAnswers = listOf("The Who", "The Rolling Stones", "The Beatles")
                ),
                Question(
                    category = "Musique",
                    text = "Quel artiste est connu pour l'album 'Purple Rain' ?",
                    correctAnswer = "Prince",
                    wrongAnswers = listOf("Michael Jackson", "David Bowie", "Elton John")
                ),
                // Culture Générale
                Question(
                    category = "Culture Générale",
                    text = "Quelle est la capitale de la France ?",
                    correctAnswer = "Paris",
                    wrongAnswers = listOf("Londres", "Berlin", "Madrid")
                ),
                Question(
                    category = "Culture Générale",
                    text = "Quelle est la plus grande planète du système solaire ?",
                    correctAnswer = "Jupiter",
                    wrongAnswers = listOf("Saturne", "Terre", "Mars")
                ),
                Question(
                    category = "Culture Générale",
                    text = "Combien de continents y a-t-il sur Terre ?",
                    correctAnswer = "7",
                    wrongAnswers = listOf("5", "6", "8")
                ),
                Question(
                    category = "Culture Générale",
                    text = "Quelle est la langue la plus parlée dans le monde ?",
                    correctAnswer = "Mandarin",
                    wrongAnswers = listOf("Anglais", "Espagnol", "Hindi")
                ),
                Question(
                    category = "Culture Générale",
                    text = "Quel est le plus long fleuve du monde ?",
                    correctAnswer = "Amazon",
                    wrongAnswers = listOf("Nile", "Yangtze", "Mississippi")
                ),
                // Sports
                Question(
                    category = "Sports",
                    text = "Combien de joueurs y a-t-il dans une équipe de football ?",
                    correctAnswer = "11",
                    wrongAnswers = listOf("10", "12", "13")
                ),
                Question(
                    category = "Sports",
                    text = "Qui a remporté le plus de titres du Grand Chelem en tennis ?",
                    correctAnswer = "Roger Federer",
                    wrongAnswers = listOf("Rafael Nadal", "Novak Djokovic", "Pete Sampras")
                ),
                Question(
                    category = "Sports",
                    text = "Dans quel sport peut-on marquer un 'hole in one' ?",
                    correctAnswer = "Golf",
                    wrongAnswers = listOf("Tennis", "Basketball", "Football")
                ),
                Question(
                    category = "Sports",
                    text = "Combien de points vaut un touchdown au football américain ?",
                    correctAnswer = "6",
                    wrongAnswers = listOf("3", "7", "10")
                ),
                Question(
                    category = "Sports",
                    text = "Qui a remporté la Coupe du Monde de la FIFA en 2018 ?",
                    correctAnswer = "France",
                    wrongAnswers = listOf("Croatia", "Germany", "Brazil")
                )
            )
            questionDao.insertAll(questions)
            Log.d("QuizDatabase", "Inserted questions")
        }
    }
}
