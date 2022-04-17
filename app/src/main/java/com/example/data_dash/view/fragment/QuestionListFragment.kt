package com.example.data_dash.view.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.data_dash.R
import com.example.data_dash.databinding.FragmentQuestionListBinding
import com.example.data_dash.db.QuestionDAO
import com.example.data_dash.db.QuestionDatabase
import com.example.data_dash.model.ModelQuestion
import com.example.data_dash.repository.QuestionRepository
import com.example.data_dash.service.TimeService
import com.example.data_dash.utlis.QuestionUtils
import com.example.data_dash.view.fragment.adapter.QuestionAdapter
import com.example.data_dash.view.fragment.dialogue.DefaultDialogue
import com.example.data_dash.view.fragment.dialogue.QuitDialogue
import com.example.data_dash.viewModel.QuestionViewModel
import com.example.data_dash.viewModel.QuestionViewModelFactory
import com.google.android.material.snackbar.Snackbar


class QuestionListFragment : Fragment() {

    private lateinit var binding: FragmentQuestionListBinding
    private lateinit var questionDAO: QuestionDAO
    private lateinit var questionRepository: QuestionRepository
    private lateinit var questionViewModel: QuestionViewModel
    private lateinit var questionList:MutableList<ModelQuestion>
    private var total=0
    private lateinit var intent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //Handle the back pressed
                showAlertDialogue(getString(R.string.alert),getString(R.string.quit))

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { binding= FragmentQuestionListBinding.inflate(layoutInflater)

        initDB()
        initService()
        clickEvent()

        // all observe
        observeQuestionList()

        questionViewModel.getAllQuestion()

        return binding.root
    }

    private fun initService() {
        intent= Intent(requireContext(),TimeService::class.java)
        requireContext().startService(intent)
    }

    private val mTimeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {

            val value= intent.extras?.getLong(QuestionUtils.COUNT_DOWN)
            if(value!! < 1000){
                binding.txtTimer.text="Time left: 00:00:00"
                showQuitDialogue()
            }
            else{

                var seconds = (value / 1000).toInt()

                val hours = seconds / (60 * 60)
                val tempMint = seconds - hours * 60 * 60
                val minutes = tempMint / 60
                seconds = tempMint - minutes * 60

                binding.txtTimer.text="Time left: ${String.format("%02d", hours)}:${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
            }


        }
    }

    private fun showQuitDialogue() {
        val dialogue= QuitDialogue("Time's Up !!","You are unable for this Quiz",object :QuitDialogue.OnClickListener{
            override fun onClick(status: String) {
                if(status=="yes"){
                    for(i in 1..questionList.size) questionViewModel.update(i, null,false)
                    findNavController().navigate(QuestionListFragmentDirections.actionQuestionListFragmentToSplashFragment())
                }
            }
        })
        dialogue.show(childFragmentManager,"dialogue")
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(mTimeReceiver, IntentFilter(QuestionUtils.INTENT_TIMER))
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(mTimeReceiver)
    }

    override fun onDestroy() {
        requireContext().stopService(Intent(requireContext(),TimeService::class.java))
        super.onDestroy()
    }

    override fun onStop() {
        try {
            requireContext().unregisterReceiver(mTimeReceiver)
        } catch (e:Exception){

        }
        super.onStop()
    }

    private fun observeQuestionList() {
        questionViewModel.getAllQuestionResponse.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                total= it.size
                questionList= it
                setData(questionList)
            }
        }
    }

    private fun setData(questionList: MutableList<ModelQuestion>) {

        val questionAdapter= QuestionAdapter(requireContext(),object :QuestionAdapter.OnQuestionClickListener{
            override fun onClick(question: ModelQuestion?) {
                val action = QuestionListFragmentDirections.actionQuestionListFragmentToQuestionDetailsFragment(question,total)
                findNavController().navigate(action)
            }

        })

        binding.rvQuestion.apply {
            setHasFixedSize(true)
            adapter= questionAdapter
        }
        questionAdapter.updateList(questionList)

    }

    private fun initDB() {
        questionDAO= QuestionDatabase.getInstance(requireContext()).questionDao
        questionRepository= QuestionRepository(questionDAO)
        questionViewModel= ViewModelProvider(viewModelStore, QuestionViewModelFactory(questionRepository))[QuestionViewModel::class.java]

        // set toolbar  title
        binding.topToolbar.txtName.text="Questions"

    }

    private fun clickEvent() {
        binding.btnSubmit.setOnClickListener {
            if(checkAnswer()){
                for(i in 1..questionList.size) questionViewModel.update(i, null,false)
                findNavController().navigate(QuestionListFragmentDirections.actionQuestionListFragmentToSplashFragment())
                Snackbar.make(requireView(),"Submitted Successfully",Snackbar.LENGTH_SHORT).show()
            }

        }

        binding.topToolbar.imgBack.setOnClickListener {
            showAlertDialogue(getString(R.string.alert),getString(R.string.quit))

        }
    }

    private fun showAlertDialogue(title:String,message:String) {
        val dialogue= DefaultDialogue(title,message,object :DefaultDialogue.OnClickListener{
            override fun onClick(status: String) {
                if(status=="yes")  findNavController().navigate(QuestionListFragmentDirections.actionQuestionListFragmentToSplashFragment())
            }
        })
        dialogue.show(childFragmentManager,"dialogue")
    }

    private fun checkAnswer():Boolean{
        for(i in 0 until questionList.size){
            if(questionList[i].isAnswer==false){
                Snackbar.make(requireView(),"Question #${i+1} answer is required",Snackbar.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }


}