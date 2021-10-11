package ru.evotor.ui_kit.dialogs

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gone
import ru.evotor.ui_kit.R
import visible

class ProgressBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as BottomSheetDialog?)?.behavior?.peekHeight = activity?.resources?.displayMetrics?.heightPixels
                ?: 0
        view.findViewById<TextView>(R.id.dialog_message).let { messageTextView ->
            arguments?.getMessage()?.let {
                messageTextView.text = it
                messageTextView.visible()
            } ?: messageTextView.gone()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bottom_sheet_progress_layout, container)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.dialog_transparent_background)))
        }
    }

    override fun getTheme(): Int = R.style.EvotorTheme_BottomSheetDialogTheme

    fun setMessage(message: String): ProgressBottomSheetDialogFragment {
        arguments?.putString(MESSAGE_KEY, message)
        return this
    }

    fun setMessage(@StringRes messageRes: Int): ProgressBottomSheetDialogFragment {
        arguments?.putInt(MESSAGE_RES_KEY, messageRes)
        return this
    }

    fun show(fragmentManager: FragmentManager): ProgressBottomSheetDialogFragment {
        try {
            fragmentManager.beginTransaction().add(this, TAG).commitNowAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            fragmentManager.beginTransaction().add(this, TAG).commitAllowingStateLoss()
        }
        return this
    }

    fun isShowing(): Boolean = dialog?.isShowing ?: false

    private fun Bundle.getMessage(): String? {
        val messageRes = getInt(MESSAGE_RES_KEY, 0)
        return if (messageRes == 0) {
            getString(MESSAGE_KEY, null)
        } else {
            context?.getString(messageRes)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = ProgressBottomSheetDialogFragment().apply {
            arguments = Bundle()
            isCancelable = false
        }

        @JvmStatic
        fun hide(fragmentManager: FragmentManager) {
            fragmentManager.findFragmentByTag(TAG)?.let {
                try {
                    fragmentManager.beginTransaction().remove(it)
                            .commitNowAllowingStateLoss()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                    fragmentManager.beginTransaction().remove(it)
                            .commitAllowingStateLoss()
                }
            }
        }

        private const val TAG = "ru.evotor.ui_kit.dialogs.progress_dialog_fragment.tag"
        private const val MESSAGE_KEY = "ru.evotor.ui_kit.dialogs.progress_dialog_fragment.message_key"
        private const val MESSAGE_RES_KEY = "ru.evotor.ui_kit.dialogs.progress_dialog_fragment.message_res_key"
    }
}