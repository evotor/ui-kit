package ru.evotor.ui_kit.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.media.AudioManager
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.util.getOrElse
import ru.evotor.ui_kit.R
import ru.evotor.ui_kit.databinding.WidgetKeypadLayoutBinding

class Keypad @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var externalKeyListener: OnKeyListener? = null

    private val binding: WidgetKeypadLayoutBinding =
            WidgetKeypadLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private val additionalButtonsKeycodes = SparseIntArray()

    private val mainButtonsKeycodes by lazy { getMainButtonsKeyCodes() }
    private val additionalButtons by lazy { getAdditionalButtonsList() }
    private val mainButtons by lazy { getMainButtonsList() }

    private var additionalButtonsCount: Int

    private val audioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager? }

    init {
        val attributes = context.obtainStyledAttributes(
                attrs,
                R.styleable.Keypad,
                defStyleAttr,
                0
        )
        additionalButtonsCount = attributes.getInt(R.styleable.Keypad_additionalButtonsCount, 0)
        initAdditionalButtonsCount(additionalButtonsCount)
        val weightsString = attributes.getString(R.styleable.Keypad_additionalButtonsWeights)
        weightsString?.trim()?.split(",")?.map { it.toFloat() }?.let {
            setAdditionalButtonsWeights(it)
        }
        initAdditionalButtonsTitles(attributes)
        setCommaButtonVisibility(attributes.getBoolean(R.styleable.Keypad_commaButtonVisible, true))
        attributes.recycle()
        initListeners()
    }

    /**
     * Устанавливает количество дополнительных функциональных кнопок
     *
     * @param count : Количество, должно быть от 0 до 6
     */
    fun setAdditionalButtonsCount(count: Int) {
        additionalButtonsCount = count
        initAdditionalButtonsCount(additionalButtonsCount)
    }

    /**
     * Устанавливает веса по вертикали дополнительных функциональных кнопок
     *
     * @param weightsList : Список весов, должен быть размера равного additionalButtonsCount
     */
    fun setAdditionalButtonsWeights(weightsList: List<Float>) {
        if (weightsList.size != additionalButtonsCount) {
            throw IllegalArgumentException("weights.size must be exactly same as additionalButtonsCount")
        }
        for (i in weightsList.indices) {
            (additionalButtons[i].layoutParams as ConstraintLayout.LayoutParams).verticalWeight = weightsList[i]
        }
    }

    /**
     * Возвращает дополнительную функциональную кнопку по ее индексу
     *
     * @param index : Индекс, должен быть от 0 до additionalButtonsCount
     */
    fun getAdditionalButton(index: Int): Button {
        if (index !in 0..additionalButtonsCount) {
            throw IllegalArgumentException("Index must be in range 0..additionalButtonsCount")
        }
        return additionalButtons[index]
    }

    /**
     * Устанавливает Keycode для дополнительной функциональной кнопки
     *
     * @param index : индекс кнопки, должен быть от 0 до additionalButtonsCount
     * @param keycode : Keycode
     */
    fun setAdditionalButtonKeycode(index: Int, keycode: Int) {
        if (index !in 0..additionalButtonsCount) {
            throw IllegalArgumentException("Index must be in range 0..additionalButtonsCount")
        }
        additionalButtonsKeycodes.put(additionalButtons[index].id, keycode)
    }

    /**
     * Устанавливает Keycode'ы для дополнительных функциональных кнопок
     *
     * @param indexAndKeycodePairList : Список пар (индекс - keycode), индекс кнопки должен быть от 0 до additionalButtonsCount
     */
    fun setAdditionalButtonKeycodes(indexAndKeycodePairList: List<Pair<Int, Int>>) {
        indexAndKeycodePairList.forEach {
            setAdditionalButtonKeycode(it.first, it.second)
        }
    }

    /**
     * Устанавливает видимость кнопки с точкой
     *
     * @param visible : состояние
     */
    fun setCommaButtonVisibility(visible: Boolean) {
        binding.buttonDot.visibility = if (visible) VISIBLE else INVISIBLE
    }

    override fun setOnKeyListener(l: OnKeyListener?) {
        super.setOnKeyListener(l)
        externalKeyListener = l
    }

    private fun initAdditionalButtonsCount(buttonsCount: Int) {
        if (buttonsCount !in 0..6) {
            throw IllegalArgumentException("Additional buttons count must be in range 0..6")
        }
        for (i in additionalButtons.indices) {
            additionalButtons[i].visibility = if (i < buttonsCount) View.VISIBLE else View.GONE
        }
    }

    private fun onClick(v: View) {
        val pressedKeyCode = mainButtonsKeycodes.getOrElse(v.id) {
            additionalButtonsKeycodes.get(v.id, KeyEvent.KEYCODE_UNKNOWN)
        }
        externalKeyListener?.onKey(v, pressedKeyCode, KeyEvent(KeyEvent.ACTION_DOWN, pressedKeyCode))
    }

    private fun initAdditionalButtonsTitles(attr: TypedArray){
        attr.getString(R.styleable.Keypad_additionalButton_0_text)?.let {
            binding.additionalButton0.text = it
        }
        attr.getString(R.styleable.Keypad_additionalButton_1_text)?.let {
            binding.additionalButton1.text = it
        }
        attr.getString(R.styleable.Keypad_additionalButton_2_text)?.let {
            binding.additionalButton2.text = it
        }
        attr.getString(R.styleable.Keypad_additionalButton_3_text)?.let {
            binding.additionalButton3.text = it
        }
        attr.getString(R.styleable.Keypad_additionalButton_4_text)?.let {
            binding.additionalButton4.text = it
        }
        attr.getString(R.styleable.Keypad_additionalButton_5_text)?.let {
            binding.additionalButton5.text = it
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        binding.buttonBackspace.setOnLongClickListener {
            val pressedKeyCode = KeyEvent.KEYCODE_CLEAR
            if (externalKeyListener != null) {
                externalKeyListener?.onKey(it, pressedKeyCode, KeyEvent(KeyEvent.ACTION_DOWN, pressedKeyCode))
            }
            true
        }
        (mainButtons + additionalButtons).forEach {
            it.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 1f)
                }
                false
            }
            it.setOnClickListener { v -> onClick(v) }
        }
    }

    private fun getAdditionalButtonsList() = listOf(
            binding.additionalButton0,
            binding.additionalButton1,
            binding.additionalButton2,
            binding.additionalButton3,
            binding.additionalButton4,
            binding.additionalButton5
    )

    private fun getMainButtonsList() = listOf(
            binding.button0,
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9,
            binding.buttonDot,
            binding.buttonBackspace,
    )

    private fun getMainButtonsKeyCodes() = SparseIntArray().apply {
        put(binding.button0.id, KeyEvent.KEYCODE_0)
        put(binding.button1.id, KeyEvent.KEYCODE_1)
        put(binding.button2.id, KeyEvent.KEYCODE_2)
        put(binding.button3.id, KeyEvent.KEYCODE_3)
        put(binding.button4.id, KeyEvent.KEYCODE_4)
        put(binding.button5.id, KeyEvent.KEYCODE_5)
        put(binding.button6.id, KeyEvent.KEYCODE_6)
        put(binding.button7.id, KeyEvent.KEYCODE_7)
        put(binding.button8.id, KeyEvent.KEYCODE_8)
        put(binding.button9.id, KeyEvent.KEYCODE_9)
        put(binding.buttonDot.id, KeyEvent.KEYCODE_PERIOD)
        put(binding.buttonBackspace.id, KeyEvent.KEYCODE_DEL)
    }
}