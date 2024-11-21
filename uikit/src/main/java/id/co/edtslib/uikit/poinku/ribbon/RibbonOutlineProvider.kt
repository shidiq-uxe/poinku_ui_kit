package id.co.edtslib.uikit.poinku.ribbon

import android.graphics.Outline
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import id.co.edtslib.uikit.poinku.ribbon.Ribbon

class RibbonOutlineProvider(private val ribbon: Ribbon) : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        val path = if (ribbon.gravity == Ribbon.Gravity.START) {
            ribbon.drawStartContainer()
        } else {
            ribbon.drawEndContainer()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            outline.setPath(path)
        } else {
            outline.setRoundRect(
                0,
                0,
                ribbon.width,
                ribbon.textContainerHeight.toInt(),
                ribbon.cornerRadius
            )
        }
    }
}

