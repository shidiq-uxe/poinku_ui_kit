package id.co.edtslib.poinkuuikit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.co.edtslib.uikit.poinku.utils.setLightStatusBar

open class GuidelinesBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightStatusBar()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.customView?.transitionName = "shared_title_<id>"

        postponeEnterTransition()
        startPostponedEnterTransition()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()

        return true
    }
}