package id.co.edtslib.poinkuuikit.ikupon_guidelines.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import id.co.edtslib.poinkuuikit.ikupon_guidelines.GuidelinesHomeCouponActivity
import id.co.edtslib.poinkuuikit.ikupon_guidelines.GuidelinesIKuponBottomTray
import id.co.edtslib.poinkuuikit.ikupon_guidelines.GuidelinesIkuponActivity
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.adapter.SingleItemViewTypeAdapter
import id.co.edtslib.uikit.poinku.adapter.singleItemViewTypeAdapter
import id.co.edtslib.uikit.poinku.recyclerView.HorizontalSlidingRecyclerView
import id.co.edtslib.uikit.poinku.databinding.ItemGridPoinkuIcouponBinding
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.colorStateList
import id.co.edtslib.uikit.poinku.utils.dimenPixelSize
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.drawable

@SuppressLint("UseCompatTextViewDrawableApis")
class ProductHorizontalCarousel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalSlidingRecyclerView<GuidelinesIkuponActivity.DummyItem, ItemGridPoinkuIcouponBinding>(context, attrs, defStyleAttr) {

    private val iKuponDetailTray by lazy {
        GuidelinesIKuponBottomTray(context as GuidelinesHomeCouponActivity)
    }

    init {
        itemSpacing = context.dimenPixelSize(R.dimen.xs) ?: 12.dp.toInt()
        carouselAdapter = singleItemViewTypeAdapter<GuidelinesIkuponActivity.DummyItem, ItemGridPoinkuIcouponBinding>(
            diff = SingleItemViewTypeAdapter.Diff(
                areContentsTheSame = { old, new -> old == new },
                areItemsTheSame = { old, new -> old.id == new.id }
            ),
            onBindViewHolder = { position, item, itemBinding ->
                itemBinding.bindItemData(item)
            }
        )
    }

    private fun ItemGridPoinkuIcouponBinding.bindItemData(item: GuidelinesIkuponActivity.DummyItem) {
        Glide.with(context)
            .load(item.image)
            .error(ColorDrawable(Color.LTGRAY))
            .into(ivCouponImage)

        root.updateLayoutParams<ViewGroup.LayoutParams> {
            width = (context.resources.displayMetrics.widthPixels * 0.33).toInt()
        }

        cv.updateLayoutParams<MarginLayoutParams> {
            setMargins(0)
        }

        root.removeView(ribbonNew)
        root.removeView(ribbonCouponLeft)

        tvCouponName.setLines(2)

        val randomState = (0..2).random()
        when(randomState) {
            0 -> {
                tvExpiredIn.setCouponAsAvailableState("i-Kupon Tersedia")
                chipCouponCategory.setAsVoucherTypeState("i-Kupon", context.drawable(R.drawable.ic_timer_16))
            }
            1 -> {
                tvExpiredIn.setCouponAsNearlyExpiredState("Segera Habis")
                chipCouponCategory.setAsPointState("3000 Point")
            }
            2 -> {
                tvExpiredIn.setOwnedCouponAsNearlyExpiredState("Berakhir Dalam 1 Hari")
                chipCouponCategory.setAsVoucherTypeState("Merchant", context.drawable(R.drawable.ic_timer_16))
            }
        }

        tvCouponName.text = item.title

        btnUseCoupon.setOnClickListener {
            iKuponDetailTray.apply { this.codeType = item.codeType }.show(item.childItems)
        }
    }

    private fun TextView.setOwnedCouponAsNearlyExpiredState(text: String) {
        setBackgroundColor(context.color(R.color.alert_error))
        setTextColor(context.color(R.color.alert_primary))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            compoundDrawableTintList = context.colorStateList(R.color.alert_primary)
        }
        setText(text)
    }

    private fun TextView.setCouponAsNearlyExpiredState(text: String) {
        setBackgroundColor(context.color(R.color.warning_background))
        setTextColor(context.color(R.color.warning_primary))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            compoundDrawableTintList = context.colorStateList(R.color.warning_primary)
        }
        setText(text)
    }

    private fun TextView.setCouponAsAvailableState(text: String) {
        setBackgroundColor(context.color(R.color.alert_link))
        setTextColor(context.color(R.color.primary_30))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            compoundDrawableTintList = context.colorStateList(R.color.primary_30)
        }
        setText(text)
    }

    private fun Chip.setAsVoucherTypeState(
        text: String,
        icon: Drawable
    ) {
        chipBackgroundColor = context.colorStateList(R.color.alert_link)
        setTextColor(context.color(R.color.primary_30))
        chipIconTint = context.colorStateList(R.color.primary_30)
        setText(text)
        chipIcon = icon
    }

    private fun Chip.setAsPointState(
        text: String,
        icon: Drawable? = null
    ) {
        chipBackgroundColor = context.colorStateList(R.color.warning_secondary_background)
        setTextColor(context.color(R.color.secondary_30))
        chipIcon = icon
        setText(text)
    }

}