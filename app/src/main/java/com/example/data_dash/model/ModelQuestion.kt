package com.example.data_dash.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "question_table")
data class ModelQuestion(

    @ColumnInfo(name="question")
    val question:String?,

    @ColumnInfo(name="answer")
    val answer:String?,

    @ColumnInfo(name="isAnswer")
    val isAnswer:Boolean?
    ):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id:Int?= null

}