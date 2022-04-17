package com.example.data_dash.view.fragment.dialogue

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.data_dash.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MessageDialogue(private val slang:String): DialogFragment() {

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder= MaterialAlertDialogBuilder(requireContext(),R.style.MyTheme)
        builder.apply {
            setTitle(R.string.warning)
            val message="${getString(R.string.you_use)} \"$slang\" ${getString(R.string.txt_alert)}"
            setMessage(message)
            background= resources.getDrawable(R.drawable.bg_layout_all_curve,null)
            builder.setPositiveButton("OK"){ _: DialogInterface, _: Int ->

            }
            builder.setNeutralButton("Don't Show"){ _: DialogInterface, _: Int ->

            }
        }

        return builder.create()
    }
}