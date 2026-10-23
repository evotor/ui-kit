package ru.evotor.ui_kit.dialogs_v2

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import gone
import ru.evotor.ui_kit.R
import ru.evotor.ui_kit.databinding.BottomSheetInputTextLayoutV2Binding
import ru.evotor.ui_kit.dialogs.BaseBottomSheetDialogFragment
import ru.evotor.ui_kit.dialogs.ButtonDescription
import ru.evotor.ui_kit.dialogs.NewErrorStyleBackgroundDrawable
import visible

class InputTextBottomSheetDialogFragmentV2 :
    BaseBottomSheetDialogFragment<BottomSheetInputTextLayoutV2Binding>() {

    private val bottomButtons = arrayListOf<ButtonDescription>()

    private var onSaveListener: ((first: String, second: String) -> Unit)? = null

    val firstValue: String
        get() = binding.inputFirst.text?.toString().orEmpty()

    val secondValue: String
        get() = binding.inputSecond.text?.toString().orEmpty()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomSheetInputTextLayoutV2Binding
        get() = BottomSheetInputTextLayoutV2Binding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isError = arguments?.getBoolean(IS_ERROR_KEY, false) ?: false
        binding.root.background = null
        if (isError) {
            binding.apply {
                dialogContentContainer.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                dialogContentContainer.background = NewErrorStyleBackgroundDrawable(requireContext())
            }
        } else {
            binding.dialogContentContainer.setBackgroundResource(R.drawable.dialog_v2_bottom_background)
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
        bindInput(
            binding.inputFirstContainer,
            binding.inputFirst,
            FIRST_VALUE_KEY,
            FIRST_HINT_RES_KEY
        )
        bindInput(
            binding.inputSecondContainer,
            binding.inputSecond,
            SECOND_VALUE_KEY,
            SECOND_HINT_RES_KEY
        )
        val hasSecondInput = arguments?.getInt(SECOND_HINT_RES_KEY, 0)?.takeIf { it != 0 } != null ||
            !arguments?.getString(SECOND_VALUE_KEY).isNullOrEmpty()
        binding.inputSecondContainer.isVisible = hasSecondInput
        if (!hasSecondInput) {
            binding.inputFirst.imeOptions = EditorInfo.IME_ACTION_DONE
        }
        val filters = arrayListOf<InputFilter>()
        arguments?.getInt(MAX_LENGTH_KEY, 0)?.takeIf { it > 0 }?.let { maxLength ->
            filters.add(InputFilter.LengthFilter(maxLength))
        }
        arguments?.getString(NUMBER_MIN_KEY)?.let { min ->
            val max = arguments?.getString(NUMBER_MAX_KEY)
            val minValue = min.toBigDecimalOrNull()
            val maxValue = max?.toBigDecimalOrNull()
            if (minValue != null && maxValue != null) {
                filters.add(NumberRangeInputFilter(minValue, maxValue))
                val numberInputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
                binding.inputFirst.inputType = numberInputType
                binding.inputSecond.inputType = numberInputType
            }
        }
        if (filters.isNotEmpty()) {
            binding.inputFirst.filters = filters.toTypedArray()
            binding.inputSecond.filters = filters.toTypedArray()
        }
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
                button.background = DialogButtonBackgroundDrawable(requireContext())
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
            button.setOnClickListener {
                if (buttonDescription is ButtonDescription.Positive) {
                    onSaveListener?.invoke(firstValue, secondValue)
                }
                buttonDescription.listener?.invoke()
                if (buttonDescription is ButtonDescription.Positive) {
                    dismiss()
                }
            }
            binding.dialogContentContainer.addView(button)
        }
        binding.dialogCloseButton.setOnClickListener { dismiss() }
    }

    private fun bindInput(
        container: TextInputLayout,
        input: TextInputEditText,
        valueKey: String,
        hintResKey: String
    ) {
        input.setText(arguments?.getString(valueKey).orEmpty())
        input.setSelection(input.text?.length ?: 0)
        arguments?.getInt(hintResKey, 0)?.takeIf { it != 0 }?.let {
            container.hint = getString(it)
        }
    }

    fun setTitle(title: String): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putString(TITLE_KEY, title)
        return this
    }

    fun setTitle(
        @StringRes titleRes: Int,
        vararg titleArgs: Any
    ): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putInt(TITLE_RES_KEY, titleRes)
        arguments?.putStringArray(
            TITLE_ARGS_KEY,
            titleArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun setMessage(message: String): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putString(MESSAGE_KEY, message)
        return this
    }

    fun setMessage(
        @StringRes messageRes: Int,
        vararg messageArgs: Any
    ): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putInt(MESSAGE_RES_KEY, messageRes)
        arguments?.putStringArray(
            MESSAGE_ARGS_KEY,
            messageArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun setFirstInput(value: String, @StringRes hintRes: Int): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putString(FIRST_VALUE_KEY, value)
        arguments?.putInt(FIRST_HINT_RES_KEY, hintRes)
        return this
    }

    fun setSecondInput(value: String, @StringRes hintRes: Int): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putString(SECOND_VALUE_KEY, value)
        arguments?.putInt(SECOND_HINT_RES_KEY, hintRes)
        return this
    }

    fun setMaxLength(maxLength: Int): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putInt(MAX_LENGTH_KEY, maxLength)
        return this
    }

    fun setNumericInput(
        min: String,
        max: String
    ): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putString(NUMBER_MIN_KEY, min)
        arguments?.putString(NUMBER_MAX_KEY, max)
        return this
    }

    fun setDetails(details: String): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putString(DETAILS_KEY, details)
        return this
    }

    fun setDetails(
        @StringRes detailsRes: Int,
        vararg detailsArgs: Any
    ): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putInt(DETAILS_RES_KEY, detailsRes)
        arguments?.putStringArray(
            DETAILS_ARGS_KEY,
            detailsArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun setAttentionText(attentionText: String): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putString(ATTENTION_KEY, attentionText)
        return this
    }

    fun setAttentionText(
        @StringRes attentionTextRes: Int,
        vararg attentionTextArgs: Any
    ): InputTextBottomSheetDialogFragmentV2 {
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

    fun setOnSaveListener(listener: (first: String, second: String) -> Unit): InputTextBottomSheetDialogFragmentV2 {
        onSaveListener = listener
        return this
    }

    fun addButton(button: ButtonDescription): InputTextBottomSheetDialogFragmentV2 {
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

    fun setIconDrawable(@DrawableRes iconDrawable: Int): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putInt(ICON_KEY, iconDrawable)
        return this
    }

    fun setIsError(isError: Boolean): InputTextBottomSheetDialogFragmentV2 {
        arguments?.putBoolean(IS_ERROR_KEY, isError)
        return this
    }

    fun setIsCancelable(isCancelable: Boolean): InputTextBottomSheetDialogFragmentV2 {
        setCancelable(isCancelable)
        return this
    }

    fun show(fragmentManager: FragmentManager): InputTextBottomSheetDialogFragmentV2 {
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
        fun newInstance() = InputTextBottomSheetDialogFragmentV2().apply {
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

        private const val TAG = "ru.evotor.ui_kit.dialogs_v2.InputTextBottomSheetDialogFragment.tag"

        private const val IS_ERROR_KEY = "ru.evotor.ui_kit.dialogs_v2.InputTextBottomSheetDialogFragment.is_error_key"
        private const val ICON_KEY = "ru.evotor.ui_kit.dialogs_v2.InputTextBottomSheetDialogFragment.icon_key"
        private const val FIRST_VALUE_KEY = "ru.evotor.ui_kit.dialogs_v2.InputTextBottomSheetDialogFragment.first_value_key"
        private const val FIRST_HINT_RES_KEY = "ru.evotor.ui_kit.dialogs_v2.InputTextBottomSheetDialogFragment.first_hint_res_key"
        private const val SECOND_VALUE_KEY = "ru.evotor.ui_kit.dialogs_v2.InputTextBottomSheetDialogFragment.second_value_key"
        private const val SECOND_HINT_RES_KEY = "ru.evotor.ui_kit.dialogs_v2.InputTextBottomSheetDialogFragment.second_hint_res_key"
        private const val MAX_LENGTH_KEY = "ru.evotor.ui_kit.dialogs_v2.InputTextBottomSheetDialogFragment.max_length_key"
        private const val NUMBER_MIN_KEY = "ru.evotor.ui_kit.dialogs_v2.InputTextBottomSheetDialogFragment.number_min_key"
        private const val NUMBER_MAX_KEY = "ru.evotor.ui_kit.dialogs_v2.InputTextBottomSheetDialogFragment.number_max_key"
    }
}
