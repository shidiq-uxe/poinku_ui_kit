package id.co.edtslib.uikit.poinku.indicator

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import id.co.edtslib.uikit.poinku.R
import androidx.core.content.withStyledAttributes

object IndicatorAttrsFactory {

    fun initAttrs(context: Context, attrs: AttributeSet?, indicatorOptions: IndicatorOptions) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.IndicatorView) {
                val indicatorSlideMode = getInt(R.styleable.IndicatorView_indicator_slide_mode, 4)
                val indicatorStyle = getInt(R.styleable.IndicatorView_indicator_style, 4)
                val checkedColor = getColor(
                    R.styleable.IndicatorView_indicator_checked_color,
                    ContextCompat.getColor(context, R.color.primary_30)
                )
                val normalColor = getColor(
                    R.styleable.IndicatorView_indicator_default_color,
                    ContextCompat.getColor(context, R.color.grey_30)
                )
                val orientation = getInt(R.styleable.IndicatorView_indicator_orientation, 0)

                val defaultSize = context.resources.getDimension(R.dimen.xxs)
                val selectedSize = context.resources.getDimension(R.dimen.m1)

                val defaultWidth =
                    getDimension(R.styleable.IndicatorView_indicator_default_width, defaultSize)
                val selectedWidth =
                    getDimension(R.styleable.IndicatorView_indicator_gap, selectedSize)

                val sliderGap = context.resources.getDimension(R.dimen.xxxs)

                val defaultSliderHeight = context.resources.getDimension(R.dimen.xxs)
                val sliderHeight = getDimension(
                    R.styleable.IndicatorView_indicator_slider_height,
                    defaultSliderHeight
                )

                indicatorOptions.apply {
                    this.checkedSliderColor = checkedColor
                    this.normalSliderColor = normalColor
                    this.orientation = orientation
                    this.indicatorStyle = indicatorStyle
                    this.slideMode = indicatorSlideMode
                    this.normalSliderWidth = defaultWidth
                    this.checkedSliderWidth = selectedWidth
                    this.sliderGap = sliderGap
                    this.sliderHeight = sliderHeight
                }
            }
        }
    }
}
