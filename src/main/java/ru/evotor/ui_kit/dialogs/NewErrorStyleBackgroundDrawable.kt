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
import androidx.core.content.ContextCompat
import androidx.core.graphics.withClip
import dpToPx
import ru.evotor.ui_kit.R

internal class NewErrorStyleBackgroundDrawable(context: Context) : Drawable() {
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
