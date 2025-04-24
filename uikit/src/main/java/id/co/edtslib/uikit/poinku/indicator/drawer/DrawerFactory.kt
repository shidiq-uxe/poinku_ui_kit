package id.co.edtslib.uikit.poinku.indicator.drawer

import id.co.edtslib.uikit.poinku.indicator.IndicatorOptions
import id.co.edtslib.uikit.poinku.indicator.IndicatorStyle

internal object DrawerFactory {
    fun createDrawer(indicatorOptions: IndicatorOptions): DrawerInterface {
        return when (indicatorOptions.indicatorStyle) {
            IndicatorStyle.DASH -> DashDrawer(indicatorOptions)
            IndicatorStyle.ROUND_RECT -> RoundRectDrawer(indicatorOptions)
            else -> CircleDrawer(indicatorOptions)
        }
    }
}