package id.co.edtslib.uikit.poinku.progressview

import androidx.annotation.DimenRes
import androidx.annotation.StringRes

/**
 * Use to customize your progress drawable or other animated drawable
 */
open class DrawableParams {

    /**
     * String resource to show along with progress/drawable
     */
    @StringRes
    var buttonTextRes: Int? = null

    /**
     * String to show along with progress/drawable
     */
    var buttonText: String? = null

    /**
     * progress/drawable gravity.
     * The default value is on the right of the text
     */
    var gravity: Int = DrawableButton.GRAVITY_TEXT_END

    /**
     * Dimension resource for the margin between text and progress/drawable
     */
    @DimenRes
    var textMarginRes: Int? = null

    /**
     * the margin between text and progress/drawable in pixels
     */
    var textMarginPx: Int = DrawableButton.DEFAULT
}