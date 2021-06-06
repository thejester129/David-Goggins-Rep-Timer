package com.example.reptimer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.reptimer.databinding.HomeFragmentBinding
import com.example.reptimer.sound.RepMediaPlayer

class HomeFragment : Fragment() {
    private lateinit var binding : HomeFragmentBinding
    private val viewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mediaPlayer = RepMediaPlayer(requireContext())
        viewModel.IRepSoundPlayer = mediaPlayer
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRepRepeatsPicker()
        setupRepSecsPicker()
    }

    private fun setupRepRepeatsPicker(){
        val repValues = IntRange(1, 30)
        val repDisplayValues = repValues.map { i -> i.toString() }.toTypedArray()
        binding.repRepeatsPicker.maxValue = repDisplayValues.size - 1
        binding.repRepeatsPicker.displayedValues = repDisplayValues
        binding.repRepeatsPicker.value = repValues.indexOf(10)
        binding.repRepeatsPicker.setOnValueChangedListener{_,_,newVal ->
            viewModel.totalRepRepeats = repValues.elementAt(newVal)
        }
    }

    private fun setupRepSecsPicker(){
        binding.repSecsPicker.maxValue = 5
        val repTimesDisplayValues = arrayOf("10","15","20","30","45","60")
        binding.repSecsPicker.maxValue = repTimesDisplayValues.size - 1
        binding.repSecsPicker.displayedValues = repTimesDisplayValues
        binding.repSecsPicker.value = repTimesDisplayValues.lastIndex
        binding.repSecsPicker.setOnValueChangedListener{_,_,newVal ->
            viewModel.totalSecsPerRep = repTimesDisplayValues[newVal].toInt()
        }
    }

}