package ru.evotor.ui_kit.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import gone
import ru.evotor.ui_kit.databinding.BottomSheetProgressLayoutBinding
import ru.evotor.ui_kit.dialogs.base.StackTraceUtils
import visible

class ProgressBottomSheetDialogFragment : BaseBottomSheetDialogFragment<BottomSheetProgressLayoutBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomSheetProgressLayoutBinding
        get() = BottomSheetProgressLayoutBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getMessage()?.let {
            binding.dialogMessage.text = it
            binding.dialogMessage.visible()
        } ?: binding.dialogMessage.gone()
    }

    fun setMessage(message: String): ProgressBottomSheetDialogFragment {
        arguments?.putString(MESSAGE_KEY, message)
        return this
    }

    fun setMessage(
        @StringRes messageRes: Int,
        vararg messageArgs: Any
    ): ProgressBottomSheetDialogFragment {
        arguments?.putInt(MESSAGE_RES_KEY, messageRes)
        arguments?.putStringArray(
            MESSAGE_ARGS_KEY,
            messageArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }
    fun show(fragmentManager: FragmentManager): ProgressBottomSheetDialogFragment {
        try {
            fragmentManager.beginTransaction().add(this, TAG).commitNowAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            fragmentManager.beginTransaction().add(this, TAG).commitAllowingStateLoss()
        }
        saveStacktrace()
        return this
    }

    fun isShowing(): Boolean = dialog?.isShowing ?: false

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

        private const val TAG = "ru.evotor.ui_kit.dialogs.ProgressBottomSheetDialogFragment.tag"
    }
}