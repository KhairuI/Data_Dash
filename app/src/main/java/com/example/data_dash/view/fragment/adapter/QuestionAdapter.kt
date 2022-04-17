package com.example.data_dash.view.fragment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.data_dash.R
import com.example.data_dash.databinding.RowQuestionBinding
import com.example.data_dash.model.ModelQuestion

class QuestionAdapter(
    private val context: Context,
    private val listener:OnQuestionClickListener): RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    private var questionList: MutableList<ModelQuestion> = mutableListOf()

    override fun getItemCount(): Int {
        return if (questionList.isNullOrEmpty()) 0 else questionList.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): QuestionViewHolder {
        return QuestionViewHolder(RowQuestionBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questionList[position]
        holder.binding.apply {
            txtQuestion.text= question.question
            txtNumber.text= "#${position+1}"
            if(question.isAnswer == true) {
                txtNumber.setTextColor(ContextCompat.getColor(context, R.color.color_green))
            }

            rowInfo.setOnClickListener {
                listener.onClick(question)
            }

        }
    }

    class QuestionViewHolder(val binding: RowQuestionBinding) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(questionList: MutableList<ModelQuestion>){
        this.questionList= questionList
        notifyDataSetChanged()
    }

    interface OnQuestionClickListener {
        fun onClick(question: ModelQuestion?)
    }
}