package ru.evotor.ui_kit.dialogs

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.evotor.ui_kit.R

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.dialog_transparent_background)))
        }
    }

    override fun getTheme(): Int = R.style.EvotorUITheme_BottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as BottomSheetDialog?)?.behavior?.peekHeight = activity?.resources?.displayMetrics?.heightPixels
                ?: 0
        view.setBackgroundResource(R.drawable.dialog_bottom_background_normal)
    }

}