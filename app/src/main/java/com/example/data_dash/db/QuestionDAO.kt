package com.example.data_dash.db

import androidx.room.*
import com.example.data_dash.model.ModelQuestion

@Dao
interface QuestionDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question:ModelQuestion):Long

    @Transaction
    @Query("SELECT * FROM question_table")
    fun getAllQuestion():MutableList<ModelQuestion>

    @Transaction
    @Query("SELECT * FROM question_table WHERE id = :id LIMIT 1")
    fun getQuestion(id:Int):ModelQuestion

    @Transaction
    @Query("UPDATE question_table SET answer = :answer, isAnswer = :isAnswer WHERE id = :id")
    fun update(id:Int,answer:String?, isAnswer:Boolean):Int

}