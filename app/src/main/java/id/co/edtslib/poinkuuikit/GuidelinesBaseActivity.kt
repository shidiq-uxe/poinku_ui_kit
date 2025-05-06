package id.co.edtslib.poinkuuikit

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import id.co.edtslib.uikit.poinku.utils.SystemBarStyle
import id.co.edtslib.uikit.poinku.utils.setLightStatusBar
import id.co.edtslib.uikit.poinku.utils.setSystemBarStyle

open class GuidelinesBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSystemBarStyle(
            statusBarStyle = SystemBarStyle.Dark(Color.WHITE)
        )

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