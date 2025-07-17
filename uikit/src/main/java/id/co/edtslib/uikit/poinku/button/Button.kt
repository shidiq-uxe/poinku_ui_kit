package id.co.edtslib.uikit.poinku.button

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.res.use
import androidx.core.view.doOnLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.button.MaterialButton
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.attachShimmerEffect
import id.co.edtslib.uikit.poinku.utils.detachShimmerEffect

class Button @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = R.attr.buttonStyle,
) : MaterialButton(context, attrs, defStyleAttr) {

    // Only call this variable only when shouldShowShimmer is true
    var shimmerFrameLayout: ShimmerFrameLayout? = null

    var shouldShowShimmer = false
        set(value) {
            field = value

            doOnLayout {
                if (value) {
                    shimmerFrameLayout = this.attachShimmerEffect()
                } else {
                    shimmerFrameLayout?.detachShimmerEffect()
                }
            }
        }

    private var _buttonType: ButtonType = ButtonType.FILLED

    var buttonType: ButtonType
        get() = _buttonType
        set(value) {
            _buttonType = value
            // applyStyle(value)
        }


    var pressedScale = DEFAULT_SCALE_VALUE

    init {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.Button, defStyleAttr, 0).use {
                _buttonType = ButtonType.values()[it.getInt(R.styleable.Button_buttonType, buttonType.ordinal)]
                shouldShowShimmer = it.getBoolean(R.styleable.Button_shouldShowShimmer, shouldShowShimmer)
                pressedScale = it.getFloat(R.styleable.Button_pressedScale, pressedScale)
            }

            applyStyle(_buttonType)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { motionEvent ->
            if (isEnabled && isClickable) {
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        this.animate().scaleX(pressedScale).scaleY(pressedScale).setDuration(100).start()
                    }
                    MotionEvent.ACTION_UP -> {
                        this.animate().scaleX(RESET_SCALE_VALUE).scaleY(RESET_SCALE_VALUE).setDuration(100).start()
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        this.animate().scaleX(RESET_SCALE_VALUE).scaleY(RESET_SCALE_VALUE).setDuration(100).start()
                    }

                    else -> {}
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onDetachedFromWindow() {
        shimmerFrameLayout = null

        super.onDetachedFromWindow()
    }

    private fun applyStyle(buttonType: ButtonType) {
        applyButtonSize(buttonType)

        ButtonAttrsFactory.applyAttributes(
            context = context,
            button = this,
            styleRes = buttonType.styleRes,
            attrs = attrs
        )
    }

    // Fallback since applyStyle won't apply minHeight attributes
    private fun applyButtonSize(buttonType: ButtonType) {
        when(buttonType) {
            ButtonType.FILLED, ButtonType.OUTLINED, ButtonType.TEXT -> {
                this@Button.minHeight = resources.getDimensionPixelSize(R.dimen.dimen_28)
            }
            ButtonType.FILLED_MEDIUM, ButtonType.OUTLINED_MEDIUM, ButtonType.TEXT_MEDIUM -> {
                this@Button.minHeight = resources.getDimensionPixelSize(R.dimen.l)
            }
            ButtonType.FILLED_LARGE, ButtonType.OUTLINED_LARGE, ButtonType.TEXT_LARGE -> {
                this@Button.minHeight = resources.getDimensionPixelSize(R.dimen.xl)
            }
        }
    }

    enum class ButtonType(val styleRes: Int) {
        FILLED(R.style.Widget_EDTS_UIKit_Poinku_Button_Filled),
        FILLED_MEDIUM(R.style.Widget_EDTS_UIKit_Poinku_Button_Filled_Medium),
        FILLED_LARGE(R.style.Widget_EDTS_UIKit_Poinku_Button_Filled_Large),
        OUTLINED(R.style.Widget_EDTS_UIKit_Poinku_Button_Outlined),
        OUTLINED_MEDIUM(R.style.Widget_EDTS_UIKit_Poinku_Button_Outlined_Medium),
        OUTLINED_LARGE(R.style.Widget_EDTS_UIKit_Poinku_Button_Outlined_Large),
        TEXT(R.style.Widget_EDTS_UIKit_Poinku_Button_TextButton),
        TEXT_MEDIUM(R.style.Widget_EDTS_UIKit_Poinku_Button_TextButton_Medium),
        TEXT_LARGE(R.style.Widget_EDTS_UIKit_Poinku_Button_TextButton_Large),
    }

    companion object {
        private const val DEFAULT_SCALE_VALUE = 0.95f
        private const val RESET_SCALE_VALUE = 1f
    }
}