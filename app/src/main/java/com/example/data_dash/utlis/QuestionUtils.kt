package com.example.data_dash.utlis

import android.content.Context
import com.example.data_dash.model.ModelQuestion
import java.io.IOException

class QuestionUtils {

    companion object{

        const val INTENT_TIMER= "intent_timer"
        const val COUNT_DOWN= "count_down"

        fun setQuestion():MutableList<ModelQuestion>{
            return mutableListOf(
                ModelQuestion("What is your name?",null, false),
                ModelQuestion("What is your carrier goal?",null, false),
                ModelQuestion("What do you know about DataDash App?",null, false)
            )
        }

        fun getJsonDataFromAsset(context: Context, fileName: String): String? {
            val jsonString: String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }
    }
}