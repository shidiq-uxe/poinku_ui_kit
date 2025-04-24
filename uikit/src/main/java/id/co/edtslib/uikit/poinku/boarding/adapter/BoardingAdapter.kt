package id.co.edtslib.uikit.poinku.boarding.adapter

import android.view.Gravity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import id.co.edtslib.uikit.poinku.adapter.SingleItemViewTypeAdapter
import id.co.edtslib.uikit.poinku.adapter.singleItemViewTypeAdapter
import id.co.edtslib.uikit.poinku.boarding.Boarding
import id.co.edtslib.uikit.poinku.boarding.ContentAlignment
import id.co.edtslib.uikit.poinku.databinding.ItemBoardingContentBinding
import id.co.edtslib.uikit.poinku.utils.horizontalBias
import kotlin.text.get

object BoardingAdapter {

    var circular = false
    var contentAlignment: ContentAlignment = ContentAlignment.Center()

    fun boardingAdapter(): SingleItemViewTypeAdapter<Boarding, ItemBoardingContentBinding> =
        singleItemViewTypeAdapter(
            diff = SingleItemViewTypeAdapter.Diff(
                areContentsTheSame = { old, new -> old == new },
                areItemsTheSame = { old, new -> old == new }
            ),
            onBindViewHolder = { position, item, itemBinding ->
                itemBinding.bindViewWithData(item, position)
                itemBinding.adjustIndicatorAlignment()
            }
        )

    private val boardingAdapter get() = boardingAdapter()

    fun getRealPosition(position: Int): Int {
        return if (circular && boardingAdapter.items.size > 1) {
            position % boardingAdapter.items.size
        } else {
            position
        }
    }

    fun getFakePosition(position: Int): Int {
        return if (circular && boardingAdapter.items.size > 1) {
            (boardingAdapter.itemCount / 2 - (boardingAdapter.itemCount /2) % boardingAdapter.items.size)+position
        } else {
            position
        }
    }

    fun getInitialPosition(canBackOnFirstPosition: Boolean): Int {
        return if (circular && boardingAdapter.items.size > 1) {
            if (canBackOnFirstPosition) {
                boardingAdapter.itemCount / 2 - (boardingAdapter.itemCount /2) % boardingAdapter.items.size
            } else {
                0
            }
        } else {
            0
        }
    }

    private fun ItemBoardingContentBinding.bindViewWithData(item: Boarding, position: Int) {
        val context =
            if (root.context is FragmentActivity) root.context as FragmentActivity else root.context

        // Safeguard against destroyed activity or invalid context
        val activity = (context as? FragmentActivity)

        if (activity != null && (activity.isDestroyed || activity.isFinishing)) {
            // If the activity is destroyed, return early to avoid Glide loading issues
            return
        }

        val image  = item.image?.let { image ->
            when(image) {
                is Int -> context.getDrawable(image)
                is String -> if (image.startsWith("http")) {
                    image
                } else {
                    context.resources.getIdentifier(
                        image, "drawable",
                        context.packageName
                    )
                }
                else -> image
            }
        }

        Glide.with(context).load(image).into(ivBoardingIll)

        tvBoardingTitle.text = item.title
        tvBoardingDescription.text = item.description
    }

    private fun ItemBoardingContentBinding.adjustIndicatorAlignment() {
        when(contentAlignment) {
            is ContentAlignment.Start -> {
                ivBoardingIll.horizontalBias(0f)
                tvBoardingTitle.gravity = Gravity.START
                tvBoardingDescription.gravity = Gravity.START
            }
            is ContentAlignment.Center -> {
                ivBoardingIll.horizontalBias(0.5f)
                tvBoardingTitle.gravity = Gravity.CENTER
                tvBoardingDescription.gravity = Gravity.CENTER
            }
            is ContentAlignment.End -> {
                ivBoardingIll.horizontalBias(1f)
                tvBoardingTitle.gravity = Gravity.END
                tvBoardingDescription.gravity = Gravity.END
            }
        }
    }
}