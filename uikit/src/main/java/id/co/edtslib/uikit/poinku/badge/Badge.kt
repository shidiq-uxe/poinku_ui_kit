package id.co.edtslib.uikit.poinku.badge

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.use
import androidx.core.view.updatePadding
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.ShapeAppearanceModel
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.dp

// Todo : Research More about Material Badge Drawable
class Badge @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(ContextThemeWrapper(context, R.style.Theme_EDTS_UIKit), attrs, defStyleAttr) {

    @ColorInt
    var badgeColor = context.color(R.color.primary_30)
        set(value) {
            field = value
            badgeDrawable.setTint(value)
        }

    @ColorInt
    var textColor = context.color(R.color.white)
        set(value) {
            field = value
            textView.setTextColor(value)
        }

    var text: String? = null
        set(value) {
            field = value
            textView.text = value
            visibility = if (value.isNullOrEmpty()) GONE else VISIBLE
            invalidate()
        }

    @StyleRes
    var badgeTextAppearance = R.style.TextAppearance_Rubik_B3_Medium
        set(value) {
            field = value
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(value)
            }
        }

    internal val badgeShapeModel = ShapeAppearanceModel()
        .toBuilder()
        .setAllCornerSizes(RelativeCornerSize(0.5f))
        .build()

    internal val badgeDrawable = MaterialShapeDrawable(badgeShapeModel).apply {
        setTint(badgeColor)
    }

    private val textView: TextView = TextView(this.context).apply {
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextAppearance(badgeTextAppearance)
        }
        setTextColor(textColor)
    }

    init {
        initAttributes(attrs)
    }

    private fun initAttributes(attrs: AttributeSet?) {
        background = badgeDrawable
        addView(textView)

        this.updatePadding(
            left = 6.dp.toInt(),
            right = 6.dp.toInt(),
        )

        context.obtainStyledAttributes(attrs, R.styleable.Badge).use {
            if(it.hasValue(R.styleable.Badge_text)) {
                text = it.getString(R.styleable.Badge_text)
            }
        }
    }
}