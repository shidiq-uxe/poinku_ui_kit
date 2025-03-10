package id.co.edtslib.poinkuuikit.ikupon_guidelines

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import id.co.edtslib.poinkuuikit.R
import id.co.edtslib.poinkuuikit.databinding.ActivityGuidelinesIkuponBinding
import id.co.edtslib.uikit.poinku.adapter.AnimationType
import id.co.edtslib.uikit.poinku.adapter.AnimationWrapperAdapter
import id.co.edtslib.uikit.poinku.adapter.multiTypeAdapter
import id.co.edtslib.uikit.poinku.databinding.ItemGridPoinkuIcouponBinding
import id.co.edtslib.uikit.poinku.databinding.ItemListPoinkuIcouponBinding
import id.co.edtslib.uikit.poinku.ribbon.Ribbon
import id.co.edtslib.uikit.poinku.searchbar.SearchBar
import id.co.edtslib.uikit.poinku.utils.addViewTransition
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.setLightStatusBar
import id.co.edtslib.uikit.poinku.utils.viewBinding
import id.co.edtslib.uikit.searchbar.SearchBarDelegate
import id.co.edtslib.uikit.poinku.R as UIKitR

class GuidelinesIkuponActivity : AppCompatActivity() {

    private val binding by viewBinding<ActivityGuidelinesIkuponBinding>()

    private val viewPool by lazy {
        RecycledViewPool().apply { setMaxRecycledViews(layoutViewType.ordinal, 2) }
    }

    private val randomVisibilityState = listOf(true, false)
    private val iKuponDetailTray by lazy {
        GuidelinesIKuponBottomTray(this)
    }

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
                            this.elevation = 6.dp
                            this.ribbonText = "${item.availability.first}x"
                            this.isVisible = randomVisibilityState.random()
                        }.anchorToView(
                            rootParent = itemBinding.root,
                            targetView = itemBinding.cv,
                            offsetY = 8.dp.toInt()
                        )

                        ribbonNew.apply {
                            this.elevation = 6.dp
                        }.anchorToView(
                            rootParent = itemBinding.root,
                            targetView = itemBinding.tvExpiredIn,
                            verticalAlignment = Ribbon.VerticalAlignment.Top
                        )

                        itemBinding.btnUseCoupon.setOnClickListener {
                            iKuponDetailTray.apply { this.codeType = item.codeType }.show(item.childItems)
                        }
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
                            this.elevation = 6.dp
                        }.anchorToView(
                            rootParent = itemBinding.root,
                            targetView = itemBinding.cv,
                            offsetY = 8.dp.toInt()
                        )

                        ribbonNew.apply {
                            this.elevation = 6.dp
                        }.anchorToView(
                            rootParent = itemBinding.root,
                            targetView = itemBinding.tvExpiredIn,
                            verticalAlignment = Ribbon.VerticalAlignment.Top
                        )

                        itemBinding.btnUseCoupon.setOnClickListener {
                            iKuponDetailTray.apply { this.codeType = item.codeType }.show(item.childItems)
                        }
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
                            this.elevation = 6.dp
                            this.ribbonText = "Baru!"
                            this.textAppearanceRes = UIKitR.style.TextAppearance_Rubik_H4_Heavy
                        }.anchorToView(
                            rootParent = itemBinding.root,
                            targetView = itemBinding.cv,
                            offsetY = 8.dp.toInt()
                        )

                        itemBinding.btnUseCoupon.setOnClickListener {
                            iKuponDetailTray.apply { this.codeType = item.codeType }.show(item.childItems)
                        }
                    }
                }
            )
        },
        onViewDetachedFromWindow = { holder ->
            holder.itemView.clearAnimation()
        }
    ).apply { items = List(50) {
        DummyItem(
            id = it.toString(),
            image = images.first(),
            title = titles.first(),
            availability = availabilities.first(),
            voucherType = voucherType.first(),
            codeType = codeType.first(),
            point = (1..1000).first(),
            ribbonVisibility = Pair(randomVisibilityState.first(), randomVisibilityState.first()),
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

    private val codeType get() = listOf(
        GuidelinesIKuponBottomTray.CodeType.Barcode,
        GuidelinesIKuponBottomTray.CodeType.QR,
        GuidelinesIKuponBottomTray.CodeType.Code
    )

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

    private var layoutViewType = LayoutViewType.GRID_2
        set(value) {
            field = value

            binding.rvIKupon.addViewTransition()

            binding.rvIKupon.layoutManager = when(value) {
                LayoutViewType.GRID_2 -> StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                LayoutViewType.GRID_3 -> StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                LayoutViewType.LINEAR -> LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
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
        /*binding.sbSearchCoupon.placeholderAnimationType = SearchBar.PlaceholderAnimationType.TypeWriterWithPrefix
        binding.sbSearchCoupon.searchBarDelegate = object : SearchBarDelegate {
            override fun onCloseIconClicked(view: View) {
                binding.sbSearchCoupon.text = ""
            }

            override fun onFocusChange(view: View, hasFocus: Boolean) {}

            override fun onTextChange(text: String) {}
        }

        binding.sbSearchCoupon.searchBarType = SearchBar.SearchBarType.BORDERLESS*/
    }

    private fun setupRecyclerView() {
        val animationAdapter = AnimationWrapperAdapter(adapter, AnimationType.Scale()).apply {
            shouldAnimateDuringScroll(true)
        }
        binding.rvIKupon.adapter = animationAdapter
    }

    enum class LayoutViewType {
        GRID_2, GRID_3, LINEAR
    }

    data class DummyItem(
        val id: String,
        val image: String,
        val title: String,
        val availability: Pair<Int, Int>,
        val voucherType: String,
        val point: Int,
        val codeType: GuidelinesIKuponBottomTray.CodeType,
        val ribbonVisibility: Pair<Boolean, Boolean>,
        val childItems: List<DummyChildClass>
    ) {
        data class DummyChildClass(
            val couponId: String,
            val couponName: String,
            val code: String,
            val couponImage: Any,
        )
    }
}