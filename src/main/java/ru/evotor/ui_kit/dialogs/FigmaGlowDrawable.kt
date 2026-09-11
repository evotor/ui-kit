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
import dpToPx

class FigmaGlowDrawable(
    context: Context,
    private val backgroundColor: Int,
    // In Figma's linear gradient order to-top-bottom
    private val gradientColors: IntArray,
) : Drawable() {
    private val clipPath = Path()
    private val cornerRadius = context.dpToPx(8).toFloat()
    private val backgroundPaint = Paint().apply {
        color = backgroundColor
        style = Paint.Style.FILL
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        if (bounds.isEmpty || bounds.width() == 0 || bounds.height() == 0) {
            return
        }

        val viewWidth = bounds.width().toFloat()
        val viewHeight = bounds.height().toFloat()
        val halfHeight = viewHeight / 2f

        val leftX = bounds.left.toFloat()
        val rightX = bounds.right.toFloat()
        val topY = bounds.top.toFloat() - halfHeight
        val bottomY = bounds.top.toFloat() + halfHeight

        val blurRadius = (viewHeight * 0.25f).coerceAtLeast(1f)
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

        canvas.save()
        canvas.clipPath(clipPath) // Обрезаем все, что выходит за скругленные рамки View

        canvas.drawRect(bounds, backgroundPaint)

        glowPaint.shader = LinearGradient(
            leftX,
            bounds.top.toFloat(),
            rightX,
            bounds.top.toFloat(),
            gradientColors,
            null,
            Shader.TileMode.CLAMP
        )

        val ovalBounds = RectF(leftX, topY, rightX, bottomY)
        canvas.drawOval(ovalBounds, glowPaint)

        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
