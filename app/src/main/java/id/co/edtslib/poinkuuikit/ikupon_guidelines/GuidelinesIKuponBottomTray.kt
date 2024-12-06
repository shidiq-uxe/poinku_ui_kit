package id.co.edtslib.poinkuuikit.ikupon_guidelines

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import id.co.edtslib.poinkuuikit.generateLoremIpsum
import id.co.edtslib.poinkuuikit.ikupon_guidelines.GuidelinesIkuponActivity.DummyItem.DummyChildClass
import id.co.edtslib.uikit.poinku.adapter.multiTypeAdapter
import id.co.edtslib.uikit.poinku.databinding.ItemIcouponBarcodeBinding
import id.co.edtslib.uikit.poinku.databinding.ItemIcouponCodeBinding
import id.co.edtslib.uikit.poinku.databinding.ItemIcouponQrBinding
import id.co.edtslib.uikit.poinku.databinding.LayoutIcouponDetailBinding
import id.co.edtslib.uikit.poinku.tray.BottomSheetTray
import id.co.edtslib.uikit.poinku.tray.BottomTrayDelegate
import id.co.edtslib.uikit.poinku.utils.MarginItem
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.deviceWidth
import id.co.edtslib.uikit.poinku.utils.inflater
import id.co.edtslib.uikit.poinku.utils.linearMarginItemDecoration
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.resetScreenBrightness
import id.co.edtslib.uikit.poinku.utils.setScreenBrightness
import id.co.edtslib.uikit.poinku.R as UIKitR

class GuidelinesIKuponBottomTray(
    private val context: FragmentActivity
) {

    var dialog: BottomSheetTray? = null

    val binding = LayoutIcouponDetailBinding.inflate(context.inflater, null, false)

    var codeType = CodeType.Barcode

    private val spacingItemDecorator by lazy {
        linearMarginItemDecoration(
            orientation = LinearLayoutManager.HORIZONTAL,
            margin = MarginItem(
                left = 6.dp.toInt(),
                right = 6.dp.toInt()
            )
        )
    }

    private val snapHelper by lazy {
        LinearSnapHelper()
    }

    fun show(items: List<DummyChildClass>) {
        dialog = BottomSheetTray.newInstance(
            title = context.getString(UIKitR.string.use_i_kupon),
            contentLayout = binding.root
        ).also {
            it.show(context.supportFragmentManager, javaClass.simpleName)
        }.apply {
            this.shouldShowClose = true
            this.titleDividerVisibility = true
            this.delegate = object : BottomTrayDelegate {
                override fun onShow(dialogInterface: DialogInterface) {
                    this@GuidelinesIKuponBottomTray.binding.rvCoupon.apply {
                        this.layoutManager = object : LinearLayoutManager(context, HORIZONTAL, false) {
                            override fun canScrollHorizontally(): Boolean {
                                return if (items.size > 1) super.canScrollHorizontally() else false
                            }
                        }
                        this.adapter = this@GuidelinesIKuponBottomTray.adapter.apply {
                            this.items = items
                        }
                        this.removeItemDecoration(spacingItemDecorator)
                        this.addItemDecoration(spacingItemDecorator)

                        snapHelper.attachToRecyclerView(this)
                    }
                    this@GuidelinesIKuponBottomTray.binding.tvDescription.text = generateLoremIpsum(65)

                    if (codeType == CodeType.QR || codeType == CodeType.Barcode) {
                        dialog?.window?.setScreenBrightness(1f)
                    }
                }

                override fun onDismiss(dialogInterface: DialogInterface) {
                    if (codeType == CodeType.QR || codeType == CodeType.Barcode) {
                        dialog?.window?.resetScreenBrightness()
                    }
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {}

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            }
        }

    }

    private val adapter = multiTypeAdapter(
        diffCallback = object : DiffUtil.ItemCallback<DummyChildClass>() {
            override fun areItemsTheSame(oldItem: DummyChildClass, newItem: DummyChildClass) =  oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: DummyChildClass, newItem: DummyChildClass) = oldItem == newItem
        },
        viewTypeConfig = {
            codeType.ordinal
        },
        bindingConfig = {
            registerViewType(
                viewType = CodeType.Barcode.ordinal,
                bindingInflater = { layoutInflater, viewGroup, attachToParent ->
                    ItemIcouponBarcodeBinding.inflate(layoutInflater, viewGroup, attachToParent)
                },
                bind = { position, itemBinding, item ->
                    (itemBinding as ItemIcouponBarcodeBinding).apply {
                        if (items.size > 1) {
                            itemBinding.root.setAsMultiItem()
                        }

                        Glide.with(context)
                            .load(item.couponImage)
                            .into(this.ivCouponImage)

                        Glide.with(context)
                            .load(codeType.image)
                            .into(this.ivBarcode)

                        this.tvCouponName.text = item.couponName
                        this.tvExpiredIn.text = "Hingga ${(1..30).random()} Nov 2024"
                        this.tvBarcodeCode.text = item.code
                    }
                }
            )

            registerViewType(
                viewType = CodeType.QR.ordinal,
                bindingInflater = { layoutInflater, viewGroup, attachToParent ->
                    ItemIcouponQrBinding.inflate(layoutInflater, viewGroup, attachToParent)
                },
                bind = { position, itemBinding, item ->
                    (itemBinding as ItemIcouponQrBinding).apply {
                        if (items.size > 1) {
                            itemBinding.root.setAsMultiItem()
                        }

                        Glide.with(context)
                            .load(item.couponImage)
                            .into(this.ivCouponImage)

                        Glide.with(context)
                            .load(codeType.image)
                            .into(this.ivBarcode)

                        this.tvCouponName.text = item.couponName
                        this.tvExpiredIn.text = "Hingga ${(1..30).random()} Nov 2024"
                        this.tvBarcodeCode.text = item.code
                    }
                }
            )

            registerViewType(
                viewType = CodeType.Code.ordinal,
                bindingInflater = { layoutInflater, viewGroup, attachToParent ->
                    ItemIcouponCodeBinding.inflate(layoutInflater, viewGroup, attachToParent)
                },
                bind = { position, itemBinding, item ->
                    (itemBinding as ItemIcouponCodeBinding).apply {
                        if (items.size > 1) {
                            itemBinding.root.setAsMultiItem()
                        }

                        Glide.with(context)
                            .load(item.couponImage)
                            .into(this.ivCouponImage)

                        this.tvCouponName.text = item.couponName
                        this.tvExpiredIn.text = "Hingga ${(1..30).random()} Nov 2024"
                        this.tvBarcodeCode.text = item.code
                    }
                }
            )
        },
        onViewDetachedFromWindow = { holder ->
            holder.itemView.clearAnimation()
        }
    )


    private fun MaterialCardView.setAsMultiItem() {
        this.cardElevation = 0f
        this.strokeWidth = 2.dp.toInt()
        this.strokeColor = context.color(UIKitR.color.primary_30)

        val itemWidth = (context.deviceWidth * CARD_WIDTH_PERCENT).toInt()
        this.updateLayoutParams<ViewGroup.LayoutParams> {
            width = itemWidth
        }
    }

    enum class CodeType(val image: String?) {
        Barcode(
            "data:image/gif;base64,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"
        ),
        QR(
            "data:image/gif;base64,R0lGODlhVABUAPcAAAAAAAAAMwAAZgAAmQAAzAAA/wArAAArMwArZgArmQArzAAr/wBVAABVMwBVZgBVmQBVzABV/wCAAACAMwCAZgCAmQCAzACA/wCqAACqMwCqZgCqmQCqzACq/wDVAADVMwDVZgDVmQDVzADV/wD/AAD/MwD/ZgD/mQD/zAD//zMAADMAMzMAZjMAmTMAzDMA/zMrADMrMzMrZjMrmTMrzDMr/zNVADNVMzNVZjNVmTNVzDNV/zOAADOAMzOAZjOAmTOAzDOA/zOqADOqMzOqZjOqmTOqzDOq/zPVADPVMzPVZjPVmTPVzDPV/zP/ADP/MzP/ZjP/mTP/zDP//2YAAGYAM2YAZmYAmWYAzGYA/2YrAGYrM2YrZmYrmWYrzGYr/2ZVAGZVM2ZVZmZVmWZVzGZV/2aAAGaAM2aAZmaAmWaAzGaA/2aqAGaqM2aqZmaqmWaqzGaq/2bVAGbVM2bVZmbVmWbVzGbV/2b/AGb/M2b/Zmb/mWb/zGb//5kAAJkAM5kAZpkAmZkAzJkA/5krAJkrM5krZpkrmZkrzJkr/5lVAJlVM5lVZplVmZlVzJlV/5mAAJmAM5mAZpmAmZmAzJmA/5mqAJmqM5mqZpmqmZmqzJmq/5nVAJnVM5nVZpnVmZnVzJnV/5n/AJn/M5n/Zpn/mZn/zJn//8wAAMwAM8wAZswAmcwAzMwA/8wrAMwrM8wrZswrmcwrzMwr/8xVAMxVM8xVZsxVmcxVzMxV/8yAAMyAM8yAZsyAmcyAzMyA/8yqAMyqM8yqZsyqmcyqzMyq/8zVAMzVM8zVZszVmczVzMzV/8z/AMz/M8z/Zsz/mcz/zMz///8AAP8AM/8AZv8Amf8AzP8A//8rAP8rM/8rZv8rmf8rzP8r//9VAP9VM/9VZv9Vmf9VzP9V//+AAP+AM/+AZv+Amf+AzP+A//+qAP+qM/+qZv+qmf+qzP+q///VAP/VM//VZv/Vmf/VzP/V////AP//M///Zv//mf//zP///wAAAAAAAAAAAAAAACH5BAEAAPwALAAAAABUAFQAAAj/AAEIHEiwoMGD+xLuOwhAocOFAx0ynEixokWDEhE+TEgw48WPIC16LLiRY0SFIVOqJIlS48aOLVdWLEnzIcyXJ3EKHFmzJ0SMPmnetJmT6M6YDYOWZKh0aVGeTo+alNoU6dCqP5Pq1GqU69WqTK26zAp1qteiQ0WKparWbFmySN+2zZp2Zty7bvHCNRuWL1q7eQPvHVyX4sjCE+U+1avY8NqzjgV/Fdw48ePDYxELXSwZMN2/kQlzHly572e2nhHPLZ25dWjVnlkD9Yv6NeiLm2tbpg15t+iPuXubNo11Mtauxy8nJ738bfGxz6k2dw4WenXp0ylfl+lbs17u4HF//7+NObx58uN1n1/fu3J57tmbro5Pv/n8+vi3E8/PP+j9/iEFJ1t7UXW3koDpsaQdb7ABV+CA1DF4W4APJujdVsMdyJiF6EmInYcGfvibgiPuB2KGIjZIIIjBUbhgiR2eRqJ7BaI4o082vhehjCpGR2KPG7KonHyuCRfje0ASORuMS7qI3JDHGWfjf0lGOdqUsXEoon9XFknlhKNxmaJ1ndnmYJlctdilel6mdmSNaxrZJEg09mQikz/SmaCYaean4p1zGUffn2Ty+CZ+hC6J5KH1JXqjkCdqqGSfkW75mEw+qhmapvBtx+mdi6qUKZybVpicoJ7i2KaldoaZaqtzKpn66nSo8qbjcrUqlSuWbMZK6Yg+Bgqmrwi++GmRoa74pLGkhihnnmPqFqybvUL77K1oYovnhTJqy22Ol8bIq7JVOgohlN8iG660QRrKKnNbJVtnV6C2O2C60fpWbE3m7tnsrq4yC6uyfEoZZ677IgzljvUOvCOn+2o7qsD0xnkseBI3O2+yTjbcrb8YhpdxyOy+yN61qhLbbkAAOw=="
        ),
        Code (null)
    }

    companion object {
        private const val CARD_WIDTH_PERCENT = 0.87
    }
}