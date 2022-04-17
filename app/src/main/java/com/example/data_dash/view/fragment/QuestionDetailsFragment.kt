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
import androidx.navigation.fragment.navArgs
import com.example.data_dash.R
import com.example.data_dash.databinding.FragmentQuestionDetailsBinding
import com.example.data_dash.db.QuestionDAO
import com.example.data_dash.db.QuestionDatabase
import com.example.data_dash.model.ModelQuestion
import com.example.data_dash.model.ModelSlang
import com.example.data_dash.repository.QuestionRepository
import com.example.data_dash.service.TimeService
import com.example.data_dash.utlis.QuestionUtils
import com.example.data_dash.utlis.QuestionUtils.Companion.getJsonDataFromAsset
import com.example.data_dash.view.fragment.dialogue.MessageDialogue
import com.example.data_dash.view.fragment.dialogue.QuitDialogue
import com.example.data_dash.viewModel.QuestionViewModel
import com.example.data_dash.viewModel.QuestionViewModelFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class QuestionDetailsFragment : Fragment() {

    private lateinit var binding: FragmentQuestionDetailsBinding
    private lateinit var question:ModelQuestion
    private lateinit var slangList: MutableList<ModelSlang>
    private var total=0
    private var current=0
    private lateinit var questionDAO: QuestionDAO
    private lateinit var questionRepository: QuestionRepository
    private lateinit var questionViewModel: QuestionViewModel
    private lateinit var intent: Intent
    private val args: QuestionDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //Handle the back pressed
                findNavController().navigate(QuestionDetailsFragmentDirections.actionQuestionDetailsFragmentToQuestionListFragment())

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {  binding= FragmentQuestionDetailsBinding.inflate(layoutInflater)

        initDB()
        initService()
        setQuestion()
        clickEvent()
        getSlangList()

        // all observe
        observeQuestion()

        return binding.root
    }

    private fun initService() {
        intent= Intent(requireContext(), TimeService::class.java)
        requireContext().startService(intent)
    }

    private val mTimeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {

            val value= intent.extras?.getLong(QuestionUtils.COUNT_DOWN)
            if(value!! < 1000) {
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
        val dialogue= QuitDialogue("Time's Up !!","You are unable for this Quiz",object : QuitDialogue.OnClickListener{
            override fun onClick(status: String) {
                if(status=="yes")  findNavController().navigate(QuestionDetailsFragmentDirections.actionQuestionDetailsFragmentToSplashFragment())
            }
        })
        dialogue.show(childFragmentManager,"dialogue")
    }

    override fun onResume() {
        super.onResume()
        Log.d("xxx", "register receiver")
        requireContext().registerReceiver(mTimeReceiver, IntentFilter(QuestionUtils.INTENT_TIMER))
    }

    override fun onPause() {
        super.onPause()
        Log.d("xxx", "unregister receiver")
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

    private fun getSlangList() {
        val jsonFileString = getJsonDataFromAsset(requireContext(), "slang.json")
        val gson = Gson()
        val slangListType = object : TypeToken<MutableList<ModelSlang>>() {}.type
        slangList = gson.fromJson(jsonFileString, slangListType)
       // slangList.forEachIndexed { idx, person -> Log.d("xxx", "> Item $idx:\n$person") }
    }

    private fun observeQuestion() {
        questionViewModel.getQuestionResponse.observe(viewLifecycleOwner){
            setData(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(it: ModelQuestion?) {
        binding.topToolbar.txtName.text="Question #${it?.id}"
        binding.txtQuestion.text= it?.question
        if(it?.isAnswer==true){
            binding.edtQuestion.setText(it.answer.toString())
        }

    }

    private fun initDB() {
        questionDAO= QuestionDatabase.getInstance(requireContext()).questionDao
        questionRepository= QuestionRepository(questionDAO)
        questionViewModel= ViewModelProvider(viewModelStore, QuestionViewModelFactory(questionRepository))[QuestionViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    private fun setQuestion() {
        question= args.question!!
        total= args.total
        Log.d("xxx", "total: $total")
        current= question.id!!
        Log.d("xxx", "current set: $current")
        binding.topToolbar.txtName.text="Question #${question.id}"
        binding.txtQuestion.text= question.question

        if(question.isAnswer==true){
            binding.edtQuestion.setText(question.answer.toString())
        }
    }

    private fun clickEvent() {
        binding.topToolbar.imgBack.setOnClickListener {
            findNavController().navigate(QuestionDetailsFragmentDirections.actionQuestionDetailsFragmentToQuestionListFragment())
        }
        binding.btnBack.setOnClickListener {

            if(current<=1) return@setOnClickListener
            else{
                binding.edtQuestion.setText("")
                current -= 1
                Log.d("xxx", "current: $current")
                questionViewModel.getQuestion(current)
            }

        }
        binding.btnNext.setOnClickListener {

            if(isInputValid()) {
                if(isHaveSlang()){
                    questionViewModel.update(current,binding.edtQuestion.text.toString(),true)
                    binding.edtQuestion.setText("")
                    navigateNext()
                }
            }
            else navigateNext()

        }
        binding.txtTimer.setOnClickListener {
            val dialogue= MessageDialogue("Alas")
            dialogue.show(childFragmentManager,"dialogue")
        }
    }

    private fun isHaveSlang():Boolean{

        val value= binding.edtQuestion.text.toString()

        for(i in 0 until slangList.size){

            if(value.contains(slangList[i].slang,ignoreCase = true)){
                val dialogue= MessageDialogue(slangList[i].slang)
                dialogue.show(childFragmentManager,"dialogue")
                return false
            }
        }

        return true
    }

    private fun navigateNext(){
        if(current==total)  findNavController().navigate(QuestionDetailsFragmentDirections.actionQuestionDetailsFragmentToQuestionListFragment())
        else{
            current += 1
            Log.d("xxx", "current: $current")
            questionViewModel.getQuestion(current)
        }
    }

    private fun isInputValid() : Boolean {
        return when {
            binding.edtQuestion.text.isNullOrBlank() -> {
              /*  binding.edtQuestion.apply {
                    error = "Answer is required"
                    requestFocus()
                }*/
                false
            }
            else -> true
        }
    }


}

