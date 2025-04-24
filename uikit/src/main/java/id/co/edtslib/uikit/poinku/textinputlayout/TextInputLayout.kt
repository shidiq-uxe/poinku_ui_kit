package id.co.edtslib.uikit.poinku.textinputlayout

import android.content.Context
import android.content.res.ColorStateList
import android.text.TextUtils
import android.util.AttributeSet
import androidx.core.view.doOnLayout
import com.google.android.material.textfield.TextInputLayout
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.colorAttr
import id.co.edtslib.uikit.poinku.utils.colorStateList
import id.co.edtslib.uikit.poinku.utils.drawable
import id.co.edtslib.uikit.poinku.utils.getProgressIndicatorDrawable
import id.co.edtslib.uikit.poinku.utils.hapticfeedback.HapticFeedback
import id.co.edtslib.uikit.poinku.utils.vibrateAnimation

/**
 * A custom [TextInputLayout] that provides additional functionality for displaying
 * loading, error, and success states.
 *
 * @param context The context to use.
 * @param attrs The attributes of the XML tag that is inflating the view.
 * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource
 * that supplies default values for the view. Can be 0 to not look for defaults.
 */
open class TextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = R.attr.textFieldStyle
) : TextInputLayout(context, attrs, defStyleAttr) {

    companion object {
        internal fun getDefStyle(context: Context, defStyleAttr: Int): Int {
            return if (defStyleAttr == 0) R.attr.textFieldStyle else defStyleAttr
        }
    }

    private val originalEndIconDrawable = endIconDrawable

    /**
     * The current state of the [TextInputLayout].
     */
    private var state: State = State.DEFAULT

    /**
     * Whether the [TextInputLayout] is in a loading state.
     */
    var isLoading: Boolean = false
        get() = state == State.LOADING
        set(value) {
            field = value
            setState(if (value) State.LOADING else State.DEFAULT)
        }

    /**
     * The success message to display in the [TextInputLayout].
     */
    var success: CharSequence? = null
        get() = helperText
        set(value) {
            field = value
            setState(if (value != null) State.SUCCESS else State.DEFAULT, value)
        }

    /**
     * Whether the [TextInputLayout] is in a success state.
     */
    var isSuccess: Boolean = false
        get() = state == State.SUCCESS
        set(value) {
            field = value
            setState(if (value) State.SUCCESS else State.DEFAULT)
        }

    var shouldChangeTextColor = false

    var isError: Boolean = false
        get() = state == State.ERROR_BG_ONLY && error != null
        set(value) {
            field = value
            setState(if (value) State.ERROR_BG_ONLY else State.DEFAULT)

            val textColor = context.color(
                if (value) R.color.red_30 else R.color.grey_80
            )

            if (shouldChangeTextColor) {
                editText?.setTextColor(textColor)
            }

            if (this@TextInputLayout.isErrorEnabled) {
                this@TextInputLayout.vibrateAnimation(
                    HapticFeedback.ERROR.duration
                )
            }
        }

    init {
        this.doOnLayout {
            setHintTextAppearance(R.style.TextAppearance_Rubik_H3_Heavy)
            hintTextColor = context.colorStateList(R.color.grey_60)
        }
    }

    /**
     * Sets the state of the [TextInputLayout] and updates its appearance accordingly.
     *
     * @param state The new state to set.
     * @param helperText#EE2B2E The helper text to display, if applicable.
     */
    private fun setState(state: State, helperText: CharSequence? = null) {
        this.state = state
        when (state) {
            State.DEFAULT -> {
                isErrorEnabled = false
                isHelperTextEnabled = false
                if (endIconDrawable == context.getProgressIndicatorDrawable()) {
                    if (originalEndIconDrawable != null) {
                        endIconDrawable = originalEndIconDrawable
                    } else {
                        endIconMode = END_ICON_NONE
                    }
                }
                if (isCounterEnabled && (editText?.maxLines ?: 0) > 1) {
                    background = context.drawable(R.drawable.slr_text_field_background)
                } else {
                    editText?.background = context.drawable(R.drawable.slr_text_field_background)
                }
            }

            State.SUCCESS -> {
                isErrorEnabled = false
                setHelperText(helperText)
                setHelperTextColor(ColorStateList.valueOf(context.colorAttr(R.attr.colorSuccess)))
                endIconMode = END_ICON_NONE
                if (isCounterEnabled && (editText?.maxLines ?: 0) > 1) {
                    background = context.drawable(R.drawable.bg_text_field_success)
                } else {
                    editText?.background = context.drawable(R.drawable.bg_text_field_success)
                }
            }

            State.LOADING -> {
                isErrorEnabled = false
                isHelperTextEnabled = false
                endIconMode = END_ICON_CUSTOM
                endIconDrawable = context.getProgressIndicatorDrawable()
                if (isCounterEnabled && (editText?.maxLines ?: 0) > 1) {
                    background = context.drawable(R.drawable.slr_text_field_background)
                } else {
                    editText?.background = context.drawable(R.drawable.slr_text_field_background)
                }
            }

            State.ERROR_BG_ONLY -> {
                if (isCounterEnabled && (editText?.maxLines ?: 0) > 1) {
                    background = context.drawable(R.drawable.bg_text_field_error)
                } else {
                    editText?.background = context.drawable(R.drawable.bg_text_field_error)
                }
            }
        }
    }

    /**
     * Sets the error message to display in the [TextInputLayout].
     *
     * @param errorText The error message to display.
     */
    override fun setError(errorText: CharSequence?) {
        super.setError(errorText)
        if (!TextUtils.isEmpty(errorText)) {
            if (isCounterEnabled && (editText?.maxLines ?: 0) > 1) {
                background = context.drawable(R.drawable.bg_text_field_error)
            } else {
                editText?.background = context.drawable(R.drawable.bg_text_field_error)
            }
        } else {
            isError = false
        }
    }

    /**
     * The possible states of the [TextInputLayout].
     */
    private enum class State {
        DEFAULT, LOADING, SUCCESS, ERROR_BG_ONLY
    }
}