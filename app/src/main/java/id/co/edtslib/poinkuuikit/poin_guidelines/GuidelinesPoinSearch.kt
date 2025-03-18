package id.co.edtslib.poinkuuikit.poin_guidelines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.co.edtslib.poinkuuikit.GuidelinesBaseActivity
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.poinkuuikit.databinding.ActivityGuidelinesPoinSearchBinding
import id.co.edtslib.uikit.poinku.utils.showKeyboard
import id.co.edtslib.uikit.poinku.utils.viewBinding
import id.co.edtslib.uikit.poinku.R as UIKitR

class GuidelinesPoinSearch : GuidelinesBaseActivity() {

    private val binding by viewBinding<ActivityGuidelinesPoinSearchBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_guidelines_poin_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    override fun onBackPressed() {
        /*val motionLayout = binding.animatedToolbar.root

        if (motionLayout.currentState == motionLayout.startState) {
            super.onBackPressed()
        } else {
            motionLayout.transitionToStart()
        }*/

        super.onBackPressed()
    }


}