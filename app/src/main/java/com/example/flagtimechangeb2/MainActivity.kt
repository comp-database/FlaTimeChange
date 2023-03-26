package com.example.flagtimechangeb2
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.flagtimechangeb2.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var dataHelper: DataHelper
    // 0 -> donated
    // 1 -> ready to donate
    //-1 -> not ready to donate
    var currStatus : Int = 0
    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataHelper = DataHelper(applicationContext)
        binding.startButton.setOnClickListener { startStopAction() }
        binding.resetButton.setOnClickListener{ resetAction() }
        if (dataHelper.timerCounting()) {
            startTimer()
        } else {
            stopTimer()
            if (dataHelper.startTime() != null && dataHelper.stopTime() != null) {
                val time = Date().time - calcRestartTime().time
                binding.timeTV.text = timeStringFromLong(time)
            }
        }
        timer.scheduleAtFixedRate(TimeTask(), 0, 500)



        binding.imgchg.setOnClickListener {
            binding.flagImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.green))
        }



//        val timelist = binding.timeTV.text.toList()
//        val one = timelist[6].digitToInt()
//        val two = timelist[7].digitToInt()
//        if (binding.timeTV.text == "00:00:00"){
//            binding.flagImg.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.red))
//        }else if (binding.timeTV.text == "00:00:10"){
//            startStopAction()
//            binding.flagImg.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.red))
//            alert()
//        }else{
////                        startStopAction()
//            binding.flagImg.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.yellow))
//        }



    }

    private inner class TimeTask: TimerTask()
    {
        override fun run()
        {
            if(dataHelper.timerCounting() )
            {
                val time = Date().time - dataHelper.startTime()!!.time
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    binding.timeTV.text = timeStringFromLong(time)

                    //code for flag change after 10 sec
                    val timelist = binding.timeTV.text.toList()
                    val one = timelist[6].digitToInt()
                    val two = timelist[7].digitToInt()
                    if (one < 1 &&  two >= 0){
                        binding.flagImg.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.red))
                    }else if (one == 1 &&  two > 0){
                        startStopAction()
                        binding.flagImg.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.yellow))
                        alert()
                    }else{
//                        startStopAction()
                        binding.flagImg.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.green))
                    }
                })
                //

            }
        }
    }



    private fun alert(){
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Do you Want to donate blood")
        //set message for alert dialog
        builder.setMessage("You're complete with 3 months duration")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            binding.flagImg.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.green))
            currStatus = 1
        }
        //performing cancel action
        builder.setNeutralButton("Not currently"){dialogInterface , which ->
            binding.flagImg.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.yellow))
            currStatus = -1         }
        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
            resetAction()
            binding.flagImg.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.red))
            currStatus = 0
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun resetAction()
    {
        dataHelper.setStopTime(null)
        dataHelper.setStartTime(null)
        stopTimer()
        binding.timeTV.text = timeStringFromLong(0)
    }

    private fun stopTimer()
    {
        dataHelper.setTimerCounting(false)
        binding.startButton.text = getString(R.string.start)
    }
    private fun startTimer()
    {
        dataHelper.setTimerCounting(true)
        binding.startButton.text = getString(R.string.stop)
    }
    private fun startStopAction()
    {
        if(dataHelper.timerCounting())
        {
            dataHelper.setStopTime(Date())
            stopTimer()
        }
        else
        {
            if(dataHelper.stopTime() != null)
            {
                dataHelper.setStartTime(calcRestartTime())
                dataHelper.setStopTime(null)
            }
            else
            {
                dataHelper.setStartTime(Date())
            }
            startTimer()
        }
    }
    private fun calcRestartTime(): Date
    {
        val diff = dataHelper.startTime()!!.time - dataHelper.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff)
    }

    private fun timeStringFromLong(ms: Long): String
    {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60) % 60)
        val hours = (ms / (1000 * 60 * 60) % 24)
        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String
    {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


}
