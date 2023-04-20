package ru.evotor.ui_kit.dialogs

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.evotor.ui_kit.R
import ru.evotor.ui_kit.dialogs.base.DialogShowListener

abstract class BaseBottomSheetDialogFragment<T : ViewBinding> : BottomSheetDialogFragment() {

    protected lateinit var binding: T

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.dialog_transparent_background)))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_layout, container)
                .apply {
                    binding = bindingInflater.invoke(inflater, findViewById(R.id.dialog_content_container), true)
                }
    }

    override fun getTheme(): Int = R.style.EvotorUITheme_BottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as BottomSheetDialog?)?.behavior?.peekHeight = activity?.resources?.displayMetrics?.heightPixels
                ?: 0
        binding.root.setBackgroundResource(R.drawable.dialog_bottom_background_normal)
    }

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T

    companion object {

        var dialogShowListener: DialogShowListener? = null

    }
}