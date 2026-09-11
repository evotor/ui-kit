package ru.evotor.ui_kit.dialogs

import androidx.annotation.StyleRes
import ru.evotor.ui_kit.R

sealed class ButtonDescription(
        val text: CharSequence,
        val style: Int,
        var listener: (() -> Unit)? = null,
        val errorStyle: Int = style
) {
    @StyleRes
    open fun getNewErrorStyle(): Int = R.style.EvotorUITheme_Button_Regular_NewErrorStyle

    class Positive(text: CharSequence, listener: () -> Unit) : ButtonDescription(
            text,
            R.style.EvotorUITheme_Button_Regular_Primary,
            listener
    )

    class Negative(text: CharSequence, listener: () -> Unit) : ButtonDescription(
            text,
            R.style.EvotorUITheme_Button_Regular_Warning,
            listener
    )

    class Neutral(text: CharSequence, listener: () -> Unit) : ButtonDescription(
            text,
            R.style.EvotorUITheme_Button_Regular_Additional,
            listener,
            R.style.EvotorUITheme_Button_Regular_Additional_Alert
    )

    class Dismiss(text: CharSequence, listener: (() -> Unit)? = null) : ButtonDescription(
            text,
            R.style.EvotorUITheme_Button_Regular_Text,
            listener
    ) {
        override fun getNewErrorStyle() = R.style.EvotorUITheme_Button_Regular_Text_NewErrorStyle
    }
}