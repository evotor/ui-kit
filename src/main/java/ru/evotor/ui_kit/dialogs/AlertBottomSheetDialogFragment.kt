package ru.evotor.ui_kit.dialogs

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
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
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentManager
import dpToPx
import gone
import ru.evotor.ui_kit.R
import ru.evotor.ui_kit.databinding.BottomSheetAlertLayoutBinding
import visible
import androidx.core.graphics.withClip


class AlertBottomSheetDialogFragment : BaseBottomSheetDialogFragment<BottomSheetAlertLayoutBinding>() {

    private val bottomButtons = arrayListOf<ButtonDescription>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomSheetAlertLayoutBinding
        get() = BottomSheetAlertLayoutBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isError = arguments?.getBoolean(IS_ERROR_KEY, false) ?: false
        val useNewErrorStyle = arguments?.getBoolean(USE_NEW_ERROR_STYLE_KEY, false)
            ?: false
        val icon = arguments?.getIcon()
            ?: R.drawable.ic_alert_circle_red_48.takeIf { isError && useNewErrorStyle }
        binding.root.setPadding(
            requireContext().resources.getDimensionPixelSize(
                if (isError && useNewErrorStyle) {
                    R.dimen.bottom_sheet_dialog_padding_new_error_style
                } else {
                    R.dimen.bottom_sheet_dialog_padding
                }
            )
        )
        if (isError) {
            binding.apply {
                if (useNewErrorStyle) {
                    root.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    root.background = NewErrorStyleBackgroundDrawable(requireContext())
                    dragHandle.isVisible = true
                } else {
                    root.setBackgroundResource(R.drawable.dialog_bottom_background_error)
                }
            }
        }
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
            if (isError && useNewErrorStyle) {
                binding.dialogTitle.layoutParams =
                    (binding.dialogTitle.layoutParams as LinearLayout.LayoutParams).apply {
                        topMargin = 0
                    }
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
            if (isError && useNewErrorStyle) {
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
        bottomButtons.forEach { buttonDescription ->
            val button = Button(
                ContextThemeWrapper(
                    context,
                    if (isError) {
                        if (useNewErrorStyle) {
                            buttonDescription.getNewErrorStyle()
                        } else {
                            buttonDescription.errorStyle
                        }
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
                0,
                resources.getDimension(R.dimen.bottom_sheet_dialog_block_vertical_margin).toInt(),
                0,
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

    fun setDetails(details: String): AlertBottomSheetDialogFragment {
        arguments?.putString(DETAILS_KEY, details)
        return this
    }

    fun setDetails(
        @StringRes detailsRes: Int,
        vararg detailsArgs: Any
    ): AlertBottomSheetDialogFragment {
        arguments?.putInt(DETAILS_RES_KEY, detailsRes)
        arguments?.putStringArray(
            DETAILS_ARGS_KEY,
            detailsArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun setAttentionText(attentionText: String): AlertBottomSheetDialogFragment {
        arguments?.putString(ATTENTION_KEY, attentionText)
        return this
    }

    fun setAttentionText(
        @StringRes attentionTextRes: Int,
        vararg attentionTextArgs: Any
    ): AlertBottomSheetDialogFragment {
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

    fun setUseNewErrorStyle(useNewErrorStyle: Boolean): AlertBottomSheetDialogFragment {
        arguments?.putBoolean(USE_NEW_ERROR_STYLE_KEY, useNewErrorStyle)
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
        private const val USE_NEW_ERROR_STYLE_KEY = "ru.evotor.ui_kit.dialogs.AlertBottomSheetDialogFragment.use_new_error_style_key"
        private const val ICON_KEY = "ru.evotor.ui_kit.dialogs.AlertBottomSheetDialogFragment.icon_key"
    }
}

private class NewErrorStyleBackgroundDrawable(context: Context) : Drawable() {
    private val clipPath = Path()
    private val cornerRadius = context.dpToPx(8).toFloat()
    private val backgroundPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.table_zebra)
        style = Paint.Style.FILL
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        if (bounds.isEmpty || bounds.width() == 0 || bounds.height() == 0) {
            return
        }

        val viewHeight = bounds.height()
        val viewWidth = bounds.width()

        val blurRadius = (minOf(viewWidth, viewHeight) * 0.25f).coerceAtLeast(1f)
        glowPaint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)

        clipPath.reset()
        val radii = floatArrayOf(
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            0f,
            0f,
            0f,
            0f
        )
        clipPath.addRoundRect(
            RectF(
                bounds.left.toFloat(),
                bounds.top.toFloat(),
                bounds.right.toFloat(),
                bounds.bottom.toFloat()
            ),
            radii,
            Path.Direction.CW
        )

        canvas.withClip(clipPath) {
            drawRect(bounds, backgroundPaint)

            val halfHeight = viewHeight / 2

            val leftX = bounds.left.toFloat()
            val rightX = bounds.right.toFloat()
            val topY = bounds.top.toFloat() - (halfHeight * 0.75f)
            val bottomY = bounds.top.toFloat() + (halfHeight * 0.75f)

            glowPaint.shader = LinearGradient(
                leftX,
                bounds.top.toFloat(),
                rightX,
                bounds.top.toFloat(),
                intArrayOf(0x409541DE, 0x40EE3D3D, 0x40BD5D22),
                null,
                Shader.TileMode.CLAMP
            )

            val ovalBounds = RectF(leftX, topY, rightX, bottomY)
            drawOval(ovalBounds, glowPaint)
        }
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
