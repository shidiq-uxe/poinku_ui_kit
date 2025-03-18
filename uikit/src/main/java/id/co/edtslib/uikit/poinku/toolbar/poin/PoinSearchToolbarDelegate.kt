package id.co.edtslib.uikit.poinku.toolbar.poin

import android.view.View

interface PoinSearchToolbarDelegate {
    fun onBackNavigationClick(view: View)
    fun onSearchBarType(text: String)
}