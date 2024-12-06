package id.co.edtslib.poinkuuikit.ikupon_guidelines

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.poinkuuikit.databinding.ActivityGuidelinesHomeCouponBinding
import id.co.edtslib.poinkuuikit.generateLoremIpsum
import id.co.edtslib.poinkuuikit.ikupon_guidelines.GuidelinesIkuponActivity.DummyItem
import id.co.edtslib.poinkuuikit.ikupon_guidelines.reward_widget.HomeSectionAdapter
import id.co.edtslib.poinkuuikit.ikupon_guidelines.reward_widget.HomeSectionType
import id.co.edtslib.uikit.poinku.overlay.ImageParallaxOverlayLayout
import id.co.edtslib.uikit.poinku.pulltorefresh.LiquidRefreshLayout
import id.co.edtslib.uikit.poinku.utils.deviceWidth
import id.co.edtslib.uikit.poinku.utils.setLightStatusBar
import id.co.edtslib.uikit.poinku.utils.viewBinding

class GuidelinesHomeCouponActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityGuidelinesHomeCouponBinding>()

    private val sectionAdapter = HomeSectionAdapter.homeAdapter

    private val dummyItems by lazy {
        List(12) {
            DummyItem(
                id = it.toString(),
                image = "https://picsum.photos/200",
                title = generateLoremIpsum((1..5).random()),
                availability = Pair(1,100),
                voucherType = generateLoremIpsum(1),
                codeType = GuidelinesIKuponBottomTray.CodeType.Code,
                point = (1..1000).random(),
                ribbonVisibility = Pair(false, false),
                childItems = List((1..10).random()) { childIndex ->
                    DummyItem.DummyChildClass(
                        couponId = childIndex.toString(),
                        couponImage = "https://picsum.photos/150",
                        couponName = generateLoremIpsum((1..5).random()),
                        code = List(16) { (('A'..'Z') + ('0'..'9')).random() }.joinToString("")
                    )
                }
            )
        }
    }

    private val dummyPics: String get() {
        val picsWidth = deviceWidth
        val picsHeight = deviceWidth.div(3)

        return "https://picsum.photos/${picsWidth}/$picsHeight"
    }

    private var homeSections = listOf<HomeSectionType>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guidelines_home_coupon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setLightStatusBar()
        onRefreshLayoutListener()
        initDiffer()

        binding.rvMain.adapter = sectionAdapter.apply {
            items = homeSections
        }
    }

    private fun initDiffer() {
        homeSections = mutableListOf(
            HomeSectionType.PromoRecommendation(
                imageUrl = "https://res.cloudinary.com/dmduc9apd/image/upload/v1732853293/Image%20Illustration/fn8vxqpbldeqjsbibszi.png",
                title = "Rekomendasi Kupon Akhir Tahun!",
                description = "Tukarkan poin loyalty jadi kupon.",
                type = ImageParallaxOverlayLayout.OverlayType.IllustrationOnly,
                data = dummyItems
            ),
            HomeSectionType.FavoritePromo(
                imageUrl = "https://res.cloudinary.com/dmduc9apd/image/upload/v1732853293/Image%20Illustration/fn8vxqpbldeqjsbibszi.png",
                title = "Kupon Favorit Minggu Ini!",
                description = "Tukarkan stamp jadi kupon.",
                type = ImageParallaxOverlayLayout.OverlayType.IllustrationOnly,
                data = dummyItems
            ),
            HomeSectionType.NearlyExpirePromo(
                imageUrl = dummyPics,
                title = "Kupon Kamu Mau Berakhir!",
                description = "Gunakan kupon di merchant atau Indomaret.",
                type = ImageParallaxOverlayLayout.OverlayType.IllustrationWithBackground,
                data = dummyItems.take(9)
            )
        )
    }

    private fun updateDiffer() {
        // Since i already used differ, no need to call notifyDataSetChanged()
        sectionAdapter.items = homeSections
    }

    private fun onRefreshLayoutListener() {
        binding.main.setOnRefreshListener(object : LiquidRefreshLayout.OnRefreshListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun refreshing() {
                homeSections = homeSections.toMutableList().apply {
                    removeAt(0)
                    add(0, HomeSectionType.PromoRecommendationSkeleton)
                }

                updateDiffer()

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.main.finishRefreshing()
                }, 3000)
            }

            override fun completeRefresh() {
                homeSections = homeSections.toMutableList().apply {
                    removeAt(0)
                    add(0, HomeSectionType.PromoRecommendation(
                        imageUrl = "https://res.cloudinary.com/dmduc9apd/image/upload/v1732853293/Image%20Illustration/fn8vxqpbldeqjsbibszi.png",
                        title = "Rekomendasi Kupon Akhir Tahun!",
                        description = "Tukarkan poin loyalty jadi kupon.",
                        type = ImageParallaxOverlayLayout.OverlayType.IllustrationOnly,
                        data = dummyItems
                    ))
                }

                updateDiffer()
            }
        })
    }
}