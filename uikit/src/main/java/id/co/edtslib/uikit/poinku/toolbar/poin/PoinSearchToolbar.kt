package id.co.edtslib.uikit.poinku.toolbar.poin

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.doOnLayout
import androidx.fragment.app.FragmentActivity
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.databinding.ViewSearchToolbarBinding
import id.co.edtslib.uikit.poinku.utils.hideKeyboard
import id.co.edtslib.uikit.poinku.utils.inflater
import id.co.edtslib.uikit.poinku.utils.showKeyboard
import id.co.edtslib.uikit.searchbar.SearchBarDelegate

class PoinSearchToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSearchToolbarBinding.inflate(context.inflater, this, true)
    var delegate: PoinSearchToolbarDelegate? = null

    private val motionLayout = binding.root

    val titleTextView = binding.tvTitle
    val searchBar = binding.sbSearch
    var shouldOverrideBack = false

    var titleTextAppearance = R.style.TextAppearance_Rubik_H1_Heavy
        set(value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                titleTextView.setTextAppearance(value)
            } else {
                titleTextView.setTextAppearance(context, value)
            }
        }

    init {
        initAttrs(attrs, defStyleAttr)
        setTransitionListener()
        setOnNavigationClickListener()
        bindSearchbarDelegate()
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            context.theme.obtainStyledAttributes(attrs, R.styleable.PoinSearchToolbar, defStyleAttr, 0).apply {
                titleTextAppearance = getResourceId(R.styleable.PoinSearchToolbar_titleTextAppearance, titleTextAppearance)
            }
        }
    }

    private fun bindSearchbarDelegate() {
        binding.sbSearch.searchBarDelegate = object : SearchBarDelegate {
            override fun onCloseIconClicked(view: View) {
                binding.sbSearch.editText.setText(null)
            }

            override fun onFocusChange(view: View, hasFocus: Boolean) {}

            override fun onTextChange(text: String) {
                delegate?.onSearchBarType(text)
            }
        }
    }

    private fun setTransitionListener() {
        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {}

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {}

            override fun onTransitionCompleted(
                motionLayout: MotionLayout?,
                currentId: Int
            ) {
                if (currentId == R.id.end) {
                    binding.sbSearch.editText.apply {
                        requestFocus()
                        showKeyboard()
                    }
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {}
        })
    }

    private fun setOnNavigationClickListener() {
        binding.ivBack.setOnClickListener {
            delegate?.onBackNavigationClick(it)

            if (shouldOverrideBack) return@setOnClickListener

            val fragmentActivity = context as? FragmentActivity ?: (context as? ContextWrapper)?.baseContext as? FragmentActivity

            if (fragmentActivity != null) {
                if (motionLayout.currentState == motionLayout.startState) {
                    fragmentActivity.onBackPressedDispatcher.onBackPressed()
                } else {
                    motionLayout.transitionToStart()
                    binding.sbSearch.editText.hideKeyboard()
                }
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return  if (shouldOverrideBack) return super.dispatchKeyEvent(event)
        else {
            if (event?.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                if (motionLayout.currentState == motionLayout.startState) {
                    return super.dispatchKeyEvent(event)
                } else {
                    motionLayout.transitionToStart()
                    binding.sbSearch.editText.hideKeyboard()
                    return true
                }
            }
            return super.dispatchKeyEvent(event)
        }
    }

}