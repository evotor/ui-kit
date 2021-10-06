package ru.evotor.ui_kit.dialogs

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gone
import ru.evotor.ui_kit.R
import ru.evotor.ui_kit.databinding.BottomSheetLayoutBinding
import visible


class AlertBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetLayoutBinding
    private val bottomButtons = arrayListOf<ButtonDescription>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomSheetLayoutBinding.bind(inflater.inflate(R.layout.bottom_sheet_layout, container))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as BottomSheetDialog?)?.behavior?.peekHeight = activity?.resources?.displayMetrics?.heightPixels
                ?: 0
        arguments?.getIcon()?.let {
            binding.dialogImage.setImageDrawable(ContextCompat.getDrawable(view.context, it))
            binding.dialogImage.visible()
        } ?: binding.dialogImage.gone()
        arguments?.getTitle()?.let {
            binding.dialogTitle.text = it
            binding.dialogTitle.visible()
        } ?: binding.dialogTitle.gone()
        arguments?.getMessage()?.let {
            binding.dialogMessage.text = it
            binding.dialogMessage.visible()
        } ?: binding.dialogMessage.gone()
        binding.dialogContentContainer.setBackgroundResource(
                if (arguments?.getBoolean(IS_ERROR_KEY, false) == true) {
                    R.drawable.dialog_bottom_background_error
                } else {
                    R.drawable.dialog_bottom_background_normal
                }
        )
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.dialog_transparent_background)))
        }
    }

    override fun getTheme(): Int = R.style.EvotorTheme_BottomSheetDialogTheme

    fun setTitle(title: String): AlertBottomSheetDialogFragment {
        arguments?.putString(TITLE_KEY, title)
        return this
    }

    fun setMessage(message: String): AlertBottomSheetDialogFragment {
        arguments?.putString(MESSAGE_KEY, message)
        return this
    }

    fun addButton(button: ButtonDescription): AlertBottomSheetDialogFragment {
        if (button is ButtonDescription.Dismiss) {
            button.listener = { this.dismiss() }
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
        return this
    }

    private fun Bundle.getIcon(): Int? = getInt(ICON_KEY).let {
        if (it == 0) null else it
    }

    private fun Bundle.getTitle(): String? = getString(TITLE_KEY, null)

    private fun Bundle.getMessage(): String? = getString(MESSAGE_KEY, null)

    sealed class ButtonDescription(
            val text: CharSequence,
            val style: Int,
            var listener: (() -> Unit)? = null
    ) {
        class Positive(text: CharSequence, listener: () -> Unit) : ButtonDescription(
                text,
                R.style.EvotorTheme_Button_Regular_Primary,
                listener
        )

        class Negative(text: CharSequence, listener: () -> Unit) : ButtonDescription(
                text,
                R.style.EvotorTheme_Button_Regular_Warning,
                listener
        )

        class Neutral(text: CharSequence, listener: () -> Unit) : ButtonDescription(
                text,
                R.style.EvotorTheme_Button_Regular_Additional,
                listener
        )

        class Dismiss(text: CharSequence) : ButtonDescription(
                text,
                R.style.EvotorTheme_Button_Regular_Text
        )
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

        private const val TAG = "ru.evotor.ui_kit.dialogs.alert_dialog_fragment.tag"

        private const val IS_ERROR_KEY = "ru.evotor.ui_kit.dialogs.alert_dialog_fragment.is_error_key"
        private const val TITLE_KEY = "ru.evotor.ui_kit.dialogs.alert_dialog_fragment.title_key"
        private const val MESSAGE_KEY = "ru.evotor.ui_kit.dialogs.alert_dialog_fragment.message_key"
        private const val ICON_KEY = "ru.evotor.ui_kit.dialogs.alert_dialog_fragment.icon_key"
    }
}