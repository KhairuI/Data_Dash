package com.example.data_dash.view.fragment.dialogue

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.data_dash.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class QuitDialogue(private val title:String,
                   private val message:String,
                   private val listener: OnClickListener): DialogFragment() {

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder= MaterialAlertDialogBuilder(requireContext(),R.style.MyTheme)
        builder.apply {
            setTitle(title)
            setMessage(message)
            isCancelable= false
            background= resources.getDrawable(R.drawable.bg_layout_all_curve,null)
            builder.setPositiveButton("Quit"){ _: DialogInterface, _: Int ->

                listener.onClick("yes")
            }
        }

        return builder.create()
    }

    interface OnClickListener {
        fun onClick(status: String)
    }
}