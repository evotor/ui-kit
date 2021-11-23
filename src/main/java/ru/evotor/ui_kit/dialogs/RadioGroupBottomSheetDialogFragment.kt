package ru.evotor.ui_kit.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.annotation.StringRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import gone
import ru.evotor.ui_kit.R
import ru.evotor.ui_kit.databinding.BottomSheetRadioGroupLayoutBinding
import visible

class RadioGroupBottomSheetDialogFragment : BaseBottomSheetDialogFragment<BottomSheetRadioGroupLayoutBinding>() {

    private val bottomButtons = arrayListOf<ButtonDescription>()

    var onItemSelectedListener: ((position: Int) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getTitle()?.let {
            binding.dialogTitle.text = it
            binding.dialogTitle.visible()
        } ?: binding.dialogTitle.gone()
        arguments?.getMessage()?.let {
            binding.dialogMessage.text = it
            binding.dialogMessage.visible()
        } ?: binding.dialogMessage.gone()
        val selectedItemPos = arguments?.getSelectedItemPos()
        arguments?.getItems()?.let { list ->
            for (i in list.indices) {
                binding.radioGroup.addView(
                        RadioButton(ContextThemeWrapper(context, R.style.EvotorUITheme_RadioButton)).apply {
                            val layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    resources.getDimension(R.dimen.button_big_min_height).toInt()
                            )
                            setLayoutParams(layoutParams)
                            text = list[i]
                            isChecked = i == selectedItemPos
                            id = i
                            background = ResourcesCompat.getDrawable(resources, R.drawable.radio_button_background, null)
                        }
                )
            }
        }
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedItemPos?.let {
                if (checkedId != selectedItemPos) {
                    group.findViewById<RadioButton>(selectedItemPos).isChecked = false
                }
                onItemSelectedListener?.invoke(checkedId)
                dismiss()
            }
        }
        bottomButtons.forEach { buttonDescription ->
            val button = Button(ContextThemeWrapper(context, buttonDescription.style), null, buttonDescription.style)
            button.text = buttonDescription.text
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
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

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomSheetRadioGroupLayoutBinding
        get() = BottomSheetRadioGroupLayoutBinding::inflate

    fun setTitle(title: String): RadioGroupBottomSheetDialogFragment {
        arguments?.putString(TITLE_KEY, title)
        return this
    }

    fun setTitle(@StringRes titleRes: Int): RadioGroupBottomSheetDialogFragment {
        arguments?.putInt(TITLE_RES_KEY, titleRes)
        return this
    }

    fun setMessage(message: String): RadioGroupBottomSheetDialogFragment {
        arguments?.putString(MESSAGE_KEY, message)
        return this
    }

    fun setMessage(@StringRes messageRes: Int): RadioGroupBottomSheetDialogFragment {
        arguments?.putInt(MESSAGE_RES_KEY, messageRes)
        return this
    }

    fun addButton(button: ButtonDescription): RadioGroupBottomSheetDialogFragment {
        if (button is ButtonDescription.Dismiss) {
            if(button.listener == null) {
                button.listener = {
                    this.dismiss()
                }
            }
        }
        bottomButtons.add(button)
        return this
    }

    fun setIsCancelable(isCancelable: Boolean): RadioGroupBottomSheetDialogFragment {
        setCancelable(isCancelable)
        return this
    }

    fun setItems(list: Array<String>, selectedPosition: Int? = null): RadioGroupBottomSheetDialogFragment {
        arguments?.putStringArray(ITEMS_KEY, list)
        selectedPosition?.let {
            arguments?.putInt(SELECTED_ITEM_KEY, it)
        }
        return this
    }

    fun show(fragmentManager: FragmentManager): RadioGroupBottomSheetDialogFragment {
        try {
            fragmentManager.beginTransaction().add(this, TAG).commitNowAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            fragmentManager.beginTransaction().add(this, TAG).commitAllowingStateLoss()
        }
        return this
    }

    private fun Bundle.getTitle(): String? {
        val titleRes = getInt(TITLE_RES_KEY, 0)
        return if (titleRes == 0) {
            getString(TITLE_KEY, null)
        } else {
            context?.getString(titleRes)
        }
    }

    private fun Bundle.getMessage(): String? {
        val messageRes = getInt(MESSAGE_RES_KEY, 0)
        return if (messageRes == 0) {
            getString(MESSAGE_KEY, null)
        } else {
            context?.getString(messageRes)
        }
    }

    private fun Bundle.getItems(): Array<String>? {
        return if (containsKey(ITEMS_KEY)) {
            getStringArray(ITEMS_KEY)
        } else {
            null
        }
    }

    private fun Bundle.getSelectedItemPos(): Int? {
        return if (containsKey(SELECTED_ITEM_KEY)) {
            getInt(SELECTED_ITEM_KEY)
        } else {
            null
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = RadioGroupBottomSheetDialogFragment().apply {
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

        private const val TAG = "ru.evotor.ui_kit.dialogs.list_dialog_fragment.tag"

        private const val TITLE_KEY = "ru.evotor.ui_kit.dialogs.list_dialog_fragment.title_key"
        private const val TITLE_RES_KEY = "ru.evotor.ui_kit.dialogs.list_dialog_fragment.title_res_key"
        private const val MESSAGE_KEY = "ru.evotor.ui_kit.dialogs.list_dialog_fragment.message_key"
        private const val MESSAGE_RES_KEY = "ru.evotor.ui_kit.dialogs.list_dialog_fragment.message_res_key"
        private const val ITEMS_KEY = "ru.evotor.ui_kit.dialogs.list_dialog_fragment.items_res_key"
        private const val SELECTED_ITEM_KEY = "ru.evotor.ui_kit.dialogs.list_dialog_fragment.selected_item_res_key"
    }
}