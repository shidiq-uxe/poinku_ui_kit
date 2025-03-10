package id.co.edtslib.poinkuuikit.stamp_exchange_guidelines

import android.app.ActivityOptions
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSmoothScroller.SNAP_TO_START
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import id.co.edtslib.poinkuuikit.GuidelinesBaseActivity
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.poinkuuikit.databinding.ActivityGuidelinesBucketStampActivittyBinding
import id.co.edtslib.poinkuuikit.databinding.ItemStampBucketBinding
import id.co.edtslib.uikit.poinku.adapter.SingleItemViewTypeAdapter
import id.co.edtslib.uikit.poinku.adapter.singleItemViewTypeAdapter
import id.co.edtslib.uikit.poinku.chip.BucketChipGroup
import id.co.edtslib.uikit.poinku.chip.BucketChipGroupDelegate
import id.co.edtslib.uikit.poinku.chip.BucketData
import id.co.edtslib.uikit.poinku.coachmark.CoachMarkData
import id.co.edtslib.uikit.poinku.coachmark.CoachMarkOverlay
import id.co.edtslib.uikit.poinku.databinding.ItemGridPoinkuStampBinding
import id.co.edtslib.uikit.poinku.utils.MarginItem
import id.co.edtslib.uikit.poinku.utils.deviceWidth
import id.co.edtslib.uikit.poinku.utils.dimenPixelSize
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.drawable
import id.co.edtslib.uikit.poinku.utils.linearMarginItemDecoration
import id.co.edtslib.uikit.poinku.utils.setLightStatusBar
import id.co.edtslib.uikit.poinku.utils.viewBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GuidelinesBucketStampActivitty : GuidelinesBaseActivity() {

    private val binding by viewBinding<ActivityGuidelinesBucketStampActivittyBinding>()
    private val dummyList: List<BucketData> = listOf(
        BucketData(
            java.util.UUID.randomUUID().toString(),
            "Indomaret Festival",
            R.drawable.indomaret,
            (2..20).random(),
        ),
        BucketData(
            java.util.UUID.randomUUID().toString(),
            "Gebyar Unilever Indonesia",
            R.drawable.unilever,
            (2..20).random(),
        ),
        BucketData(
            java.util.UUID.randomUUID().toString(),
            "Danone Festival",
            R.drawable.danone,
            (2..20).random(),
        ),
        BucketData(
            java.util.UUID.randomUUID().toString(),
            "Mister Donut Fair",
            R.drawable.mister_donut,
            (2..20).random(),
        ),
        BucketData(
            java.util.UUID.randomUUID().toString(),
            "Yummy Choice",
            R.drawable.yummy_choice,
            (2..20).random(),
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_guidelines_bucket_stamp_activitty)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setLightStatusBar()
        bindBucketChipItems()
        attachBucketAdapter()

        showCoachmark()
    }

    private fun bindBucketChipItems() {
        binding.cgBucket.items = dummyList

        binding.cgBucket.delegate = object : BucketChipGroupDelegate {
            override fun onBucketChipClicked(
                position: Int,
                bucketData: BucketData
            ) {
                scrollBucketItemToPosition(position)
            }
        }
    }

    private fun scrollBucketItemToPosition(position: Int) {
        val layoutManager = binding.rvBucket.layoutManager as? LinearLayoutManager
        layoutManager?.let {
            val smoothScroller = object : LinearSmoothScroller(this) {
                override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            }
            smoothScroller.targetPosition = position
            layoutManager.startSmoothScroll(smoothScroller)
        }
    }

    private fun attachBucketAdapter() {
        val spacingItemDecorator = linearMarginItemDecoration(
            orientation = LinearLayoutManager.VERTICAL,
            margin = MarginItem(
                first = 0,
                last = 0,
                top = dimenPixelSize(id.co.edtslib.uikit.poinku.R.dimen.xs) ?: 12.dp.toInt(),
                bottom = dimenPixelSize(id.co.edtslib.uikit.poinku.R.dimen.xs) ?: 12.dp.toInt()
            )
        )

        binding.rvBucket.adapter = singleItemViewTypeAdapter<BucketData, ItemStampBucketBinding>(
            diff = SingleItemViewTypeAdapter.Diff(
                areItemsTheSame = { old, new -> old.id == new.id },
                areContentsTheSame = { old, new -> old == new }
            ),
            onBindViewHolder = { position, item, binding ->
                binding.ivBucketStampImage.setImageDrawable(
                    drawable(item.bucketImage as? Int ?: R.drawable.indomaret)
                )

                binding.tvBucketStampName.text = item.bucketName
                binding.tvBucketStampOwnedCount.text = "${item.bucketStampCount} Stamp"

                attachBucketChildAdapter(binding.rvStamps)
            },
            itemList = dummyList,
        )

        if (binding.rvBucket.itemDecorationCount >= 1) {
            binding.rvBucket.removeItemDecorationAt(0)
        }

        binding.rvBucket.addItemDecoration(spacingItemDecorator)

        binding.rvBucket.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val firstVisiblePosition = (recyclerView.layoutManager as LinearLayoutManager)
                    .findFirstVisibleItemPosition()

                binding.cgBucket.setSelectedPosition(firstVisiblePosition, fromScroll = binding.cgBucket.isUserScrolling)
            }
        })
    }

    private fun attachBucketChildAdapter(recyclerView: RecyclerView) {
        val spacingItemDecorator = linearMarginItemDecoration(
            orientation = LinearLayoutManager.HORIZONTAL,
            margin = MarginItem(
                first = 0,
                last = 0,
                left = dimenPixelSize(id.co.edtslib.uikit.poinku.R.dimen.dimen_6) ?: 6.dp.toInt(),
                right = dimenPixelSize(id.co.edtslib.uikit.poinku.R.dimen.dimen_6) ?: 6.dp.toInt()
            )
        )

        recyclerView.adapter = singleItemViewTypeAdapter<Any, ItemGridPoinkuStampBinding>(
            diff = SingleItemViewTypeAdapter.Diff(
                areItemsTheSame = { old, new -> old == new },
                areContentsTheSame = { old, new -> old == new }
            ),
            onBindViewHolder = { position, item, binding ->
                binding.root.updateLayoutParams<ViewGroup.LayoutParams> {
                    width = (deviceWidth * 0.33f).toInt()
                }

                binding.ivCouponImage.setImageDrawable(drawable(id.co.edtslib.uikit.poinku.R.drawable.ic_placeholder_medium_24))
                binding.tvExpiredIn.text = "Kuota mau habis"
                binding.tvCouponName.text = "Diskon Rp2.000 Kellogg’s Frosted Flakes "
                binding.chipCouponCategory.text = "50 Stamp"
            },
            itemList = List(4) {}
        )

        if (recyclerView.itemDecorationCount >= 1) {
            recyclerView.removeItemDecorationAt(0)
        }

        recyclerView.addItemDecoration(spacingItemDecorator)
    }

    // List of targets
    private val placeholderTargets = mutableListOf<View>()

    private fun showCoachmark() {
        val coachmarkTitles = resources.getStringArray(R.array.stamp_bucket_coachmark_titles)
        val coachmarkDescriptions = resources.getStringArray(R.array.stamp_bucket_coachmark_descriptions)

        // Target 1 - Bucket Chip Group
        placeholderTargets.add(binding.coachMarkPlaceholder1)

        binding.rvBucket.post {
            binding.rvBucket.findViewHolderForAdapterPosition(0)?.apply {
                // Target 2 - Bucket RecyclerView Item
                itemView.findViewById<View>(R.id.coachMarkPlaceholder2).let {
                    placeholderTargets.add(it)
                }

                // Target 3 - Stamp Card
                itemView.findViewById<View>(R.id.rvStamps).let {
                    placeholderTargets.add(it)
                }

                // Target 4 - Bucket RecyclerView See All
                itemView.findViewById<View>(R.id.tvSeeAll).let {
                    placeholderTargets.add(it)
                }
            }

            // Map into coachmark items
            val coachmarkItems = placeholderTargets.indices.map { index ->
                CoachMarkData(
                    target = placeholderTargets[index],
                    title = coachmarkTitles[index],
                    description = coachmarkDescriptions[index]
                )
            }

            // 1 Second Delay before showing Coachmark & Spotlight or after loading all the api
            Handler(Looper.getMainLooper()).postDelayed({
                CoachMarkOverlay.Builder(this)
                    .setCoachMarkItems(coachmarkItems)
                    .build()
            }, 1000L)
        }
    }
}