package ru.evotor.ui_kit.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import ru.evotor.ui_kit.databinding.WidgetPinpadLayoutBinding

class Pinpad @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var externalKeyListener: OnKeyListener? = null

    private val binding: WidgetPinpadLayoutBinding =
            WidgetPinpadLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private val mainButtonsKeycodes by lazy { getMainButtonsKeyCodes() }
    private val mainButtons by lazy { getMainButtonsList() }

    private val audioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager? }

    init {
        initListeners()
    }

    override fun setOnKeyListener(l: OnKeyListener?) {
        super.setOnKeyListener(l)
        externalKeyListener = l
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        mainButtons.forEach {
            it.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 1f)
                }
                false
            }
            it.setOnClickListener { v -> onClick(v) }
        }
    }

    private fun onClick(v: View) {
        val pressedKeyCode = mainButtonsKeycodes.get(v.id, KeyEvent.KEYCODE_UNKNOWN)
        externalKeyListener?.onKey(v, pressedKeyCode, KeyEvent(KeyEvent.ACTION_DOWN, pressedKeyCode))
    }

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
            binding.button9
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
    }
}