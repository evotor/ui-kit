package ru.evotor.ui_kit.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import ru.evotor.ui_kit.R

object GroupedListItemBackground {

    fun applyGroupBackgrounds(
        container: LinearLayout,
        @ColorRes colorRes: Int = R.color.bg_neutral_secondary,
        @DimenRes largeRadiusRes: Int = R.dimen.corner_medium,
        @DimenRes smallRadiusRes: Int = R.dimen.corner_extra_small
    ) {
        val visible = (0 until container.childCount)
            .map { container.getChildAt(it) }
            .filter { it.visibility == View.VISIBLE }
        visible.forEachIndexed { index, view ->
            view.background = itemBackground(
                context = view.context,
                topLarge = index == 0,
                bottomLarge = index == visible.lastIndex,
                colorRes = colorRes,
                largeRadiusRes = largeRadiusRes,
                smallRadiusRes = smallRadiusRes
            )
        }
    }

    @JvmOverloads
    fun applyItemBackground(
        view: View,
        topLarge: Boolean,
        bottomLarge: Boolean,
        @ColorRes colorRes: Int = R.color.bg_neutral_secondary,
        @DimenRes largeRadiusRes: Int = R.dimen.corner_medium,
        @DimenRes smallRadiusRes: Int = R.dimen.corner_extra_small
    ) {
        view.background = itemBackground(
            context = view.context,
            topLarge = topLarge,
            bottomLarge = bottomLarge,
            colorRes = colorRes,
            largeRadiusRes = largeRadiusRes,
            smallRadiusRes = smallRadiusRes
        )
    }

    fun itemBackground(
        context: Context,
        topLarge: Boolean,
        bottomLarge: Boolean,
        @ColorRes colorRes: Int = R.color.bg_neutral_secondary,
        @DimenRes largeRadiusRes: Int = R.dimen.corner_medium,
        @DimenRes smallRadiusRes: Int = R.dimen.corner_extra_small
    ): Drawable = roundedBackground(
        context = context,
        colorRes = colorRes,
        topLarge = topLarge,
        bottomLarge = bottomLarge,
        largeRadiusRes = largeRadiusRes,
        smallRadiusRes = smallRadiusRes
    )

    private fun roundedBackground(
        context: Context,
        @ColorRes colorRes: Int,
        topLarge: Boolean,
        bottomLarge: Boolean,
        @DimenRes largeRadiusRes: Int,
        @DimenRes smallRadiusRes: Int
    ): RippleDrawable {
        val content = roundedShape(
            context = context,
            colorRes = colorRes,
            topLarge = topLarge,
            bottomLarge = bottomLarge,
            largeRadiusRes = largeRadiusRes,
            smallRadiusRes = smallRadiusRes
        )
        val mask = roundedShape(
            context = context,
            colorRes = android.R.color.white,
            topLarge = topLarge,
            bottomLarge = bottomLarge,
            largeRadiusRes = largeRadiusRes,
            smallRadiusRes = smallRadiusRes
        )
        return RippleDrawable(ColorStateList.valueOf(rippleColor(context)), content, mask)
    }

    private fun roundedShape(
        context: Context,
        @ColorRes colorRes: Int,
        topLarge: Boolean,
        bottomLarge: Boolean,
        @DimenRes largeRadiusRes: Int,
        @DimenRes smallRadiusRes: Int
    ): GradientDrawable {
        val large = context.resources.getDimension(largeRadiusRes)
        val small = context.resources.getDimension(smallRadiusRes)
        val top = if (topLarge) large else small
        val bottom = if (bottomLarge) large else small
        return GradientDrawable().apply {
            setColor(ContextCompat.getColor(context, colorRes))
            cornerRadii = floatArrayOf(top, top, top, top, bottom, bottom, bottom, bottom)
        }
    }

    private fun rippleColor(context: Context): Int {
        val value = TypedValue()
        val resolved = context.theme.resolveAttribute(
            android.R.attr.colorControlHighlight,
            value,
            true
        )
        return when {
            !resolved -> ContextCompat.getColor(context, R.color.button_ripple_color)
            value.resourceId != 0 -> ContextCompat.getColor(context, value.resourceId)
            else -> value.data
        }
    }
}
