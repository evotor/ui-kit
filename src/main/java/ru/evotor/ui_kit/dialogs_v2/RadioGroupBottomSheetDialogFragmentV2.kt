package ru.evotor.ui_kit.dialogs_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import gone
import ru.evotor.ui_kit.R
import ru.evotor.ui_kit.databinding.BottomSheetRadioGroupLayoutV2Binding
import ru.evotor.ui_kit.dialogs.BaseBottomSheetDialogFragment
import ru.evotor.ui_kit.widgets.GroupedListItemBackground
import visible

class RadioGroupBottomSheetDialogFragmentV2 :
    BaseBottomSheetDialogFragment<BottomSheetRadioGroupLayoutV2Binding>() {

    var onItemSelectedListener: ((position: Int) -> Unit)? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomSheetRadioGroupLayoutV2Binding
        get() = BottomSheetRadioGroupLayoutV2Binding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.background = null
        binding.dialogContentContainer.setBackgroundResource(R.drawable.dialog_v2_bottom_background)
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
        val selectedItemPos = arguments?.getSelectedItemPos()
        val lockedItems = arguments?.getLockedItems()
        arguments?.getItems()?.let { list ->
            list.forEachIndexed { index, title ->
                val itemView = layoutInflater.inflate(
                    R.layout.item_radio_group_dialog_v2,
                    binding.radioGroup,
                    false
                ) as LinearLayout
                val radioButton = itemView.findViewById<RadioButton>(R.id.radio_button)
                val titleView = itemView.findViewById<TextView>(R.id.text)
                val lockIcon = itemView.findViewById<ImageView>(R.id.lock_icon)
                titleView.text = title
                if (lockedItems?.getOrNull(index) == true) {
                    lockIcon.visible()
                } else {
                    lockIcon.gone()
                }
                radioButton.isChecked = index == selectedItemPos
                itemView.setOnClickListener {
                    for (childIndex in 0 until binding.radioGroup.childCount) {
                        val child = binding.radioGroup.getChildAt(childIndex)
                        child.findViewById<RadioButton>(R.id.radio_button)?.isChecked =
                            child === itemView
                    }
                    onItemSelectedListener?.invoke(index)
                    dismiss()
                }
                binding.radioGroup.addView(itemView)
            }
        }
        GroupedListItemBackground.applyGroupBackgrounds(binding.radioGroup)
        binding.dialogCloseButton.setOnClickListener { dismiss() }
        applyMaxListHeight()
    }

    private fun applyMaxListHeight() {
        binding.radioGroupScroll.post {
            val maxHeight =
                (resources.displayMetrics.heightPixels * MAX_LIST_HEIGHT_SCREEN_RATIO).toInt()
            if (binding.radioGroupScroll.height > maxHeight) {
                binding.radioGroupScroll.layoutParams.height = maxHeight
                binding.radioGroupScroll.requestLayout()
            }
        }
    }

    fun setTitle(title: String): RadioGroupBottomSheetDialogFragmentV2 {
        arguments?.putString(TITLE_KEY, title)
        return this
    }

    fun setTitle(
        @StringRes titleRes: Int,
        vararg titleArgs: Any
    ): RadioGroupBottomSheetDialogFragmentV2 {
        arguments?.putInt(TITLE_RES_KEY, titleRes)
        arguments?.putStringArray(
            TITLE_ARGS_KEY,
            titleArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun setMessage(message: String): RadioGroupBottomSheetDialogFragmentV2 {
        arguments?.putString(MESSAGE_KEY, message)
        return this
    }

    fun setMessage(
        @StringRes messageRes: Int,
        vararg messageArgs: Any
    ): RadioGroupBottomSheetDialogFragmentV2 {
        arguments?.putInt(MESSAGE_RES_KEY, messageRes)
        arguments?.putStringArray(
            MESSAGE_ARGS_KEY,
            messageArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun setItems(
        items: List<Item>,
        selectedPosition: Int? = null
    ): RadioGroupBottomSheetDialogFragmentV2 {
        return setItems(
            items = items.map { it.title }.toTypedArray(),
            selectedPosition = selectedPosition,
            lockedItems = items.map { it.isLocked }.toBooleanArray()
        )
    }

    fun setItems(
        items: Array<String>,
        selectedPosition: Int? = null,
        lockedItems: BooleanArray? = null
    ): RadioGroupBottomSheetDialogFragmentV2 {
        arguments?.putStringArray(ITEMS_KEY, items)
        selectedPosition?.let {
            arguments?.putInt(SELECTED_ITEM_KEY, it)
        }
        lockedItems?.let {
            arguments?.putBooleanArray(LOCKED_ITEMS_KEY, it)
        }
        return this
    }

    fun setIsCancelable(isCancelable: Boolean): RadioGroupBottomSheetDialogFragmentV2 {
        setCancelable(isCancelable)
        return this
    }

    fun show(fragmentManager: FragmentManager): RadioGroupBottomSheetDialogFragmentV2 {
        try {
            fragmentManager.beginTransaction().add(this, TAG).commitNowAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            fragmentManager.beginTransaction().add(this, TAG).commitAllowingStateLoss()
        }
        saveStacktrace()
        return this
    }

    private fun Bundle.getItems(): Array<String>? {
        return if (containsKey(ITEMS_KEY)) {
            getStringArray(ITEMS_KEY)
        } else {
            null
        }
    }

    private fun Bundle.getLockedItems(): BooleanArray? {
        return if (containsKey(LOCKED_ITEMS_KEY)) {
            getBooleanArray(LOCKED_ITEMS_KEY)
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

    data class Item(
        val title: String,
        val isLocked: Boolean = false
    )

    companion object {

        private const val MAX_LIST_HEIGHT_SCREEN_RATIO = 0.72f

        @JvmStatic
        fun newInstance() = RadioGroupBottomSheetDialogFragmentV2().apply {
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

        private const val TAG =
            "ru.evotor.ui_kit.dialogs_v2.RadioGroupBottomSheetDialogFragmentV2.tag"

        private const val ITEMS_KEY =
            "ru.evotor.ui_kit.dialogs_v2.RadioGroupBottomSheetDialogFragmentV2.items_key"
        private const val LOCKED_ITEMS_KEY =
            "ru.evotor.ui_kit.dialogs_v2.RadioGroupBottomSheetDialogFragmentV2.locked_items_key"
        private const val SELECTED_ITEM_KEY =
            "ru.evotor.ui_kit.dialogs_v2.RadioGroupBottomSheetDialogFragmentV2.selected_item_key"
    }
}
