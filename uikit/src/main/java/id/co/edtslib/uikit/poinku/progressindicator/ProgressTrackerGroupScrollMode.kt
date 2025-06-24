package id.co.edtslib.uikit.poinku.progressindicator

import androidx.annotation.IntDef
import id.co.edtslib.uikit.poinku.progressindicator.ProgressTrackerGroup.Companion.MODE_AUTO
import id.co.edtslib.uikit.poinku.progressindicator.ProgressTrackerGroup.Companion.MODE_FIXED
import id.co.edtslib.uikit.poinku.progressindicator.ProgressTrackerGroup.Companion.MODE_SCROLLABLE

@Retention(AnnotationRetention.SOURCE)
@IntDef(MODE_SCROLLABLE, MODE_FIXED, MODE_AUTO)
annotation class ProgressTrackerGroupScrollMode()
