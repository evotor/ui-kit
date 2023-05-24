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
import ru.evotor.ui_kit.dialogs.base.StackTraceUtils

abstract class BaseBottomSheetDialogFragment<T : ViewBinding> : BottomSheetDialogFragment() {

    protected lateinit var binding: T

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogShowListener?.onShow(
            dialogFragment = this,
            where = StackTraceUtils.getStackTrace(),
            title = arguments?.getTitle() ?: "[NO_TITLE]",
            titleArgs = arguments?.getTitleArgs() ?: emptyArray(),
            message = arguments?.getMessage() ?: "[NO_MESSAGE]",
            messageArgs = arguments?.getMessageArgs() ?: emptyArray()
        )
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.dialog_transparent_background)))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_layout, container)
            .apply {
                binding = bindingInflater.invoke(
                    inflater,
                    findViewById(R.id.dialog_content_container),
                    true
                )
            }
    }

    override fun getTheme(): Int = R.style.EvotorUITheme_BottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as BottomSheetDialog?)?.behavior?.peekHeight =
            activity?.resources?.displayMetrics?.heightPixels
                ?: 0
        binding.root.setBackgroundResource(R.drawable.dialog_bottom_background_normal)
    }

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T

    protected fun saveStacktrace() {
        arguments?.putString(STACKTRACE_KEY, StackTraceUtils.getStackTrace())
    }

    protected fun Bundle.getTitle(): String? {
        val titleRes = getInt(TITLE_RES_KEY, 0)
        return if (titleRes == 0) {
            getString(TITLE_KEY, null)
        } else {
            context?.getString(titleRes)
        }
    }

    protected fun Bundle.getTitleArgs(): Array<String>? {
        if (!containsKey(TITLE_ARGS_KEY)) return null
        return getStringArray(TITLE_ARGS_KEY)
    }

    protected fun Bundle.getMessage(): String? {
        val messageRes = getInt(MESSAGE_RES_KEY, 0)
        return if (messageRes == 0) {
            getString(MESSAGE_KEY, null)
        } else {
            context?.getString(messageRes)
        }
    }

    protected fun Bundle.getMessageArgs(): Array<String>? {
        if (!containsKey(MESSAGE_ARGS_KEY)) return null
        return getStringArray(MESSAGE_ARGS_KEY)
    }

    companion object {

        var dialogShowListener: DialogShowListener? = null

        private const val STACKTRACE_KEY =
            "ru.evotor.ui_kit.dialogs.BaseBottomSheetDialogFragment.key.stacktrace"

        @JvmStatic
        protected val TITLE_KEY = "ru.evotor.ui_kit.dialogs.BaseBottomSheetDialogFragment.title_key"
        @JvmStatic
        protected val TITLE_RES_KEY = "ru.evotor.ui_kit.dialogs.BaseBottomSheetDialogFragment.title_res_key"
        @JvmStatic
        protected val TITLE_ARGS_KEY = "ru.evotor.ui_kit.dialogs.BaseBottomSheetDialogFragment.title_args_key"
        @JvmStatic
        protected val MESSAGE_KEY = "ru.evotor.ui_kit.dialogs.BaseBottomSheetDialogFragment.message_key"
        @JvmStatic
        protected val MESSAGE_RES_KEY = "ru.evotor.ui_kit.dialogs.BaseBottomSheetDialogFragment.message_res_key"
        @JvmStatic
        protected val MESSAGE_ARGS_KEY = "ru.evotor.ui_kit.dialogs.BaseBottomSheetDialogFragment.message_args_key"

    }
}