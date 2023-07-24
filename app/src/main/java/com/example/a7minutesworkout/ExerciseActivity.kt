package com.example.a7minutesworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namespace.R
import com.example.namespace.databinding.ActivityExerciseBinding
import com.example.namespace.databinding.DialogCustomBackConfigurationBinding
import java.util.ArrayList
import java.util.Locale


class ExerciseActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    private var restTimer : CountDownTimer? = null
    private var restProgress = 0

    private var restTimerSecond : CountDownTimer? = null
    private var restProgressSecond = 0

    private var binding : ActivityExerciseBinding? = null

    private var exerciseList :  ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1 // Current Position of Exercise.

    private var isDialogShowing = false

    private var pauseOffsetExercise: Long = 0

    private var isExercisePaused = false
    private var pauseStartTime: Long = 0




    // TODO (Step 2 - Variable for Text to Speech which will be initialized later on.)
    // START
    private var tts: TextToSpeech? = null // Variable for Text to Speech
    // END

    private var player: MediaPlayer? = null

    // Declaring an exerciseAdapter object which will be initialized later.
    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbar)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnPrevious?.setOnClickListener{
            if(currentExercisePosition !=0){
                exerciseList!![currentExercisePosition].setIsSelected(false)
                currentExercisePosition--
                exerciseList!![currentExercisePosition].setIsSelected(true)
                pauseOffsetExercise = 0
                exerciseAdapter!!.notifyDataSetChanged()
                setupRestViewSecond()
            }
        }
        binding?.btnNext?.setOnClickListener{
            if (currentExercisePosition < exerciseList?.size!! - 1) {
                exerciseList!![currentExercisePosition].setIsSelected(false)
                exerciseList!![currentExercisePosition].setIsSelected(true)
                pauseOffsetExercise = 0
                exerciseAdapter?.notifyDataSetChanged()
                setupRestView()
            } else {
                Toast.makeText(this, "No more exercises", Toast.LENGTH_SHORT).show()
            }
        }
        binding?.btnPause?.setOnClickListener{
            if (isExercisePaused) {
                // Resume the exercise
                isExercisePaused = false
                binding?.btnPause?.setBackgroundResource(R.drawable.play)
                setRestProgressSecond(pauseOffsetExercise)
            } else {
                // Pause the exercise
                isExercisePaused = true
                binding?.btnPause?.setBackgroundResource(R.drawable.pause)
                restTimer?.cancel()
                restTimerSecond?.cancel()
                //pauseStartTime = System.currentTimeMillis() - pauseOffsetExercise
            }
        }



        // TODO (Step 4 - Initializing the variable of Text to Speech.)
        // START
        tts = TextToSpeech(this, this)
        // END


        exerciseList = Constants.defaultExerciseList()

        setupRestView()
        setupExerciseStatusRecyclerView()


    }
    private fun setupExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter

    }






    private fun setupRestView() {
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        restProgress = 0
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvTimer?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.tvTimer2?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.upcomingLabel?.visibility = View.VISIBLE

        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE

        binding?.Option?.visibility = View.INVISIBLE

        binding?.tvUpcomingExerciseName?.text = exerciseList?.get(currentExercisePosition+1)?.getName()

        // This function is used to set the progress details.
        setRestProgress(pauseOffsetExercise)
    }

    private fun setRestProgress(pauseOffsetExercise : Long){
        binding?.progressBar?.progress = restProgress
        restTimer = object : CountDownTimer(10000 - pauseOffsetExercise,1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10-restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true) // Current Item is selected
                exerciseAdapter!!.notifyDataSetChanged() // Notified the current item to adapter class to reflect it into UI.
                setupRestViewSecond()

            }

        }.start()

    }


    private fun setupRestViewSecond() {

        if (restTimerSecond != null) {
            restTimerSecond?.cancel()
            restProgressSecond = 0
        }
        restProgressSecond = 0
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvTimer?.visibility = View.INVISIBLE
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.tvTimer2?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.upcomingLabel?.visibility = View.INVISIBLE

        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE
        binding?.Option?.visibility = View.VISIBLE


        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()

        // This function is used to set the progress details.
        setRestProgressSecond(pauseOffsetExercise)
    }

    private fun setRestProgressSecond(pauseOffsetExercise : Long){
        binding?.progressBarExercise?.progress = restProgressSecond
        restTimer = object : CountDownTimer(10000 - pauseOffsetExercise,1000){
            override fun onTick(p0: Long) {
                restProgressSecond++
                binding?.progressBarExercise?.progress = 10 - restProgressSecond
                binding?.tvTimer2?.text = (10-restProgressSecond).toString()
            }

            override fun onFinish() {
                exerciseList!![currentExercisePosition].setIsSelected(false) // set IsSelected is false to run setIscompleted
                exerciseList!![currentExercisePosition].setIsCompleted(true)
                exerciseAdapter!!.notifyDataSetChanged() // Notified the current item to adapter class to reflect it into UI.
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    setupRestView()
                }
                else {
                    Toast.makeText(this@ExerciseActivity, "FINISH", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
                }
            }

        }.start()

    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            var result = tts?.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The Language specified is not supported!")
            }
        }
        else{
            Log.e("TTS", "Initialization Failed!")
        }

    }
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onBackPressed() {
        customDialogForBackButton()
        //super.onBackPressed()
    }
    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfigurationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCancelable(false) // Set dialog as non-cancelable


        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.tvYes.setOnClickListener {
            //Todo 6 We need to specify that we are finishing this activity if not the player
            // continues beeping even after the screen is not visibile

            customDialog.dismiss() // Dialog will be dismissed
            this.finish()


        }
        dialogBinding.tvNo.setOnClickListener {
            customDialog.dismiss()
        }

        // Create a Handler to delay the execution of the dialog show() method
        val handler = Handler(mainLooper)
        handler.postDelayed({
            customDialog.show()
        }, 3000) // Delay the dialog show for 100 milliseconds

    }
}


