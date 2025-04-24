package id.co.edtslib.uikit.poinku.textfield

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import androidx.annotation.StyleRes
import androidx.core.content.res.use
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.drawable
import id.co.edtslib.uikit.textfield.TextField
import androidx.core.content.withStyledAttributes

enum class TextFieldStyle(
    val styleResId: Int,
    val editTextStyleResId: Int = R.style.Widget_EDTS_UIKit_TextInputEditText_LabelInside
) {
    LABEL_INSIDE(R.style.Widget_EDTS_UIKit_TextInputLayout_LabelInside),
    WITHOUT_LABEL(R.style.Widget_EDTS_UIKit_TextInputLayout_WithoutLabel),
    OTP(R.style.Widget_EDTS_UIKit_TextInputLayout_Otp)
}

internal object TextFieldAttrsFactory {
    @SuppressLint("ResourceType", "PrivateResource")
    fun applyAttributes(
        context: Context,
        textField: TextField,
        @StyleRes styleRes: Int,
        @StyleRes editTextStyleResId: Int,
        attrs: AttributeSet? = null
    ) {
        val themedContext = ContextThemeWrapper(context, styleRes)
        themedContext.withStyledAttributes(
            styleRes,
            com.google.android.material.R.styleable.TextInputLayout
        ) {

            with(textField) {
                boxBackgroundColor = getColor(
                    com.google.android.material.R.styleable.TextInputLayout_boxBackgroundColor,
                    boxBackgroundColor
                )
                boxStrokeWidth = getDimensionPixelSize(
                    com.google.android.material.R.styleable.TextInputLayout_boxStrokeWidth,
                    boxStrokeWidth
                )
                boxStrokeWidthFocused = getDimensionPixelSize(
                    com.google.android.material.R.styleable.TextInputLayout_boxStrokeWidthFocused,
                    boxStrokeWidthFocused
                )
                setCounterOverflowTextAppearance(
                    getResourceId(
                        com.google.android.material.R.styleable.TextInputLayout_counterOverflowTextAppearance,
                        R.style.TextAppearance_Rubik_B4_Light
                    )
                )
                counterOverflowTextColor =
                    getColorStateList(com.google.android.material.R.styleable.TextInputLayout_counterOverflowTextColor)
                setCounterTextAppearance(
                    getResourceId(
                        com.google.android.material.R.styleable.TextInputLayout_counterTextAppearance,
                        R.style.TextAppearance_Rubik_B4_Light
                    )
                )
                counterTextColor =
                    getColorStateList(com.google.android.material.R.styleable.TextInputLayout_counterTextColor)
                setEndIconTintList(getColorStateList(com.google.android.material.R.styleable.TextInputLayout_endIconTint))
                errorIconDrawable =
                    getDrawable(com.google.android.material.R.styleable.TextInputLayout_errorIconDrawable)
                setErrorTextAppearance(
                    getResourceId(
                        com.google.android.material.R.styleable.TextInputLayout_errorTextAppearance,
                        R.style.TextAppearance_Rubik_Regular_Error
                    )
                )
                isExpandedHintEnabled = getBoolean(
                    com.google.android.material.R.styleable.TextInputLayout_expandedHintEnabled,
                    isExpandedHintEnabled
                )
                setHelperTextTextAppearance(
                    getResourceId(
                        com.google.android.material.R.styleable.TextInputLayout_helperTextTextAppearance,
                        R.style.TextAppearance_Rubik_B4_Light
                    )
                )
                setHintTextAppearance(
                    getResourceId(
                        com.google.android.material.R.styleable.TextInputLayout_hintTextAppearance,
                        R.style.TextAppearance_Rubik_H3_Heavy
                    )
                )
                hintTextColor =
                    getColorStateList(com.google.android.material.R.styleable.TextInputLayout_hintTextColor)
                // Todo : android:textColorHint
                setPlaceholderTextAppearance(
                    getResourceId(
                        com.google.android.material.R.styleable.TextInputLayout_placeholderTextAppearance,
                        R.style.TextAppearance_Rubik_B2_Light
                    )
                )
                placeholderTextColor =
                    getColorStateList(com.google.android.material.R.styleable.TextInputLayout_placeholderTextColor)
                setPrefixTextAppearance(
                    getResourceId(
                        com.google.android.material.R.styleable.TextInputLayout_prefixTextAppearance,
                        R.style.TextAppearance_Rubik_B2_Light
                    )
                )
                getColorStateList(com.google.android.material.R.styleable.TextInputLayout_prefixTextColor)?.let {
                    setPrefixTextColor(
                        it
                    )
                }
                setStartIconTintList(getColorStateList(com.google.android.material.R.styleable.TextInputLayout_startIconTint))
                setSuffixTextAppearance(
                    getResourceId(
                        com.google.android.material.R.styleable.TextInputLayout_suffixTextAppearance,
                        R.style.TextAppearance_Rubik_B2_Light
                    )
                )
                getColorStateList(com.google.android.material.R.styleable.TextInputLayout_suffixTextColor)?.let {
                    setSuffixTextColor(
                        it
                    )
                }
                boxCollapsedPaddingTop = getDimensionPixelSize(
                    com.google.android.material.R.styleable.TextInputLayout_boxCollapsedPaddingTop,
                    boxCollapsedPaddingTop
                )

                /*val shapeAppearanceResId = styledAttributes.getResourceId(com.google.android.material.R.styleable.MaterialButton_shapeAppearance, 0)
            val shapeAppearanceOverlayResId = styledAttributes.getResourceId(com.google.android.material.R.styleable.MaterialButton_shapeAppearanceOverlay, 0)
            if (shapeAppearanceResId != 0) {
                shapeAppearanceModel = ShapeAppearanceModel.builder(themedContext, shapeAppearanceResId, shapeAppearanceOverlayResId).build()
            }*/
            }

        }

        if (attrs != null) {
            themedContext.obtainStyledAttributes(editTextStyleResId, intArrayOf(
                android.R.attr.background,
                android.R.attr.maxLines,
                android.R.attr.textAppearance
            )).use { editTextStyleAttributes ->
                editTextStyleAttributes.getResourceId(0, -1).takeIf { it != -1 }?.let { backgroundResource ->
                    textField.editText?.background = context.drawable(backgroundResource)
                }

                editTextStyleAttributes.getInteger(1, -1).takeIf { it != -1 }?.let { maxLines ->
                    textField.editText?.maxLines = maxLines
                }

                editTextStyleAttributes.getResourceId(1, -1).takeIf { it != -1 }?.let { textAppearanceResId ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textField.editText?.setTextAppearance(textAppearanceResId)
                    } else {
                        textField.editText?.setTextAppearance(themedContext, textAppearanceResId)
                    }
                }
            }
        }
    }
}
