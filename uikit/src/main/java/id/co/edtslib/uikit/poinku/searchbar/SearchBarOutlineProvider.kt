package id.co.edtslib.uikit.poinku.searchbar

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import id.co.edtslib.uikit.poinku.utils.dp

class SearchBarOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View?, outline: Outline?) {
        outline?.setRoundRect(
            0,
            0,
            view?.width ?: 0,
            view?.height ?: 0,
            8.dp
        )
    }
}