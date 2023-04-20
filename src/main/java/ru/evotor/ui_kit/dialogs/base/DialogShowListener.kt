package ru.evotor.ui_kit.dialogs.base

import androidx.fragment.app.DialogFragment

interface DialogShowListener {

    fun onShow(
        dialogFragment: DialogFragment,
        where: String,
        title: String,
        titleArgs: Array<String>,
        message: String,
        messageArgs: Array<String>
    )
}