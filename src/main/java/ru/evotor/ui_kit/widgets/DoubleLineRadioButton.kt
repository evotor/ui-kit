package ru.evotor.ui_kit.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.StringRes
import ru.evotor.ui_kit.R
import ru.evotor.ui_kit.databinding.WidgetDoubleLineRadioButtonLayoutBinding
import java.lang.ref.WeakReference

class DoubleLineRadioButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: WidgetDoubleLineRadioButtonLayoutBinding =
        WidgetDoubleLineRadioButtonLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private var listenerRef: WeakReference<OnCheckedChangeListener>? = null

    init {
        val attributes = context.obtainStyledAttributes(
            attrs,
            R.styleable.DoubleLineRadioButton,
            defStyleAttr,
            0
        )
        if (attributes.hasValue(R.styleable.DoubleLineRadioButton_rb_id)) {
            val rbId = attributes.getResourceId(R.styleable.DoubleLineRadioButton_rb_id, 0)
            binding.radioButton.id = rbId
        }
        setTitle(attributes.getString(R.styleable.DoubleLineRadioButton_title))
        setSubtitle(attributes.getString(R.styleable.DoubleLineRadioButton_subtitle))
        if (attributes.hasValue(R.styleable.DoubleLineRadioButton_checked)) {
            binding.radioButton.isChecked =
                attributes.getBoolean(R.styleable.DoubleLineRadioButton_checked, false)
        }
        attributes.recycle()
        initListeners()
    }

    var isChecked: Boolean
        get() = binding.radioButton.isChecked
        set(value) {
            binding.radioButton.isChecked = value
        }

    val radioButton
        get() = binding.radioButton

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        listenerRef = WeakReference(listener)
    }

    fun setTitle(title: String?) {
        binding.title.text = title
    }

    fun setTitle(@StringRes titleResId: Int) {
        binding.title.setText(titleResId)
    }

    fun setSubtitle(subtitle: String?) {
        binding.subtitle.text = subtitle
    }

    fun setSubtitle(@StringRes subtitleResId: Int) {
        binding.subtitle.setText(subtitleResId)
    }

    private fun initListeners() {
        binding.radioButton.setOnCheckedChangeListener { _, b ->
            if (b && parent is RadioGroup) {
                (parent as RadioGroup).check(binding.radioButton.id)
            }
            listenerRef?.get()?.onCheckedChanged(b)
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(checked: Boolean)
    }
}
