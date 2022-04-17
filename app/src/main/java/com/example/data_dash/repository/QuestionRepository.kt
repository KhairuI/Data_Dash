package com.example.data_dash.repository

import com.example.data_dash.db.QuestionDAO
import com.example.data_dash.model.ModelQuestion

class QuestionRepository(private val dao: QuestionDAO) {

    // insert question
    suspend fun insert(question:ModelQuestion) = dao.insert(question)

    // get all question
    fun getAllQuestion()= dao.getAllQuestion()

    // get single question
    fun getQuestion(id:Int)= dao.getQuestion(id)

    // update question
    fun update(id:Int,answer:String?, isAnswer:Boolean)= dao.update(id,answer, isAnswer)


}