package com.example.reptimer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.reptimer.databinding.FragmentFirstBinding
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val timer = Timer()
    private val countdownTimerTask = CountdownTimerTask()
    private var mediaPlayer : MediaPlayer? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.stay_hard_1)
        binding.startButton.setOnClickListener { startTimer() }
        binding.stopButton.setOnClickListener { stopTimer() }
    }

    private fun startTimer(){
        setupTimer()
        binding.startButton.visibility = View.GONE
        binding.stopButton.visibility = View.VISIBLE
    }

    private fun stopTimer(){
        timer.cancel()
        binding.startButton.visibility = View.VISIBLE
        binding.stopButton.visibility = View.GONE
    }

    private fun setupTimer(){
        timer.scheduleAtFixedRate(countdownTimerTask, 0, 1000)
        countdownTimerTask.seconds.observe(viewLifecycleOwner,  { seconds ->
            binding.timeTextview.text = getFormattedTime(seconds.toLong())
            if(seconds.rem(60) == 0){
                makeSound()
            }
        })
    }

    private fun makeSound(){
        mediaPlayer?.start()
    }

    private class CountdownTimerTask : TimerTask() {
        private var internalSeconds = 10 * 60
        var seconds = MutableLiveData<Int>()
        override fun run() {
            internalSeconds--
            seconds.postValue(internalSeconds)
        }
    }

    private fun getFormattedTime(totalSecs: Long): String {
        val minutes = TimeUnit.SECONDS.toMinutes(totalSecs)
        val minsAsSecs = TimeUnit.MINUTES.toSeconds(minutes)
        val leftoverSecs = totalSecs - minsAsSecs

        return  "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (leftoverSecs < 10) "0" else ""}$leftoverSecs"
    }


}