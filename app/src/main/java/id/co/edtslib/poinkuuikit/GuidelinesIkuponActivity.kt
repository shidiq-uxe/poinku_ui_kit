package id.co.edtslib.poinkuuikit

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.transition.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import id.co.edtslib.poinkuuikit.databinding.ActivityGuidelinesIkuponBinding
import id.co.edtslib.uikit.poinku.adapter.AnimationType
import id.co.edtslib.uikit.poinku.adapter.AnimationWrapperAdapter
import id.co.edtslib.uikit.poinku.adapter.multiTypeAdapter
import id.co.edtslib.uikit.poinku.databinding.ItemGridPoinkuIcouponBinding
import id.co.edtslib.uikit.poinku.databinding.ItemListPoinkuIcouponBinding
import id.co.edtslib.uikit.poinku.ribbon.Ribbon
import id.co.edtslib.uikit.poinku.searchbar.SearchBar
import id.co.edtslib.uikit.poinku.utils.dimen
import id.co.edtslib.uikit.poinku.utils.px
import id.co.edtslib.uikit.poinku.utils.setLightStatusBar
import id.co.edtslib.uikit.poinku.utils.viewBinding
import id.co.edtslib.uikit.poinku.R as UIKitR

class GuidelinesIkuponActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityGuidelinesIkuponBinding>()

    private val viewPool by lazy {
        RecycledViewPool().apply {  setMaxRecycledViews(layoutViewType.ordinal, 10) }
    }

    private val randomVisibilityState = listOf(true, false)

    private val adapter = multiTypeAdapter(
        diffCallback = object : DiffUtil.ItemCallback<DummyItem>() {
            override fun areItemsTheSame(oldItem: DummyItem, newItem: DummyItem) =  oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: DummyItem, newItem: DummyItem) = oldItem == newItem
        },
        viewTypeConfig = {
            layoutViewType.ordinal
        },
        bindingConfig = {
            registerViewType(
                viewType = LayoutViewType.GRID_2.ordinal,
                bindingInflater = { layoutInflater, viewGroup, attachToParent ->
                    ItemGridPoinkuIcouponBinding.inflate(layoutInflater, viewGroup, attachToParent)
                },
                bind = { position, itemBinding, item ->
                    (itemBinding as ItemGridPoinkuIcouponBinding).apply {
                        Glide.with(this@GuidelinesIkuponActivity)
                            .load(item.image)
                            .error(ColorDrawable(Color.LTGRAY))
                            .into(this.ivCouponImage)

                        this.tvCouponName.text = item.title
                        this.ribbonNew.ribbonText = "Baru!"
                        this.tvExpiredIn.text = "Hingga ${(1..30).random()} Nov 2024"
                        this.chipCouponCategory.text = item.voucherType

                        ribbonCouponLeft.apply {
                            this.elevation = 6.px
                            this.ribbonText = "${item.availability.first}x"
                            this.isVisible = randomVisibilityState.random()
                        }.anchorToView(
                            rootParent = itemBinding.root,
                            targetView = itemBinding.cv,
                            offsetY = 8.px.toInt()
                        )

                        ribbonNew.apply {
                            this.elevation = 6.px
                        }.anchorToView(
                            rootParent = itemBinding.root,
                            targetView = itemBinding.tvExpiredIn,
                            verticalAlignment = Ribbon.VerticalAlignment.Top
                        )
                    }
                }
            )

            registerViewType(
                viewType = LayoutViewType.GRID_3.ordinal,
                bindingInflater = { layoutInflater, viewGroup, attachToParent ->
                    ItemGridPoinkuIcouponBinding.inflate(layoutInflater, viewGroup, attachToParent)
                },
                bind = { position, itemBinding, item ->
                    (itemBinding as ItemGridPoinkuIcouponBinding).apply {
                        Glide.with(this@GuidelinesIkuponActivity)
                            .load(item.image)
                            .error(ColorDrawable(Color.LTGRAY))
                            .into(this.ivCouponImage)

                        this.tvCouponName.text = item.title
                        this.ribbonCouponLeft.ribbonText = "${item.availability.first}x"
                        this.ribbonNew.ribbonText = "Baru!"
                        this.tvExpiredIn.text = "Hingga ${(1..30).random()} Nov 2024"
                        this.chipCouponCategory.text = item.voucherType

                        this.tvExpiredIn.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                        this.chipCouponCategory.chipIcon = null

                        ribbonCouponLeft.isVisible = listOf(true, false).random()
                        ribbonNew.isVisible = listOf(true, false).random()

                        ribbonCouponLeft.apply {
                            this.elevation = 6.px
                        }.anchorToView(
                            rootParent = itemBinding.root,
                            targetView = itemBinding.cv,
                            offsetY = 8.px.toInt()
                        )

                        ribbonNew.apply {
                            this.elevation = 6.px
                        }.anchorToView(
                            rootParent = itemBinding.root,
                            targetView = itemBinding.tvExpiredIn,
                            verticalAlignment = Ribbon.VerticalAlignment.Top
                        )
                    }
                }
            )

            registerViewType(
                viewType = LayoutViewType.LINEAR.ordinal,
                bindingInflater = { layoutInflater, viewGroup, attachToParent ->
                    ItemListPoinkuIcouponBinding.inflate(layoutInflater, viewGroup, attachToParent)
                },
                bind = { position, itemBinding, item ->
                    (itemBinding as ItemListPoinkuIcouponBinding).apply {
                        Glide.with(this@GuidelinesIkuponActivity)
                            .load(item.image)
                            .error(ColorDrawable(Color.LTGRAY))
                            .into(this.ivCouponImage)

                        this.tvCouponName.text = item.title
                        this.ribbonCouponLeft.text = "${item.availability.first}x"
                        this.tvExpiredIn.text = "Hingga ${(1..30).random()} Nov 2024"
                        this.tvCouponType.text = item.voucherType

                        ribbonCouponLeft.isVisible = listOf(true, false).random()
                        ribbonNew.isVisible = listOf(true, false).random()

                        ribbonNew.apply {
                            this.elevation = 6.px
                            this.ribbonText = "Baru!"
                            this.textAppearanceRes = UIKitR.style.TextAppearance_Rubik_Semibold_H4
                        }.anchorToView(
                            rootParent = itemBinding.root,
                            targetView = itemBinding.cv,
                            offsetY = 8.px.toInt()
                        )
                    }
                }
            )
        },
        onViewDetachedFromWindow = { holder ->
            holder.itemView.clearAnimation()
        }
    ).apply { items = List(18) {
        DummyItem(
            image = images.random(),
            title = titles.random(),
            availability = availabilities.random(),
            voucherType = voucherType.random(),
            point = (1..1000).random(),
            ribbonVisibility = Pair(randomVisibilityState.random(), randomVisibilityState.random())
        )
    } }


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
        "https://s3-alpha-sig.figma.com/img/6c2f/36e8/ffcf10f6d9edb5dcb7fe13091b83a4ad?Expires=1731283200&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=gka7TYPhziv1v6edhrOuZ9T3LVhZ7KO019e7bosuWR3kCgUp45hVISV1wV0KDIuy88UgGNtNIq3nBM8MIdmMYJPNBq80pw8brqkEcu~ij6W1aounkuUQo~PxMhVgWp4vs5uEmD4dS92UfG~6sYYfI8SIKFaB55Ms~TXfHZqqSGrYB~qfew1B8XYd0odkzA49OSBhvSYrY60pzE~feVT~gc0aA6pLEDigliJY5jplGsedWngcknpqgdh8FI3DTK542k9MWpr4lpF3pRtPlLZH~gczdjRoQ3~KxjkgryN1RLbiUahtDnC2W8703IroPoOmbiMAu19I9aRAv62FOGbzIg__",
        "https://s3-alpha-sig.figma.com/img/5ab7/3d0f/f2c98baca9fe6fc7423e3a62fde5a913?Expires=1731283200&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=Jzh14WhMT59xtIezfxJ22y5IpTnQWjePx81ciFsT~HpN1O~8PlqhRsXVNzymogZjXz-4Ka9c5kShqZITG8PlVLOCMjvoU~Hbj2vH~2LnXo4e8d5U6MG4liORNBYVyNSAEcmEv3hpY1Ih8vr3aE0lI~6Fj5cOlQUkflTj04LNgeg1HCcLegebXKg0I5ri7YdWD5OQSFc6xxZv4GzcrPrtMFfyLSeyoyC0RniLkw4L3Jb~WRqnRuYo1XamTnd12ra8UdvAC85W4pstRYAN1AxayrELgOZtXQvBG70vm7tO8vyzpU70pBKFtYdsYhrICsQ7hAUQqpuqBO4Wm82MQOobbw__",
        "https://s3-alpha-sig.figma.com/img/f08e/13c5/34173b537edfebe07ce808f34503dad7?Expires=1731283200&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=otMx7qfesojwgs2TZg~Bi6ktxPThnoeaJyJIhSfZ6WgkRzkzCukc67La1eCJxK~4qzKFG6rtncRhY-5UvXZqdkiOIfV66mTeEgfr185qnKoZHbmgOpIgvEbl~8MOKrWpUhY-WEyzl9Hx0agE3X85Xu3HZf2QJqg9hfmfdlqgxnWAzL~q-Q1vQb3Zfp9ttWVFL7StDYpFxVgs7ZIaQnoQgyL0lY1TJqky6M2EmnNGyXRyetIpuYhKRkj3lu1BCn379cD-8lks~bPh50vbTAvSiWDU7NbafqNvGGmy9WqLxwZ-zWRDia8OOddfQ4Xi~x3KSN5LKKBgdRVNcHHMwoKekg__",
        "https://s3-alpha-sig.figma.com/img/1750/330d/6648b7b26d65723688e8afa3d104153e?Expires=1731283200&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=Mgn81C~Duyy8qwjQZVuzP8D9hfS3cSHqO8LfkGJZ0~g0yp~KVmBuTc5nZvd6OCrxPXFU0ywh-S8Zb9-zV4bdbEz39Von~GzrzVd5mOBSKPFg57FacR8Gd8FlQDyZhPGna6lGBZ76NzWU4g1zU2R6d6T2zJ8~oV6dPSRFc9vl7Kz67mHY3UmMq606MydKbe~4X0hyQTDMum1QIwumMx8KcPx9JOYUOqf9YX~aTkiPuzFB0A9I3KO87nuw5IyrPar0jOXOD~tpoC~IEx3xBQXCpg1rQA-phT4zIAVCuX~A-w2G9CZgh5QXg~VqTG7gc8aSFbihLYEr0-nJfTi4bEyk2Q__"
    )

    private var layoutViewType = LayoutViewType.GRID_2
        set(value) {
            field = value

            val transition = AutoTransition().apply {
                duration = 200

                addListener(
                    onStart = { /* Todo : Disable Menu (Action ViewType) */ },
                    onEnd = { /* Todo : Disable Menu (Action ViewType) */ }
                )
            }

            TransitionManager.beginDelayedTransition(binding.rvIKupon, transition)

            binding.rvIKupon.layoutManager = when(value) {
                LayoutViewType.GRID_2 -> StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                LayoutViewType.GRID_3 -> StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                LayoutViewType.LINEAR -> LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }

            // Remove the current item decoration if it exists


            // Create and add the new item decoration
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guidelines_ikupon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setLightStatusBar()
        setupSearchBar()
        setupRecyclerView()
        onMenuItemClickListener()
    }

    private fun onMenuItemClickListener() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            menuItem?.let { _ ->
                when(menuItem.itemId) {
                    R.id.action_history -> {}
                    R.id.action_view_type -> {
                        layoutViewType = when(layoutViewType) {
                            LayoutViewType.LINEAR -> LayoutViewType.GRID_2
                            LayoutViewType.GRID_2 -> LayoutViewType.GRID_3
                            LayoutViewType.GRID_3 -> LayoutViewType.LINEAR
                        }
                    }
                }
            }

            true
        }
    }

    private fun setupSearchBar() {
        binding.sbSearchCoupon.placeholderAnimationType = SearchBar.PlaceholderAnimationType.TypeWriterWithPrefix
    }

    private fun setupRecyclerView() {
        val animationAdapter = AnimationWrapperAdapter(adapter, AnimationType.Scale()).apply {
            shouldAnimateDuringScroll(false)
        }

        adapter.setHasStableIds(true)
        binding.rvIKupon.setRecycledViewPool(viewPool)
        binding.rvIKupon.adapter = adapter
    }

    enum class LayoutViewType {
        GRID_2, GRID_3, LINEAR
    }

    data class DummyItem(
        val image: String,
        val title: String,
        val availability: Pair<Int, Int>,
        val voucherType: String,
        val point: Int,
        val ribbonVisibility: Pair<Boolean, Boolean>
    )
}