package id.co.edtslib.uikit.poinku.recyclerView

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.adapter.SingleItemViewTypeAdapter
import id.co.edtslib.uikit.poinku.utils.MarginItem
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.linearMarginItemDecoration

// Todo : Refactor the class onto using Material Carousel to Improve its performance | do during AGP Update
abstract class HorizontalSlidingRecyclerView<T : Any, B : ViewBinding> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var carouselAdapter: SingleItemViewTypeAdapter<T, B>? = null
        set(value) {
            field = value

            adapter = value
        }

    var itemSpacing: Int = 0.dp.toInt()
        set(value) {
            field = value

            if (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }

            addItemDecoration(itemSpacingDecorator(value))
        }

    var shouldSnap = false
        set(value) {
            field = value

            if (value) {
                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(this)
            }
        }

    init {
        /*layoutManager = CarouselLayoutManager().apply {
            val strategy = MultiBrowseCarouselStrategy()

            setCarouselStrategy(strategy)
        }*/

        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)

        clipToPadding = false
        clipChildren = false

        adapter = carouselAdapter

        // Initialize from XML attributes if provided
        context.theme.obtainStyledAttributes(attrs, R.styleable.HorizontalCarouselRecyclerView, 0, 0).use {
            itemSpacing = it.getDimensionPixelSize(R.styleable.HorizontalCarouselRecyclerView_itemSpacing, 0)
            addItemDecoration(itemSpacingDecorator(itemSpacing))
        }
    }

    var items: List<T> = carouselAdapter?.items.orEmpty()
        set(value) {
            field = value

            carouselAdapter?.items = value
        }

    private fun itemSpacingDecorator(itemSpacing: Int) = linearMarginItemDecoration(
        orientation = LinearLayoutManager.HORIZONTAL,
        margin = MarginItem(
            left = itemSpacing.div(2),
            right = itemSpacing.div(2)
        )
    )
}
