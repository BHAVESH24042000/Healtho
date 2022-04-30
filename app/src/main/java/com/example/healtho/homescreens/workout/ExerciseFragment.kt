package com.example.healtho.homescreens.workout

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.healtho.R
import com.example.healtho.adapters.ExerciseStatusAdapter
import com.example.healtho.databinding.FragmentExerciseBinding
import com.example.healtho.databinding.FragmentWorkoutBinding
import com.example.healtho.dialogs.noInternetDialog.NoInternetDialogFragment
import com.example.healtho.homescreens.dashboard.sleep.SleepDashboardScreenEvents
import com.example.healtho.homescreens.models.ExerciseModel
import com.example.healtho.homescreens.models.Workout
import com.example.healtho.homescreens.stats.waterstats.WaterStatsViewModel
import com.example.healtho.util.BaseFragment
import com.example.healtho.util.Objects
import com.example.healtho.util.Objects.exercisesInfo
import com.example.healtho.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ExerciseFragment : BaseFragment<FragmentExerciseBinding>(FragmentExerciseBinding::inflate), TextToSpeech.OnInitListener {

    private var restTimer : CountDownTimer? = null
    private var restProgress = 0
    private var restTime : Long = 10
    private var exerciseTimer : CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseTime : Long = 30
    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1
    private var tts : TextToSpeech? = null
    private var player : MediaPlayer? = null
    private var exerciseAdapter : ExerciseStatusAdapter? = null
    private val viewModel by viewModels<WorkoutViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tts = TextToSpeech(requireContext(),this)
        exerciseList = Exercises.defaultExerciseList()
        setupRestView()
        setupExerciseStatusRecyclerView()
        collectUiEvents()
    }
    // rest
    private fun setRestProgressBar(){
        binding.progressBar.progress = restProgress
        restTimer = object : CountDownTimer(restTime*100, 100){ // should be 1000, 1000 for actual 7mins
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding.progressBar.progress = restTime.toInt() - restProgress
                binding.timerTv.text = (restTime - restProgress).toString()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].isSelected = true
                exerciseAdapter!!.notifyDataSetChanged() //update the adapter about change the data
                setupExerciseView()
            }
        }.start()
    }

    private fun setupRestView(){

        try{
            player = MediaPlayer.create(requireContext(), R.raw.press_start)
            player!!.isLooping = false
            player!!.start()
        }catch (e : Exception){
            e.printStackTrace()
        }

        binding.restViewLl.visibility = View.VISIBLE
        binding.exerciseViewLl.visibility = View.GONE

        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }

        binding.nextInfoText.text = exercisesInfo[currentExercisePosition+1]
        binding.nextExerciseTv.text = exerciseList!![currentExercisePosition + 1].name
        setRestProgressBar()
    }

    // exercise
    private fun setExerciseProgressBar(){
        binding.exerciseProgressBar.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseTime*100, 100) { // should be 1000, 1000 for actual 7mins

            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding.exerciseProgressBar.progress = exerciseTime.toInt() - exerciseProgress
                binding.exerciseTimerTv.text = (exerciseTime - exerciseProgress).toString()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish() {
                if(currentExercisePosition < exerciseList?.size!! - 1){
                    exerciseList!![currentExercisePosition].isSelected = (false)
                    exerciseList!![currentExercisePosition].isCompleted = (true)
                    exerciseAdapter!!.notifyDataSetChanged() //update the adapter about change the data
                    setupRestView()
                }else{
                      viewModel.onWorkoutCompleted()
                      onDestroyView()

                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigate(R.id.action_exerciseFragment_to_dashboardFragment)
                    }, 2000)

                }
            }
        }.start()
    }

    private fun collectUiEvents() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.events.collect {
            when (it) {
                is WorkoutDashboardScreenEvents.ShowToast -> requireContext().showToast(it.message)
                WorkoutDashboardScreenEvents.ShowNoInternetDialog -> showNoInternetDialog()
            }
        }
    }

    private fun showNoInternetDialog() {
        NoInternetDialogFragment().show(parentFragmentManager, Objects.OPEN_NO_INTERNET_DIALOG)
    }

    private fun setupExerciseView(){

        binding.exerciseViewLl.visibility = View.VISIBLE
        binding.restViewLl.visibility = View.GONE

        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        setExerciseProgressBar()
        speakOut(exerciseList!![currentExercisePosition].name)

        Glide.with(this).load(exerciseList!![currentExercisePosition].image).into(binding.imageIv);
        binding.exerciseNameTv.text = exerciseList!![currentExercisePosition].name
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){ // check if there is a tts on this mobile
            val result = tts!!.setLanguage(Locale.US)
            // check if the language is installed
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
            }
        }else{ //error or stopped
        }
    }

    private fun speakOut(text: String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun setupExerciseStatusRecyclerView(){
        //set the recycler view in horizontal way
        binding.exerciseStatusRv.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(requireContext())
        exerciseAdapter!!.submitList(exerciseList)
        binding.exerciseStatusRv.adapter = exerciseAdapter
    }

    override fun onDestroyView() {
        // rest view
        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }
        // exercise view
        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        // text to speech
        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }
        // media player
        if(player!=null){
            player!!.stop()
        }

        super.onDestroyView()
    }

}