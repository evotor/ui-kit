package ru.evotor.ui_kit.dialogs

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    fun show(fragmentManager: FragmentManager) {
        try {
            fragmentManager.beginTransaction().add(this, TAG).commitNowAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            fragmentManager.beginTransaction().add(this, TAG).commitAllowingStateLoss()
        }
    }

    private fun Bundle.getMessage(): String? = getString(MESSAGE_KEY, null)

    companion object {

        fun newInstance() = ProgressBottomSheetDialogFragment().apply {
            arguments = Bundle()
            //isCancelable = false
        }

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
    }
}