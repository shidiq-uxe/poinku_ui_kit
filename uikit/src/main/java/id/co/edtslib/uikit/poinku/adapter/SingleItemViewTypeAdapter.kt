package id.co.edtslib.uikit.poinku.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import id.co.edtslib.uikit.poinku.databinding.ItemGridSkeletonPoinkuIcouponBinding
import id.co.edtslib.uikit.poinku.utils.inflater

class SingleItemViewTypeAdapter<T : Any, B : ViewBinding>(
    private val register: Register<T, B>,
    private val diff: Diff<T>,
    private val bindingFactory: (LayoutInflater, ViewGroup, Boolean) -> B,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffUtil = AsyncListDiffer(this, object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = diff.areItemsTheSame(oldItem, newItem)
        override fun areContentsTheSame(oldItem: T, newItem: T) = diff.areContentsTheSame(oldItem, newItem)
    })

    private val viewTypeRegistry = mutableMapOf<Int, ViewType<T, B>>()

    var shimmerMode: Boolean = false
    var paginationState: PaginationState = PaginationState.Complete
    var items: List<T>
        get() = diffUtil.currentList
        set(value) = diffUtil.submitList(value)

    override fun getItemViewType(position: Int): Int {
        return when {
            shimmerMode -> SHIMMER_VIEW_TYPE
            paginationState == PaginationState.Loading && position == items.size -> PAGINATION_VIEW_TYPE
            else -> register.viewTypeResolver?.invoke(items[position], position) ?: DEFAULT_VIEW_TYPE
        }
    }

    @Suppress("KotlinConstantConditions")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = parent.context.inflater
        return when (viewType) {
            SHIMMER_VIEW_TYPE -> ShimmerViewHolder(bindingFactory(inflater, parent, false))
            PAGINATION_VIEW_TYPE -> PaginationViewHolder(bindingFactory(inflater, parent, false))
            DEFAULT_VIEW_TYPE -> BaseViewHolder(bindingFactory(inflater, parent, false))
            else -> {
                val viewTypeHandler = if (viewType != DEFAULT_VIEW_TYPE || viewType != PAGINATION_VIEW_TYPE || viewType != SHIMMER_VIEW_TYPE) {
                    viewTypeRegistry[viewType]
                } else {
                    throw IllegalArgumentException("View type should not be : $DEFAULT_VIEW_TYPE or $PAGINATION_VIEW_TYPE or $SHIMMER_VIEW_TYPE")
                } ?: throw IllegalArgumentException("Unknown view type: $viewType")

                BaseViewHolder(viewTypeHandler.bindingFactory(inflater, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            SHIMMER_VIEW_TYPE -> (holder as? SingleItemViewTypeAdapter<*, *>.ShimmerViewHolder)?.bind()
            PAGINATION_VIEW_TYPE -> (holder as? SingleItemViewTypeAdapter<*, *>.PaginationViewHolder)?.bind(paginationState)
            else -> (holder as? SingleItemViewTypeAdapter<*, *>.BaseViewHolder)?.bind(items[position])
        }
    }


    override fun getItemCount(): Int {
        return when {
            shimmerMode -> SHIMMER_ITEM_COUNT
            paginationState == PaginationState.Loading -> items.size + 1
            else -> items.size
        }
    }

    inner class BaseViewHolder(private val viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: Any) {
            @Suppress("UNCHECKED_CAST")
            (viewBinding as? B)?.let {
                (item as? T)?.let {
                    register.onBindViewHolder(adapterPosition, item, viewBinding)
                    viewBinding.root.setOnClickListener {
                        register.onItemClick?.invoke(viewBinding, item, adapterPosition)
                    }
                }
            }
        }
    }

    inner class PaginationViewHolder(private val viewBinding: B) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(state: PaginationState) {
            register.onBindPaginationState?.invoke(state, viewBinding)
        }
    }

    inner class ShimmerViewHolder(private val viewBinding: B) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind() {
            register.onBindShimmer?.invoke(viewBinding)
        }
    }

    fun addViewType(type: Int, factory: ViewType<T, B>) {
        viewTypeRegistry[type] = factory
    }

    data class Register<T, B : ViewBinding>(
        val onBindViewHolder: (position: Int, item: T, binding: B) -> Unit = { _,_,_ -> },
        val onBindPaginationState: ((state: PaginationState, binding: B) -> Unit)? = null,
        val onBindShimmer: ((binding: B) -> Unit)? = null,
        val viewTypeResolver: ((item: T, position: Int) -> Int)? = null,
        val onItemClick: ((binding: B, item: T, position: Int) -> Unit)? = null
    )

    data class Diff<T>(
        val areContentsTheSame: (old: T, new: T) -> Boolean,
        val areItemsTheSame: (old: T, new: T) -> Boolean
    )

    data class Params(
        val widthPercent: Double = 0.0,
        val heightPercent: Double = 0.0,
        val margin: Margin = Margin()
    ) {
        data class Margin(val top: Int = 0, val left: Int = 0, val right: Int = 0, val bottom: Int = 0)
    }

    data class ViewType<T, B : ViewBinding>(
        val bindingFactory: (LayoutInflater, ViewGroup, Boolean) -> B,
        val onBind: (item: T, binding: B) -> Unit
    )

    enum class PaginationState { Loading, Error, Complete }

    companion object {
        private const val SHIMMER_VIEW_TYPE = -1
        private const val PAGINATION_VIEW_TYPE = -2
        private const val DEFAULT_VIEW_TYPE = 0
        private const val SHIMMER_ITEM_COUNT = 5
    }
}

inline fun <T : Any, reified B : ViewBinding> singleItemViewTypeAdapter(
    diff: SingleItemViewTypeAdapter.Diff<T>,
    noinline onBindViewHolder: (position: Int, item: T, binding: B) -> Unit = { _, _, _ -> },
    itemList: List<T> = emptyList(),
    noinline onItemClick: ((binding: B, item: T, position: Int) -> Unit)? = null,
    noinline onBindPaginationState: ((state: SingleItemViewTypeAdapter.PaginationState, binding: B) -> Unit)? = null,
    noinline onBindShimmer: ((binding: B) -> Unit)? = null,
    noinline viewTypeResolver: ((item: T, position: Int) -> Int)? = null,
    shimmerMode: Boolean = false,
    paginationState: SingleItemViewTypeAdapter.PaginationState = SingleItemViewTypeAdapter.PaginationState.Complete
): SingleItemViewTypeAdapter<T, B> {
    return SingleItemViewTypeAdapter(
        register = SingleItemViewTypeAdapter.Register(
            onBindViewHolder = onBindViewHolder,
            onBindPaginationState = onBindPaginationState,
            onBindShimmer = onBindShimmer,
            viewTypeResolver = viewTypeResolver,
            onItemClick = onItemClick
        ),
        diff = diff,
        bindingFactory = { inflater, parent, attachToParent ->
            B::class.java.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            ).invoke(null, inflater, parent, attachToParent) as B
        }
    ).apply {
        this.shimmerMode = shimmerMode
        this.paginationState = paginationState
        if (itemList.isNotEmpty()) items = itemList
    }
}

fun skeletonAdapter(
    skeletonCount: Int = 5,
    onBindHolder: (position: Int, binding: ItemGridSkeletonPoinkuIcouponBinding) -> Unit = { _,_ -> }
) = singleItemViewTypeAdapter<Any, ItemGridSkeletonPoinkuIcouponBinding>(
    diff = SingleItemViewTypeAdapter.Diff(
        areItemsTheSame = { old, new -> old == new },
        areContentsTheSame = { old, new -> old == new }
    ),
    onBindViewHolder = { position, _, binding ->
        onBindHolder.invoke(position, binding)
        binding.root.startShimmer()
    },
    itemList = List(skeletonCount) {}
)

