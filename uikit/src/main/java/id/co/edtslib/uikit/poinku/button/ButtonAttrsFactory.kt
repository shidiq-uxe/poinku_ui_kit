package id.co.edtslib.uikit.poinku.button

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import androidx.annotation.StyleRes
import androidx.core.content.res.use
import com.google.android.material.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.shape.ShapeAppearanceModel
import androidx.core.content.withStyledAttributes

// Todo : Refactor needed ASAP, Since some of MaterialButton Attrs is flagged as private.
object ButtonAttrsFactory {

    @SuppressLint("ResourceType", "PrivateResource")
    fun applyAttributes(
        context: Context,
        button: MaterialButton,
        @StyleRes styleRes: Int,
        attrs: AttributeSet? = null
    ) {
        val themedContext = ContextThemeWrapper(context, styleRes)
        themedContext.withStyledAttributes(styleRes, R.styleable.MaterialButton) {

            with(button) {
                backgroundTintList = getColorStateList(R.styleable.MaterialButton_backgroundTint)
                iconTint = getColorStateList(R.styleable.MaterialButton_iconTint)
                rippleColor = getColorStateList(R.styleable.MaterialButton_rippleColor)
                strokeColor = getColorStateList(R.styleable.MaterialButton_strokeColor)
                strokeWidth =
                    getDimensionPixelSize(R.styleable.MaterialButton_strokeWidth, strokeWidth)
                insetTop =
                    getDimensionPixelSize(R.styleable.MaterialButton_android_insetTop, insetTop)
                insetBottom = getDimensionPixelSize(
                    R.styleable.MaterialButton_android_insetBottom,
                    insetBottom
                )
                // insetLeft = styledAttributes.getDimensionPixelSize(R.styleable.MaterialButton_android_insetLeft, insetLeft)
                // insetRight = styledAttributes.getDimensionPixelSize(R.styleable.MaterialButton_android_insetRight, insetRight)
                iconPadding =
                    getDimensionPixelSize(R.styleable.MaterialButton_iconPadding, iconPadding)
                iconSize = getDimensionPixelSize(R.styleable.MaterialButton_iconSize, iconSize)
                cornerRadius =
                    getDimensionPixelSize(R.styleable.MaterialButton_cornerRadius, cornerRadius)

                val shapeAppearanceResId =
                    getResourceId(R.styleable.MaterialButton_shapeAppearance, 0)
                val shapeAppearanceOverlayResId =
                    getResourceId(R.styleable.MaterialButton_shapeAppearanceOverlay, 0)
                if (shapeAppearanceResId != 0) {
                    shapeAppearanceModel = ShapeAppearanceModel.builder(
                        themedContext,
                        shapeAppearanceResId,
                        shapeAppearanceOverlayResId
                    ).build()
                }
            }

        }

        if (attrs != null) {
            themedContext.obtainStyledAttributes(styleRes, intArrayOf(
                android.R.attr.padding,
                android.R.attr.textAppearance,
                android.R.attr.textColor,
                android.R.attr.minHeight,
            )).use { androidAttributes ->
                androidAttributes.getDimensionPixelSize(0, -1).takeIf { it != -1 }?.let { padding ->
                    button.setPadding(padding, padding, padding, padding)
                }

                // This was not working
                androidAttributes.getDimensionPixelSize(3, -1).takeIf { it != -1 }?.let { minHeight ->
                    button.minHeight = minHeight
                }

                androidAttributes.getResourceId(1, -1).takeIf { it != -1 }?.let { textAppearanceResId ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        button.setTextAppearance(textAppearanceResId)
                    } else {
                        button.setTextAppearance(themedContext, textAppearanceResId)
                    }
                }

                androidAttributes.getColorStateList(2)?.let { textColor ->
                    button.setTextColor(textColor)
                }
            }
        }
    }
}

