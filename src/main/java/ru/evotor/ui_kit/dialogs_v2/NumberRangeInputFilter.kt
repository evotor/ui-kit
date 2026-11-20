package ru.evotor.ui_kit.dialogs_v2

import android.text.InputFilter
import android.text.Spanned
import java.math.BigDecimal

internal class NumberRangeInputFilter(
    private val min: BigDecimal,
    private val max: BigDecimal
) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val newValue = dest.subSequence(0, dstart).toString() +
            source.subSequence(start, end) +
            dest.subSequence(dend, dest.length)
        val digits = newValue.replace(',', '.').trimEnd('.')
        if (digits.isEmpty()) {
            return null
        }
        val value = digits.toBigDecimalOrNull() ?: return ""
        return if (value !in min..max) "" else null
    }
}
