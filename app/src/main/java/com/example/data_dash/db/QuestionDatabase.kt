package com.example.data_dash.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data_dash.model.ModelQuestion

@Database(entities = [ModelQuestion::class],version = 1)
abstract class QuestionDatabase:RoomDatabase() {

    abstract val questionDao: QuestionDAO

    companion object{

        @Volatile
        private var instance: QuestionDatabase?= null

        fun getInstance(context: Context): QuestionDatabase {
            synchronized(this){
                return  instance ?: Room.databaseBuilder(context.applicationContext,
                    QuestionDatabase::class.java,"questionDB").fallbackToDestructiveMigration().allowMainThreadQueries().build()
                    .also {
                        instance = it
                    }

            }
        }

    }

}