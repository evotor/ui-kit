package ru.evotor.ui_kit.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import gone
import ru.evotor.ui_kit.R
import ru.evotor.ui_kit.databinding.BottomSheetAlertLayoutBinding
import ru.evotor.ui_kit.dialogs.base.StackTraceUtils
import visible


class AlertBottomSheetDialogFragment : BaseBottomSheetDialogFragment<BottomSheetAlertLayoutBinding>() {

    private val bottomButtons = arrayListOf<ButtonDescription>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomSheetAlertLayoutBinding
        get() = BottomSheetAlertLayoutBinding::inflate

    var onDestroyCallback: (() -> (Unit))? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getIcon()?.let {
            binding.dialogImage.setImageDrawable(ContextCompat.getDrawable(view.context, it))
            binding.dialogImage.visible()
        } ?: binding.dialogImage.gone()
        arguments?.getTitle()?.let {
            val args = arguments?.getTitleArgs()
            binding.dialogTitle.text = if (args != null) {
                String.format(it, *args)
            } else {
                it
            }
            binding.dialogTitle.visible()
        } ?: binding.dialogTitle.gone()
        arguments?.getMessage()?.let {
            val args = arguments?.getMessageArgs()
            binding.dialogMessage.text = if (args != null) {
                String.format(it, *args)
            } else {
            it
        }
            binding.dialogMessage.visible()
        } ?: binding.dialogMessage.gone()
        val isError = arguments?.getBoolean(IS_ERROR_KEY, false) ?: false
        if (isError) {
            binding.root.setBackgroundResource(R.drawable.dialog_bottom_background_error)
        }
        bottomButtons.forEach { buttonDescription ->
            val button = Button(
                ContextThemeWrapper(
                    context,
                    if (isError) {
                        buttonDescription.errorStyle
                    } else {
                        buttonDescription.style
                    }
                ), null, buttonDescription.style
            )
            button.text = buttonDescription.text
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(
                resources.getDimension(R.dimen.bottom_sheet_dialog_block_horizontal_margin).toInt(),
                resources.getDimension(R.dimen.bottom_sheet_dialog_block_vertical_margin).toInt(),
                resources.getDimension(R.dimen.bottom_sheet_dialog_block_horizontal_margin).toInt(),
                resources.getDimension(R.dimen.bottom_sheet_dialog_block_vertical_margin).toInt()
            )
            button.layoutParams = layoutParams
            button.setOnClickListener { buttonDescription.listener?.invoke() }
            binding.dialogContentContainer.addView(button)
        }
    }

    fun setTitle(title: String): AlertBottomSheetDialogFragment {
        arguments?.putString(TITLE_KEY, title)
        return this
    }

    fun setTitle(
        @StringRes titleRes: Int,
        vararg titleArgs: Any
    ): AlertBottomSheetDialogFragment {
        arguments?.putInt(TITLE_RES_KEY, titleRes)
        arguments?.putStringArray(
            TITLE_ARGS_KEY,
            titleArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun setMessage(message: String): AlertBottomSheetDialogFragment {
        arguments?.putString(MESSAGE_KEY, message)
        return this
    }

    fun setMessage(
        @StringRes messageRes: Int,
        vararg messageArgs: Any
    ): AlertBottomSheetDialogFragment {
        arguments?.putInt(MESSAGE_RES_KEY, messageRes)
        arguments?.putStringArray(
            MESSAGE_ARGS_KEY,
            messageArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun addButton(button: ButtonDescription): AlertBottomSheetDialogFragment {
        if (button is ButtonDescription.Dismiss) {
            if (button.listener == null) {
                button.listener = {
                    this.dismiss()
                }
            }
        }
        bottomButtons.add(button)
        return this
    }

    fun setIconDrawable(@DrawableRes iconDrawable: Int): AlertBottomSheetDialogFragment {
        arguments?.putInt(ICON_KEY, iconDrawable)
        return this
    }

    fun setIsError(isError: Boolean): AlertBottomSheetDialogFragment {
        arguments?.putBoolean(IS_ERROR_KEY, isError)
        return this
    }

    fun setIsCancelable(isCancelable: Boolean): AlertBottomSheetDialogFragment {
        setCancelable(isCancelable)
        return this
    }

    fun show(fragmentManager: FragmentManager): AlertBottomSheetDialogFragment {
        try {
            fragmentManager.beginTransaction().add(this, TAG).commitNowAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            fragmentManager.beginTransaction().add(this, TAG).commitAllowingStateLoss()
        }
        saveStacktrace()
        return this
    }

    private fun Bundle.getIcon(): Int? = getInt(ICON_KEY).let {
        if (it == 0) null else it
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyCallback?.invoke()
    }
    companion object {

        @JvmStatic
        fun newInstance() = AlertBottomSheetDialogFragment().apply {
            arguments = Bundle()
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

        private const val TAG = "ru.evotor.ui_kit.dialogs.AlertBottomSheetDialogFragment.tag"

        private const val IS_ERROR_KEY = "ru.evotor.ui_kit.dialogs.AlertBottomSheetDialogFragment.is_error_key"
        private const val ICON_KEY = "ru.evotor.ui_kit.dialogs.AlertBottomSheetDialogFragment.icon_key"
    }
}