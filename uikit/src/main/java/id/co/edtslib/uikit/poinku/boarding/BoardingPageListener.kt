package id.co.edtslib.uikit.poinku.boarding

import android.view.View

interface BoardingPageListener {
    fun onPageSelected(position: Int, fakePosition: Int)
    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int, fakePosition: Int)
    fun onRegisterButtonClicked(view: View)
    fun onLoginButtonClicked(view: View)
}