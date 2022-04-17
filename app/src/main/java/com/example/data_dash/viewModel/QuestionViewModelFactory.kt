package com.example.data_dash.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data_dash.repository.QuestionRepository

class QuestionViewModelFactory(private val repository: QuestionRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(QuestionViewModel::class.java)){
            return QuestionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}