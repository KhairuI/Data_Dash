package com.example.data_dash.service

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import com.example.data_dash.utlis.QuestionUtils

class TimeService: Service() {

    private lateinit var countDownTimer:CountDownTimer
    private val intent= Intent(QuestionUtils.INTENT_TIMER)

    override fun onCreate() {
        super.onCreate()

        Log.d("xxx", "start timer")

        countDownTimer= object :CountDownTimer(600000,1000){
            override fun onTick(p0: Long) {
              //  Log.d("xxx", "time remaining: $p0")
                intent.putExtra(QuestionUtils.COUNT_DOWN,p0)
                sendBroadcast(intent)
            }

            override fun onFinish() {
                Log.d("xxx", "time finish")
            }

        }
        countDownTimer.start()
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
       return null
    }
}