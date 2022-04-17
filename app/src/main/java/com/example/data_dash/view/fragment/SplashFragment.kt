package com.example.data_dash.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.data_dash.databinding.FragmentSplashBinding
import com.example.data_dash.db.QuestionDAO
import com.example.data_dash.db.QuestionDatabase
import com.example.data_dash.repository.QuestionRepository
import com.example.data_dash.utlis.QuestionUtils
import com.example.data_dash.viewModel.QuestionViewModel
import com.example.data_dash.viewModel.QuestionViewModelFactory

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private lateinit var questionDAO: QuestionDAO
    private lateinit var questionRepository: QuestionRepository
    private lateinit var questionViewModel: QuestionViewModel
    private var isEmpty= true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { binding= FragmentSplashBinding.inflate(layoutInflater)

        initDB()
        clickEvent()

        // all observe
        observeQuestionList()

        questionViewModel.getAllQuestion()

        return binding.root
    }

    private fun observeQuestionList() {
        questionViewModel.getAllQuestionResponse.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()) {
                Log.d("xxx", "observeQuestionList: list is not empty")
                isEmpty= false
            }
        }
    }

    private fun initDB() {
        questionDAO= QuestionDatabase.getInstance(requireContext()).questionDao
        questionRepository= QuestionRepository(questionDAO)
        questionViewModel= ViewModelProvider(viewModelStore,QuestionViewModelFactory(questionRepository))[QuestionViewModel::class.java]
    }

    private fun clickEvent() {
        binding.btnStart.setOnClickListener {
            if(isEmpty){
                Log.d("xxx", "insert question")
                QuestionUtils.setQuestion().let {
                    it.forEach {
                        questionViewModel.insert(it)
                    }
                }
            }
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToQuestionListFragment())
        }
    }

}