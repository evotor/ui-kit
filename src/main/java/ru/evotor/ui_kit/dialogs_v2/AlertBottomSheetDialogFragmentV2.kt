package ru.evotor.ui_kit.dialogs_v2

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentManager
import gone
import ru.evotor.ui_kit.R
import ru.evotor.ui_kit.databinding.BottomSheetAlertLayoutV2Binding
import ru.evotor.ui_kit.dialogs.BaseBottomSheetDialogFragment
import ru.evotor.ui_kit.dialogs.ButtonDescription
import ru.evotor.ui_kit.dialogs.NewErrorStyleBackgroundDrawable
import visible

class AlertBottomSheetDialogFragmentV2 :
    BaseBottomSheetDialogFragment<BottomSheetAlertLayoutV2Binding>() {

    private val bottomButtons = arrayListOf<ButtonDescription>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomSheetAlertLayoutV2Binding
        get() = BottomSheetAlertLayoutV2Binding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isError = arguments?.getBoolean(IS_ERROR_KEY, false) ?: false
        if (isError) {
            binding.apply {
                root.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                root.background = NewErrorStyleBackgroundDrawable(requireContext())
            }
        } else {
            binding.root.setBackgroundResource(R.drawable.dialog_v2_bottom_background)
        }
        val icon = arguments?.getIcon()
            ?: R.drawable.ic_alert_circle_red_48.takeIf { isError }
        icon?.let {
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
        arguments?.getDetails()?.let {
            val args = arguments?.getDetailsArgs()
            binding.dialogDetails.text = if (args != null) {
                String.format(it, *args)
            } else {
                it
            }
            if (isError) {
                binding.dialogDetails.setPadding(
                    binding.dialogDetails.paddingStart,
                    requireContext().resources.getDimensionPixelSize(R.dimen.bottom_sheet_dialog_block_vertical_margin),
                    binding.dialogDetails.paddingEnd,
                    requireContext().resources.getDimensionPixelSize(R.dimen.bottom_sheet_dialog_padding_new_error_style),
                )
            }
            binding.dialogDetails.visible()
        } ?: binding.dialogDetails.gone()
        arguments?.getAttention()?.let {
            val args = arguments?.getAttentionArgs()
            binding.dialogAttentionText.text = if (args != null) {
                String.format(it, *args)
            } else {
                it
            }
            binding.dialogAttentionText.visible()
        } ?: binding.dialogAttentionText.gone()
        bottomButtons.forEachIndexed { index, buttonDescription ->
            val isPrimary =
                (buttonDescription is ButtonDescription.Positive ||
                    buttonDescription is ButtonDescription.Negative
                )
            val buttonStyle = if (isPrimary) {
                R.style.EvotorUITheme_Button_NewDialog_Primary
            } else {
                R.style.EvotorUITheme_Button_NewDialog_Secondary
            }
            val button = Button(
                ContextThemeWrapper(context, buttonStyle), null, buttonStyle,
            )
            button.text = buttonDescription.text
            val glowInset = if (isPrimary) {
                resources.getDimensionPixelSize(R.dimen.button_dialog_v2_shadow_radius)
            } else {
                0
            }
            if (isPrimary) {
                button.background = DialogV2ButtonBackgroundDrawable(requireContext())
            }
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val topMargin = if (index == 0) {
                resources.getDimensionPixelSize(
                    R.dimen.bottom_sheet_dialog_message_to_button_margin_v2
                )
            } else {
                resources.getDimensionPixelSize(
                    R.dimen.button_dialog_v2_margin
                )
            }
            layoutParams.setMargins(
                -glowInset,
                topMargin - glowInset,
                -glowInset,
                -glowInset
            )
            if (isPrimary) {
                val pillHeight = resources.getDimensionPixelSize(
                    R.dimen.button_dialog_v2_min_height
                )
                layoutParams.height = pillHeight + 2 * glowInset
            }
            button.layoutParams = layoutParams
            button.setOnClickListener { buttonDescription.listener?.invoke() }
            binding.dialogContentContainer.addView(button)
        }
        addCloseButton()
    }

    private fun addCloseButton() {
        val bottomSheet = dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return
        val parent = bottomSheet.parent as? ViewGroup ?: return
        val res = resources
        val iconSize = res.getDimensionPixelSize(R.dimen.bottom_sheet_dialog_close_button_icon_size)
        val button = ImageView(requireContext()).apply {
            id = R.id.dialog_close_button
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_dialog_close_button)
            scaleType = ImageView.ScaleType.CENTER
            val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close)
            icon?.setBounds(0, 0, iconSize, iconSize)
            setImageDrawable(icon)
            setOnClickListener { dismiss() }
        }
        val size = res.getDimensionPixelSize(R.dimen.bottom_sheet_dialog_close_button_size)
        val margin = res.getDimensionPixelSize(R.dimen.bottom_sheet_dialog_close_button_margin)
        val gap = res.getDimensionPixelSize(R.dimen.bottom_sheet_dialog_close_button_margin_top)
        val params = when (parent) {
            is FrameLayout -> FrameLayout.LayoutParams(size, size).apply {
                gravity = Gravity.TOP or Gravity.END
                marginEnd = margin
            }
            is CoordinatorLayout -> CoordinatorLayout.LayoutParams(size, size).apply {
                gravity = Gravity.TOP or Gravity.END
                marginEnd = margin
            }
            else -> ViewGroup.MarginLayoutParams(size, size).apply {
                marginEnd = margin
            }
        }
        parent.addView(button, params)
        parent.clipChildren = false
        parent.clipToPadding = false

        bottomSheet.addOnLayoutChangeListener { _, _, top, _, _, _, _, _, _ ->
            button.translationY = (top - size - gap).toFloat()
        }
        bottomSheet.post {
            button.translationY = (bottomSheet.top - size - gap).toFloat()
        }
    }

    fun setTitle(title: String): AlertBottomSheetDialogFragmentV2 {
        arguments?.putString(TITLE_KEY, title)
        return this
    }

    fun setTitle(
        @StringRes titleRes: Int,
        vararg titleArgs: Any
    ): AlertBottomSheetDialogFragmentV2 {
        arguments?.putInt(TITLE_RES_KEY, titleRes)
        arguments?.putStringArray(
            TITLE_ARGS_KEY,
            titleArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun setMessage(message: String): AlertBottomSheetDialogFragmentV2 {
        arguments?.putString(MESSAGE_KEY, message)
        return this
    }

    fun setMessage(
        @StringRes messageRes: Int,
        vararg messageArgs: Any
    ): AlertBottomSheetDialogFragmentV2 {
        arguments?.putInt(MESSAGE_RES_KEY, messageRes)
        arguments?.putStringArray(
            MESSAGE_ARGS_KEY,
            messageArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun setDetails(details: String): AlertBottomSheetDialogFragmentV2 {
        arguments?.putString(DETAILS_KEY, details)
        return this
    }

    fun setDetails(
        @StringRes detailsRes: Int,
        vararg detailsArgs: Any
    ): AlertBottomSheetDialogFragmentV2 {
        arguments?.putInt(DETAILS_RES_KEY, detailsRes)
        arguments?.putStringArray(
            DETAILS_ARGS_KEY,
            detailsArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun setAttentionText(attentionText: String): AlertBottomSheetDialogFragmentV2 {
        arguments?.putString(ATTENTION_KEY, attentionText)
        return this
    }

    fun setAttentionText(
        @StringRes attentionTextRes: Int,
        vararg attentionTextArgs: Any
    ): AlertBottomSheetDialogFragmentV2 {
        arguments?.putInt(ATTENTION_RES_KEY, attentionTextRes)
        arguments?.putStringArray(
            ATTENTION_ARGS_KEY,
            attentionTextArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun updateAttentionText(text: String?) {
        binding.dialogAttentionText.text = text
        binding.dialogAttentionText.isVisible = !text.isNullOrBlank()
    }

    fun updateAttentionText(
        @StringRes attentionTextRes: Int,
        vararg attentionTextArgs: Any
    ) {
        updateAttentionText(requireContext().getString(attentionTextRes, *attentionTextArgs))
    }

    fun addButton(button: ButtonDescription): AlertBottomSheetDialogFragmentV2 {
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

    fun setIconDrawable(@DrawableRes iconDrawable: Int): AlertBottomSheetDialogFragmentV2 {
        arguments?.putInt(ICON_KEY, iconDrawable)
        return this
    }

    fun setIsError(isError: Boolean): AlertBottomSheetDialogFragmentV2 {
        arguments?.putBoolean(IS_ERROR_KEY, isError)
        return this
    }

    fun setIsCancelable(isCancelable: Boolean): AlertBottomSheetDialogFragmentV2 {
        setCancelable(isCancelable)
        return this
    }

    fun show(fragmentManager: FragmentManager): AlertBottomSheetDialogFragmentV2 {
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

    companion object {

        @JvmStatic
        fun newInstance() = AlertBottomSheetDialogFragmentV2().apply {
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

        private const val TAG = "ru.evotor.ui_kit.dialogs_v2.AlertBottomSheetDialogFragmentV2.tag"

        private const val IS_ERROR_KEY = "ru.evotor.ui_kit.dialogs_v2.AlertBottomSheetDialogFragmentV2.is_error_key"
        private const val ICON_KEY = "ru.evotor.ui_kit.dialogs_v2.AlertBottomSheetDialogFragmentV2.icon_key"
    }
}
