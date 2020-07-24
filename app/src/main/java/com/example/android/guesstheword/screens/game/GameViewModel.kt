package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

// extends ViewModel
class GameViewModel : ViewModel() {

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

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

    // countdown timer
    private val _timer: CountDownTimer;

    private val _currentTime = MutableLiveData<Long>();

    private val currentTime: LiveData<Long>
        get() = _currentTime;

    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time);
    }

    private val _eventBuzz = MutableLiveData<BuzzType>();
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz

    init {
        _eventGameFinish.value = false
        resetList()
        nextWord()
        _score.value = 0;
        _eventBuzz.value = BuzzType.NO_BUZZ

        // creates a timer which triggers the end of the game when it finishes
        // it will tick every ONE_SECOND until the COUNTDOWN_TIME passes
        _timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(milisUntilFinished: Long) {
                _currentTime.value = milisUntilFinished / ONE_SECOND
                if (milisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                _eventGameFinish.value = true;
                _currentTime.value = DONE
                _eventBuzz.value = BuzzType.GAME_OVER
            }
        }
        _timer.start();
    }

    companion object {
        // these represent different important times in the game, such as game length

        // this is when the game is over
        private const val DONE = 0L;

        // this is the number of miliseconds in a second
        private const val ONE_SECOND = 1000L;

        // this is the total time of the game
        private const val COUNTDOWN_TIME = 60000L;

        // This is the time when the phone will start buzzing each second
        private const val COUNTDOWN_PANIC_SECONDS = 10L
    }

    override fun onCleared() {
        super.onCleared()
        _timer.cancel();
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
            resetList()
        }
        _word.value = wordList.removeAt(0)
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
        _eventBuzz.value = BuzzType.CORRECT
    }

    // avoids that when we e.g. rotate the phone, the event is triggered again
    // method for completed events
    fun onGameFinishComplete() {
        _eventGameFinish.value = false;
    }

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }
}