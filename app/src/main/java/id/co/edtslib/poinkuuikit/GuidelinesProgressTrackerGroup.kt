package id.co.edtslib.poinkuuikit

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.co.edtslib.poinkuuikit.databinding.ActivityGuidelinesProgressTrackerGroupBinding
import id.co.edtslib.uikit.poinku.badge.Badge
import id.co.edtslib.uikit.poinku.progressindicator.ProgressTrackerGroup
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.setLightStatusBar
import id.co.edtslib.uikit.poinku.utils.viewBinding
import id.co.edtslib.uikit.poinku.R as UIKitR

class GuidelinesProgressTrackerGroup : GuidelinesBaseActivity() {

    private val binding by viewBinding<ActivityGuidelinesProgressTrackerGroupBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightStatusBar()
        setContentView(R.layout.activity_guidelines_progress_tracker_group)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createProgressTracker(this)
    }

    // Updated implementation with better debugging and visibility
    fun createProgressTracker(context: Context) {
        val progressGroup = binding.progressTrackerGroup

        /*repeat(3) { index ->
            val tracker = progressGroup.createAndAddProgressTracker()
            val stepView = createStepView(context, completed = false) // Your step view (circle, text, etc.)
            tracker.addStep(stepView, showConnector = index < 2) // No connector for last item
        }*/

        // Create step views with proper dimensions and styling
        val step1 = createStepView(context, "Step 3", "1", true)
        val step2 = createStepView(context, "Step 2", "2", true)
        val step3 = createStepView(context, "Step 3", "3", false)

        progressGroup.addStep(step1, true)
        progressGroup.addStep(step2, true)
        progressGroup.addStep(step3, false)

        Handler(Looper.getMainLooper()).postDelayed({
            progressGroup.selectStep(1)
        }, 1000)
    }

    private fun createStepView(
        context: Context,
        text: String,
        badgeText: String,
        completed: Boolean
    ): View {
        val linearLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val color = context.color(if (completed) UIKitR.color.primary_30 else UIKitR.color.grey_40)

        val badge = Badge(context).apply {
            this.text = badgeText
            this.badgeColor = color
        }

        val textView = TextView(context).apply {
            this.text = text
            this.setTextColor(color)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8.dp.toInt(), 0, 0, 0)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextAppearance(UIKitR.style.TextAppearance_Rubik_B4_Medium)
            }
        }

        linearLayout.addView(badge)
        linearLayout.addView(textView)
        return linearLayout
    }
}