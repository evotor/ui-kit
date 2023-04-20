package ru.evotor.ui_kit.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gone
import ru.evotor.ui_kit.R
import ru.evotor.ui_kit.databinding.DialogFragmentContentLayoutBinding
import ru.evotor.ui_kit.dialogs.base.StackTraceUtils
import visible

class RecyclerViewDialogFragment : DialogFragment() {

    var adapter: RecyclerView.Adapter<*>? = null

    private lateinit var binding: DialogFragmentContentLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogFragmentContentLayoutBinding.bind(inflater.inflate(R.layout.dialog_fragment_content_layout, container))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val icon = arguments?.getIcon()
        val title = arguments?.getTitle()
        icon?.let {
            binding.toolbar.setNavigationIcon(it)
        }
        title?.let {
            binding.toolbar.title = String.format(it, arguments?.getTitleArgs())
        }
        binding.toolbar.isVisible = icon != null || title != null
        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }
        setupRecyclerView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.EvotorUITheme_Dialog_FullScreen)
    }

    fun setTitle(title: String): RecyclerViewDialogFragment {
        arguments?.putString(TITLE_KEY, title)
        return this
    }

    fun setTitle(
        @StringRes titleRes: Int,
        vararg titleArgs: Any
    ): RecyclerViewDialogFragment {
        arguments?.putInt(TITLE_RES_KEY, titleRes)
        arguments?.putStringArray(
            TITLE_ARGS_KEY,
            titleArgs.map { it.toString() }.toTypedArray()
        )
        return this
    }

    fun setNavigationIcon(@DrawableRes iconRes: Int): RecyclerViewDialogFragment {
        arguments?.putInt(ICON_RES_KEY, iconRes)
        return this
    }

    fun show(fragmentManager: FragmentManager): RecyclerViewDialogFragment {
        try {
            fragmentManager.beginTransaction().add(this, TAG).commitNowAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            fragmentManager.beginTransaction().add(this, TAG).commitAllowingStateLoss()
        }
        BaseBottomSheetDialogFragment.dialogShowListener?.onShow(
            dialogFragment = this,
            where = StackTraceUtils.getStackTrace(),
            title = arguments?.getTitle() ?: "[NO_TITLE]",
            titleArgs = arguments?.getTitleArgs() ?: emptyArray(),
            message = "[NO_MESSAGE]",
            messageArgs = emptyArray()
        )
        return this
    }

    private fun setupRecyclerView() {
        adapter?.let {
            binding.dialogList.layoutManager = LinearLayoutManager(context)
            binding.dialogList.visible()
            binding.dialogList.setHasFixedSize(true)
            binding.dialogList.adapter = it
        } ?: binding.dialogList.gone()
    }

    private fun Bundle.getTitle(): String? {
        val titleRes = getInt(TITLE_RES_KEY, 0)
        return if (titleRes == 0) {
            getString(TITLE_KEY, null)
        } else {
            context?.getString(titleRes)
        }
    }

    private fun Bundle.getTitleArgs(): Array<String>? {
        if (!containsKey(TITLE_ARGS_KEY)) return null
        return getStringArray(TITLE_ARGS_KEY)
    }

    private fun Bundle.getIcon(): Int? = getInt(ICON_RES_KEY).let {
        if (it == 0) null else it
    }

    companion object {

        private const val TAG = "ru.evotor.ui_kit.dialogs.content_dialog_fragment.tag"
        private const val TITLE_KEY = "ru.evotor.ui_kit.dialogs.content_dialog_fragment.title_key"
        private const val TITLE_RES_KEY = "ru.evotor.ui_kit.dialogs.content_dialog_fragment.title_res_key"
        private const val TITLE_ARGS_KEY = "ru.evotor.ui_kit.dialogs.content_dialog_fragment.title_args_key"
        private const val ICON_RES_KEY = "ru.evotor.ui_kit.dialogs.content_dialog_fragment.icon_res_key"

        @JvmStatic
        fun newInstance(): RecyclerViewDialogFragment {
            return RecyclerViewDialogFragment()
                    .apply {
                        isCancelable = false
                        arguments = Bundle()
                    }
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
    }
}