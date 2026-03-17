package com.belyaev.selivanov.unscramblegame

import com.belyaev.selivanov.unscramblegame.data.MAX_NO_OF_WORDS
import com.belyaev.selivanov.unscramblegame.data.SCORE_INCREASE
import com.belyaev.selivanov.unscramblegame.ui_model.GameViewModel
import org.junit.Assert.*
import org.junit.Test


class GameViewModelTest {
    private val viewModel = GameViewModel()


    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        val currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambleWord(currentGameUiState.currentScrambledWord)
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()
        val updatedGameUiState = viewModel.uiState.value
        assertEquals(SCORE_INCREASE, updatedGameUiState.score)
        assertFalse(updatedGameUiState.isGuessedWordWrong)
    }

    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        val incorrectPlayerWord = "incorrect"
        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()
        val currentGameUiState = viewModel.uiState.value
        assertEquals(0, currentGameUiState.score)
        assertTrue(currentGameUiState.isGuessedWordWrong)
    }
    @Test
    fun gameViewModel_Initialization_FirstWordLoaded(){
        val gameUiState = viewModel.uiState.value
        val unscrambledWord = getUnscrambleWord(gameUiState.currentScrambledWord)
        assertNotEquals(unscrambledWord, gameUiState.currentScrambledWord)
        assertTrue(gameUiState.currentWordCount == 1)
        assertTrue(gameUiState.score == 0)
        assertFalse(gameUiState.isGameOver)
    }
    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly(){
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambleWord(currentGameUiState.currentScrambledWord)
        repeat(MAX_NO_OF_WORDS){
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambleWord(currentGameUiState
                .currentScrambledWord)
            assertEquals(expectedScore, currentGameUiState.score)
        }
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        assertTrue(currentGameUiState.isGameOver)

    }

    private fun getUnscrambleWord(scrambledWord: String): String {
        return com.belyaev.selivanov.unscramblegame.data.allWords.firstOrNull{ word ->
            scrambledWord.toSet() == word.toSet()
        }?: ""
    }
}
