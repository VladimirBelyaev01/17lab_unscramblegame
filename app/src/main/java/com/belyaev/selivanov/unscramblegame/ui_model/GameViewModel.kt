package com.belyaev.selivanov.unscramblegame.ui_model

import androidx.lifecycle.ViewModel
import com.belyaev.selivanov.unscramblegame.data.GameUiState
import com.belyaev.selivanov.unscramblegame.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.belyaev.selivanov.unscramblegame.data.MAX_NO_OF_WORDS
import com.belyaev.selivanov.unscramblegame.data.SCORE_INCREASE
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    var userGuess by mutableStateOf("")
        private set
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private val usedWords: MutableSet<String> = mutableSetOf()

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        while (usedWords.contains(currentWord)) {
            currentWord = allWords.random()
        }
        usedWords.add(currentWord)
        return shuffleCurrentWord(currentWord)
    }

    fun updateUserGuess(guessWord: String) {
        userGuess = guessWord
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score + SCORE_INCREASE
            updateGameState(updatedScore)
        } else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        updateUserGuess("")
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount + 1
                )
            }
        }
    }

    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(
            currentScrambledWord = pickRandomWordAndShuffle()
        )
    }
}