package id.co.edtslib.uikit.poinku.chip

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import id.co.edtslib.uikit.poinku.utils.MarginItem
import id.co.edtslib.uikit.poinku.utils.color
import id.co.edtslib.uikit.poinku.utils.dp
import id.co.edtslib.uikit.poinku.utils.linearMarginItemDecoration
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.utils.dimen

class BucketChipGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var delegate: BucketChipGroupDelegate? = null

    private val chipGroupAdapter = BucketChipGroupAdapter()
    var items: List<BucketData> = chipGroupAdapter.items
        set(value) {
            field = value
            chipGroupAdapter.updateItems(value)
        }

    private val spacingItemDecorator = linearMarginItemDecoration(
        orientation = LinearLayoutManager.HORIZONTAL,
        margin = MarginItem(left = 4.dp.toInt(), right = 4.dp.toInt())
    )

    private val snapHelper = LinearSnapHelper()
    private var isSnapping = false
    var isUserScrolling = false


    init {
        initView()
    }

    private fun initView() {
        clipToPadding = false
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        adapter = chipGroupAdapter
        spacingItemDecorator.takeIf { itemDecorationCount == 0 }?.let {
            addItemDecoration(it)
        }
    }

    fun setSelectedPosition(position: Int, fromScroll: Boolean = false) {
        if (chipGroupAdapter.selectedPosition == position) return

        val previousSelected = chipGroupAdapter.selectedPosition
        chipGroupAdapter.selectedPosition = position
        notifySelectionChange(previousSelected, position)

        if (!fromScroll) {
            isUserScrolling = false // Ensure that clicks are not overridden
            snapToPosition(position)
        }
    }




    fun snapToPosition(position: Int) {
        if (isSnapping) return
        (layoutManager as? LinearLayoutManager)?.let { lm ->
            val targetView = lm.findViewByPosition(position) ?: run {
                smoothScrollToPosition(position)
                postDelayed({ snapToPosition(position) }, 150)
                return
            }
            snapHelper.calculateDistanceToFinalSnap(lm, targetView)?.let { distance ->
                if (distance[0] != 0 || distance[1] != 0) {
                    isSnapping = true
                    smoothScrollBy(distance[0], distance[1])
                    postDelayed({ isSnapping = false }, 300)
                }
            }
        }
    }

    private fun notifySelectionChange(previous: Int, current: Int) {
        if (previous != NO_POSITION) {
            chipGroupAdapter.notifyItemChanged(previous, "selection")
        }
        chipGroupAdapter.notifyItemChanged(current, "selection")
    }



    private inner class BucketChipGroupAdapter : Adapter<BucketChipGroupAdapter.ViewHolder>() {

        var items: List<BucketData> = emptyList()
            private set

        var selectedPosition: Int = 0

        fun updateItems(newItems: List<BucketData>) {
            items = newItems
            selectedPosition = 0
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(BucketChip(context))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position], position == selectedPosition)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
            if (payloads.isNotEmpty() && payloads.contains("selection")) {
                holder.toggleSelection(position == selectedPosition)
            } else {
                onBindViewHolder(holder, position)
            }
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(private val chip: BucketChip) : RecyclerView.ViewHolder(chip) {
            fun bind(item: BucketData, isSelected: Boolean) {
                chip.apply {
                    stampCount = item.bucketStampCount
                    image = item.bucketImage
                    toggleSelected(isSelected)
                    chipCard.setOnClickListener {
                        if (!isUserScrolling) {
                            delegate?.onBucketChipClicked(adapterPosition, item)
                            setSelectedPosition(adapterPosition, fromScroll = false)
                        }
                    }

                }
            }

            fun toggleSelection(isSelected: Boolean) {
                chip.toggleSelected(isSelected)
            }
        }
    }

    private fun BucketChip.toggleSelected(isChecked: Boolean) {
        val strokeColor = context.color(if (isChecked) R.color.primary_30 else R.color.grey_30)
        val strokeWidth = context.dimen(if (isChecked) R.dimen.stroke_width_medium else R.dimen.stroke_width_normal).toInt()
        val textColor = context.color(if (isChecked) R.color.primary_30 else R.color.grey_60)

        chipCard.strokeColor = strokeColor
        chipCard.strokeWidth = strokeWidth
        this.textColor = textColor
    }
}






