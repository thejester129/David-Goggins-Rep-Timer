package com.example.reptimer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.reptimer.sound.IRepSoundPlayer
import java.util.*
import java.util.concurrent.TimeUnit

class TimerViewModel: ViewModel() {
    //total time
    var totalRepRepeats:Int = 10
        set(value) {
            field = value
            currentTotalSecs.postValue(totalTimeSeconds())
        }
    var totalSecsPerRep:Int = 60
        set(value) {
            field = value
            currentTotalSecs.postValue(totalTimeSeconds())
        }
    private fun totalTimeSeconds():Int = totalRepRepeats * totalSecsPerRep

    //time progress
    private var currentTotalSecs : MutableLiveData<Int> = MutableLiveData(totalTimeSeconds())
    private var currentRepSecs : MutableLiveData<Int> = MutableLiveData(totalSecsPerRep)

    var displayTotalTime : LiveData<String> = Transformations.switchMap(currentTotalSecs){
        seconds -> MutableLiveData(formatTime(seconds))
    }
    var totalProgress : LiveData<Int> = Transformations.switchMap(currentTotalSecs){
        currentTotalSecs -> MutableLiveData( 100 - (currentTotalSecs * 100 / totalTimeSeconds()))
    }
    var repProgress : LiveData<Int> = Transformations.switchMap(currentRepSecs){
        currentRepSecs -> MutableLiveData(100 - (currentRepSecs * 100 / totalSecsPerRep))
    }
    //timer
    private var timer = Timer()
    private var countdownTimerTask = CountdownTimerTask(totalTimeSeconds(), totalSecsPerRep)

    //buttons
    var startButtonVisible:MutableLiveData<Boolean> = MutableLiveData(true)
    var pauseButtonVisible:MutableLiveData<Boolean> = MutableLiveData(false)
    var stopButtonVisible:MutableLiveData<Boolean> = MutableLiveData(false)

    // sound player interface with callback
    lateinit var IRepSoundPlayer: IRepSoundPlayer

    fun startTimer(){
        initTimer()
        startButtonVisible.postValue(false)
        pauseButtonVisible.postValue(true)
        stopButtonVisible.postValue(true)
    }

    fun pauseTimer(){
        timer.cancel()
        startButtonVisible.postValue(true)
        pauseButtonVisible.postValue(false)
        stopButtonVisible.postValue(true)
    }

    fun stopTimer(){
        // TODO remove observer
        timer.cancel()
        currentTotalSecs.postValue(totalTimeSeconds())
        currentRepSecs.postValue(totalSecsPerRep)
        startButtonVisible.postValue(true)
        pauseButtonVisible.postValue(false)
        stopButtonVisible.postValue(false)
    }

    private fun initTimer(){
        timer = Timer()
        countdownTimerTask = CountdownTimerTask(totalTimeSeconds(), totalSecsPerRep)
        timer.scheduleAtFixedRate(countdownTimerTask, 0, 1000)
        countdownTimerTask.remainingTotalSecs.observeForever { secs ->
            currentTotalSecs.postValue(secs)
        }
        countdownTimerTask.remainingRepSecs.observeForever { secs ->
            currentRepSecs.postValue(secs)
            if(secs == totalSecsPerRep){
                IRepSoundPlayer.playRepSound()
            }
        }
    }

    private class CountdownTimerTask(totalSecs: Int, val repSecs:Int) : TimerTask() {
        private var _remainingTotalSecs : Int = totalSecs
        var remainingTotalSecs = MutableLiveData<Int>()

        private var _remainingRepSecs : Int = repSecs
        var remainingRepSecs = MutableLiveData<Int>()

        override fun run() {
            remainingTotalSecs.postValue(_remainingTotalSecs)
            _remainingTotalSecs--

            remainingRepSecs.postValue(_remainingRepSecs)
            _remainingRepSecs--
            if(_remainingRepSecs < 0)
                _remainingRepSecs = repSecs
        }
    }

    private fun formatTime(totalSecs: Int): String {
        val minutes = TimeUnit.SECONDS.toMinutes(totalSecs.toLong())
        val minsAsSecs = TimeUnit.MINUTES.toSeconds(minutes)
        val leftoverSecs = totalSecs - minsAsSecs

        return  "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (leftoverSecs < 10) "0" else ""}$leftoverSecs"
    }

}