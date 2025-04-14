package id.co.edtslib.poinkuuikit.poin_guidelines

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import id.co.edtslib.poinkuuikit.GuidelinesBaseActivity
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.poinkuuikit.databinding.ActivityGuidelinesPoinSearchBinding
import id.co.edtslib.poinkuuikit.ikupon_guidelines.GuidelinesIKuponBottomTray
import id.co.edtslib.poinkuuikit.ikupon_guidelines.GuidelinesIkuponActivity.DummyItem
import id.co.edtslib.uikit.poinku.adapter.multiTypeAdapter
import id.co.edtslib.uikit.poinku.databinding.ItemGridPoinkuIcouponBinding
import id.co.edtslib.uikit.poinku.ribbon.Ribbon
import id.co.edtslib.uikit.poinku.utils.TextStyle
import id.co.edtslib.uikit.poinku.utils.buildHighlightedMessage
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.viewBinding
import kotlin.math.abs
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

        initFilterChips()
        initAppBarBehavior()
        initPoinSpan()
        bindAdapter()
    }

    private fun initAppBarBehavior() {
        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val collapseProgress = abs(verticalOffset).toFloat() / totalScrollRange

            binding.widget.root.alpha = 1 - collapseProgress

            val anchorViewHeight = binding.tvPointAnchor.height

            val targetDuration = if (collapseProgress < 1f) 200 else 300

            val targetTranslation = if (collapseProgress < 0.8f) -anchorViewHeight else if (collapseProgress == 1f) 0 else binding.tvPointAnchor.translationY
            val targetAlpha = if (collapseProgress < 0.8f) 0f else if (collapseProgress == 1f) 1f else binding.tvPointAnchor.alpha

            val alphaAnimation = ObjectAnimator.ofFloat(binding.tvPointAnchor, View.ALPHA, targetAlpha).apply {
                duration = targetDuration.div(2).toLong()
            }

            val translationAnimation = ObjectAnimator.ofFloat(binding.tvPointAnchor, View.TRANSLATION_Y, targetTranslation.toFloat()).apply {
                duration = targetDuration.toLong()
            }

            AnimatorSet()
                .apply { playTogether(alphaAnimation, translationAnimation) }
                .also { it.start() }
        }
    }

    private fun initPoinSpan() {
        val message = buildHighlightedMessage(
            context = this,
            message = "Kamu Punya: 2480 Poin Loyalty",
            defaultTextAppearance = TextStyle.h3Style(
                context = this,
                color = this.color(UIKitR.color.grey_50),
                fontFamily = UIKitR.font.rubik
            ),
            highlightedMessages = listOf("2480 Poin Loyalty"),
            highlightedTextAppearance = listOf(
                TextStyle.h3Style(
                    context = this,
                    color = this.color(UIKitR.color.grey_70),
                    fontFamily = UIKitR.font.rubik_semibold
                )
            )
        )

        binding.tvPointAnchor.text = message
    }

    private fun initFilterChips() {
        binding.cgFilter.apply {
            items = listOf(
                Pair("1", "Semua"),
                Pair("2", "Food & Beverage"),
                Pair("3", "Detergent"),
                Pair("4", "Yummy Choice"),
                Pair("5", "Diamond"),
            )
        }.also {
            binding.cgFilter.checkChipByPosition(0)
        }
    }

    private val titles get() = listOf(
        "Voucher Belanja Elektronik Rp100.000" ,
        "Red Spirit - Double Fry Pan Red",
        "Skin Weapon Magallanica: Free Fire",
        "Diskon 10% XBox Series X 512 Tb - Black Version",
        "Title"
    )
    private val availabilities get() = listOf(
        (1..1000).random() to 1000,
        (1..50).random() to 50,
        (1..100).random() to 100,
        (1..3).random() to 3
    )
    private val voucherType get()  = listOf(
        "Delivery",
        "Merchant",
        "i-Kupon"
    )

    private val images get() = listOf(
        "https://picsum.photos/270",
        "https://picsum.photos/280",
        "https://picsum.photos/290"
    )

    private val adapter = multiTypeAdapter(
        diffCallback = object : DiffUtil.ItemCallback<DummyItem>() {
            override fun areItemsTheSame(oldItem: DummyItem, newItem: DummyItem) =  oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: DummyItem, newItem: DummyItem) = oldItem == newItem
        },
        viewTypeConfig = {
            1
        },
        bindingConfig = {
            registerViewType(
                viewType = 1,
                bindingInflater = { layoutInflater, viewGroup, attachToParent ->
                    ItemGridPoinkuIcouponBinding.inflate(layoutInflater, viewGroup, attachToParent)
                },
                bind = { position, itemBinding, item ->
                    (itemBinding as ItemGridPoinkuIcouponBinding).apply {
                        Glide.with(this@GuidelinesPoinSearch)
                            .load(item.image)
                            .error(ColorDrawable(Color.LTGRAY))
                            .into(this.ivCouponImage)

                        this.tvCouponName.text = item.title
                        this.ribbonNew.ribbonText = "Baru!"
                        this.tvExpiredIn.text = "Hingga ${(1..30).random()} Nov 2024"
                        this.chipCouponCategory.text = item.voucherType

                        ribbonCouponLeft.apply {
                            this.elevation = 6.dp
                            this.ribbonText = "${item.availability.first}x"
                            this.isVisible = false
                        }.anchorToView(
                            targetView = itemBinding.cv,
                            offsetY = 8.dp.toInt()
                        )

                        ribbonNew.apply {
                            this.elevation = 6.dp
                            this.isVisible = false
                        }.anchorToView(
                            targetView = itemBinding.tvExpiredIn,
                            verticalAlignment = Ribbon.VerticalAlignment.Top
                        )

                        itemBinding.btnUseCoupon.setOnClickListener {

                        }
                    }
                }
            )
        },
        onViewDetachedFromWindow = { holder ->
            holder.itemView.clearAnimation()
        }
    ).apply { items = List(12) {
        DummyItem(
            id = it.toString(),
            image = images.first(),
            title = titles.first(),
            availability = availabilities.first(),
            voucherType = voucherType.first(),
            codeType = GuidelinesIKuponBottomTray.CodeType.QR,
            point = (1..1000).first(),
            ribbonVisibility = Pair(true, true),
            childItems = List((1..10).first()) { childIndex ->
                DummyItem.DummyChildClass(
                    couponId = childIndex.toString(),
                    couponImage = images.first(),
                    couponName = titles.first(),
                    code = List(16) { (('A'..'Z') + ('0'..'9')).random() }.joinToString("")
                )
            }
        )
    } }

    private fun bindAdapter() {
        binding.rvPoint.adapter = adapter
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