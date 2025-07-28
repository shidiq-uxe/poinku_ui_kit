package id.co.edtslib.poinkuuikit.profile

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.co.edtslib.poinkuuikit.GuidelinesBaseActivity
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.poinkuuikit.databinding.ActivityAnimatedStrokeBinding
import id.co.edtslib.uikit.poinku.coachmark.CoachMarkData
import id.co.edtslib.uikit.poinku.coachmark.CoachMarkOverlay
import id.co.edtslib.uikit.poinku.coachmark.CoachmarkDelegate
import id.co.edtslib.uikit.poinku.utils.viewBinding

class AnimatedStrokeActivity : GuidelinesBaseActivity() {

    private val binding by viewBinding<ActivityAnimatedStrokeBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.WHITE,
                Color.WHITE
            )
        )
        setContentView(R.layout.activity_animated_stroke)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initCoachMark()

        binding.referralEntryPoint2.isGlowing = false
        binding.referralEntryPoint3.onlyGlow = true

        binding.referralEntryPoint.setInfinite(true)
        binding.referralEntryPoint2.setInfinite(true)

    }

    private fun initCoachMark() {
        val coachmarkItems = listOf(
            CoachMarkData(
                target = binding.referralEntryPoint,
                title = "Program Referral",
                description = "Bagikan kode referral untuk mendapatkan hadiah setiap kali ada teman yang bergabung."
            )
        )

        val coachMarkDelegate = object : CoachmarkDelegate {
            override fun onNextClickClickListener(index: Int) {

            }

            override fun onSkipClickListener(index: Int) {

            }

            override fun onDismissListener() {
                binding.referralEntryPoint.setInfinite(false)
            }
        }

        binding.referralEntryPoint.setInfinite(true)

        Handler(Looper.getMainLooper()).postDelayed({
            CoachMarkOverlay.Builder(this)
                .setDismissibleOnBack(true)
                .setCoachMarkItems(coachmarkItems)
                .setCoachMarkDelegate(coachMarkDelegate)
                .build()
        }, 1000L)
    }
}