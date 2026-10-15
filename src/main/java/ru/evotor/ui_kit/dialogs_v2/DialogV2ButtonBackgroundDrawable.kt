package ru.evotor.ui_kit.dialogs_v2

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import ru.evotor.ui_kit.R

internal class DialogV2ButtonBackgroundDrawable(context: Context) : Drawable() {

    private val cornerRadius =
        context.resources.getDimension(R.dimen.button_dialog_v2_corner_radius)

    private val shadowRadius =
        context.resources.getDimension(R.dimen.button_dialog_v2_shadow_radius)

    private val gradientStart = ContextCompat.getColor(context, R.color.gradient_accent_start)
    private val gradientEnd = ContextCompat.getColor(context, R.color.gradient_accent_end)
    private val shadowColor = ContextCompat.getColor(context, R.color.button_new_dialog_shadow)

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = shadowColor
        maskFilter = BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.NORMAL)
    }

    override fun draw(canvas: Canvas) {
        if (bounds.isEmpty) return

        val rect = RectF(bounds)
        val inset = shadowRadius
        val buttonRect = RectF(
            rect.left + inset,
            rect.top + inset,
            rect.right - inset,
            rect.bottom - inset
        )
        if (buttonRect.isEmpty) return

        canvas.drawRoundRect(buttonRect, cornerRadius, cornerRadius, shadowPaint)

        fillPaint.shader = LinearGradient(
            buttonRect.left,
            buttonRect.top,
            buttonRect.right,
            buttonRect.top,
            gradientStart,
            gradientEnd,
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(buttonRect, cornerRadius, cornerRadius, fillPaint)
    }

    override fun setAlpha(alpha: Int) {
        fillPaint.alpha = alpha
        shadowPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        fillPaint.colorFilter = colorFilter
        shadowPaint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getPadding(padding: Rect): Boolean {
        val inset = shadowRadius.toInt()
        padding.set(inset, inset, inset, inset)
        return true
    }
}
