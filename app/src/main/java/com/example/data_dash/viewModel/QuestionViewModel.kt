package com.example.data_dash.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data_dash.model.ModelQuestion
import com.example.data_dash.repository.QuestionRepository
import kotlinx.coroutines.launch

class QuestionViewModel(private val repository: QuestionRepository) : ViewModel() {

    // insert question
    private val _insertQuestionResponse : MutableLiveData<Long> = MutableLiveData()
    val insertQuestionResponse : LiveData<Long> get() = _insertQuestionResponse

    fun insert(question: ModelQuestion) = viewModelScope.launch {
        _insertQuestionResponse.value = repository.insert(question)
    }

    // get all question

    private val _getAllQuestionResponse : MutableLiveData<MutableList<ModelQuestion>> = MutableLiveData()
    val getAllQuestionResponse: LiveData<MutableList<ModelQuestion>> get() = _getAllQuestionResponse

    fun getAllQuestion() = viewModelScope.launch {
        _getAllQuestionResponse.value = repository.getAllQuestion()
    }

    // get single question

    private val _getQuestionResponse : MutableLiveData<ModelQuestion> = MutableLiveData()
    val getQuestionResponse: LiveData<ModelQuestion> get() = _getQuestionResponse

    fun getQuestion(id:Int) = viewModelScope.launch {
        _getQuestionResponse.value = repository.getQuestion(id)
    }

    // update question

    private val _updateQuestionResponse : MutableLiveData<Int> = MutableLiveData()
    val updateQuestionResponse: LiveData<Int> get() = _updateQuestionResponse

    fun update(id:Int,answer:String?, isAnswer:Boolean) = viewModelScope.launch {
        _updateQuestionResponse.value = repository.update(id, answer, isAnswer)
    }


}