package id.co.edtslib.uikit.poinku.indicator

import androidx.annotation.IntDef
import id.co.edtslib.uikit.poinku.indicator.IndicatorSlideMode.Companion.NORMAL
import id.co.edtslib.uikit.poinku.indicator.IndicatorSlideMode.Companion.SMOOTH
import id.co.edtslib.uikit.poinku.indicator.IndicatorSlideMode.Companion.WORM
import id.co.edtslib.uikit.poinku.indicator.IndicatorSlideMode.Companion.COLOR
import id.co.edtslib.uikit.poinku.indicator.IndicatorSlideMode.Companion.SCALE

@IntDef(NORMAL, SMOOTH, WORM, COLOR, SCALE)
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
annotation class IndicatorSlideModeAnnotation