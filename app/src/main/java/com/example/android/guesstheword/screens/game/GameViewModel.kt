package com.example.android.guesstheword.screens.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

// extends ViewModel
class GameViewModel : ViewModel() {

    // The current word - internal
    // MutableLiveData - LiveData that can be modified and changed (unlike LiveData)
    private val _word = MutableLiveData<String>();

    // external
    val word: LiveData<String>
        get() = _word;

    // The current score - internal
    private val _score = MutableLiveData<Int>();

    // external
    val score: LiveData<Int>
        get() = _score

    // event for game finished
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish;

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>


    init {
        _eventGameFinish.value = false
        resetList()
        nextWord()
        _score.value = 0;
    }

    override fun onCleared() {
        super.onCleared()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            // game has finished
            _eventGameFinish.value = true
        } else {
            _word.value = wordList.removeAt(0)
        }
    }


    /** Methods for buttons presses **/
    fun onSkip() {
        // subtracting one with null safety
        _score.value = (_score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = (_score.value)?.plus(1)
        nextWord()
    }

    // avoids that when we e.g. rotate the phone, the event is triggered again
    // method for completed events
    fun onGameFinishComplete() {
        _eventGameFinish.value = false;
    }
}