package id.co.edtslib.poinkuuikit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.co.edtslib.poinkuuikit.auth.RegisterActivity
import id.co.edtslib.poinkuuikit.boarding.OnBoardingActivity
import id.co.edtslib.poinkuuikit.databinding.ActivityMainBinding
import id.co.edtslib.poinkuuikit.databinding.ItemGuidelinesParentBinding
import id.co.edtslib.poinkuuikit.ikupon_guidelines.GuidelinesHomeCouponActivity
import id.co.edtslib.poinkuuikit.ikupon_guidelines.GuidelinesIkuponActivity
import id.co.edtslib.poinkuuikit.poin_guidelines.GuidelinesPoinSearch
import id.co.edtslib.poinkuuikit.profile.AnimatedStrokeActivity
import id.co.edtslib.poinkuuikit.stamp_exchange_guidelines.GuidelinesBucketStampActivitty
import id.co.edtslib.uikit.poinku.adapter.SingleItemViewTypeAdapter
import id.co.edtslib.uikit.poinku.adapter.singleItemViewTypeAdapter
import id.co.edtslib.uikit.poinku.utils.setLightStatusBar
import id.co.edtslib.uikit.poinku.utils.viewBinding

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityMainBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setLightStatusBar()

        bindItemMenu()
    }

    private val ItemGuidelinesParentBinding.sharedElementOptions get() = ActivityOptionsCompat.makeSceneTransitionAnimation(
        this@MainActivity,
        this.tvDesignComponentTitle,
        ViewCompat.getTransitionName(this.tvDesignComponentTitle).orEmpty()
    ).toBundle()

    private fun bindItemMenu() {
        val items = resources.getStringArray(R.array.array_guidelines_title).toList()

        binding.rvItemMenu.adapter = singleItemViewTypeAdapter<String, ItemGuidelinesParentBinding>(
            diff = SingleItemViewTypeAdapter.Diff(
                areItemsTheSame = { old, new -> old == new },
                areContentsTheSame = { old, new -> old == new }
            ),
            onBindViewHolder = { position, item, binding ->
                binding.tvDesignComponentTitle.text = item
                ViewCompat.setTransitionName(binding.tvDesignComponentTitle, "title_$position")
            },
            itemList = items,
            onItemClick = { binding, item, position ->
                startActivity(
                    when(position) {
                        0 -> Intent(this, GuidelinesTypographyActivity::class.java)
                        1 -> Intent(this, GuidelinesButtonActivity::class.java)
                        2 -> Intent(this, GuidelinesColorsActivity::class.java)
                        3 -> Intent(this, GuidelinesProgressActivity::class.java)
                        4 -> Intent(this, GuidelinesIkuponActivity::class.java)
                        5 -> Intent(this, GuidelinesHomeCouponActivity::class.java)
                        6 -> Intent(this, GuidelinesBucketStampActivitty::class.java)
                        7 -> Intent(this, GuidelinesPoinSearch::class.java)
                        8 -> Intent(this, OnBoardingActivity::class.java)
                        9 -> Intent(this, RegisterActivity::class.java)
                        10 -> Intent(this, AnimatedStrokeActivity::class.java)
                        else -> Intent(this, WIPActivity::class.java)
                    },
                    binding.sharedElementOptions
                )
            }
        )
    }
}