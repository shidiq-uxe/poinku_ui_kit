package id.co.edtslib.uikit.poinku.boarding.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.co.edtslib.uikit.poinku.R
import id.co.edtslib.uikit.poinku.boarding.Boarding
import id.co.edtslib.uikit.poinku.databinding.ItemBoardingContentBinding
import id.co.edtslib.uikit.poinku.utils.drawable
import id.co.edtslib.uikit.poinku.utils.inflater

class BoardingAdapter : RecyclerView.Adapter<BoardingAdapter.BoardingVH>() {

    var list: List<Boarding> = emptyList()
        private set

    private val wrappedList: List<Boarding>
        get() = if (list.size > 1) {
            val first = list.first()
            val last = list.last()
            listOf(last) + list + first
        } else {
            list
        }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Boarding>) {
        list = items
        notifyDataSetChanged()
    }

    fun getRealCount(): Int = list.size

    fun getRealPosition(position: Int): Int {
        return when {
            list.size <= 1 -> position
            position == 0 -> list.size - 1
            position == wrappedList.lastIndex -> 0
            else -> position - 1
        }
    }

    fun getStartPosition(): Int = if (list.size > 1) 1 else 0

    override fun getItemCount(): Int = wrappedList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardingVH {
        val binding = ItemBoardingContentBinding.inflate(
            parent.context.inflater,
            parent,
            false
        )
        return BoardingVH(binding)
    }

    override fun onBindViewHolder(holder: BoardingVH, position: Int) {
        holder.bind(position)
    }

    inner class BoardingVH(private val binding: ItemBoardingContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val realPosition = getRealPosition(position)
            val item = list[realPosition]

            binding.bindViewWithData(item, realPosition)
        }

        private fun ItemBoardingContentBinding.bindViewWithData(item: Boarding, position: Int) {
            val context = root.context

            val image = item.image?.let { image ->
                when (image) {
                    is Int -> context.drawable(image)
                    is String -> if (image.startsWith("http")) image
                    else context.resources.getIdentifier(image, "drawable", context.packageName)
                    else -> image
                }
            }

            if (context.isValidForGlide()) {
                Glide.with(binding.root.context)
                    .load(image)
                    .into(binding.root)
            }
        }

        fun Context.isValidForGlide(): Boolean {
            return when (this) {
                is Activity -> !this.isFinishing && !this.isDestroyed
                is ContextWrapper -> baseContext.isValidForGlide()
                else -> true
            }
        }
    }
}


